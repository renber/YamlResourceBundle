package de.renber.yamlbundleeditor.redoundo;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.databinding.viewmodels.PropertyChangeSupportBase;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IUndoSupport;

public class DefaultRedoUndoService implements IUndoSupport {

	ICommand undoCommand;
	ICommand redoCommand;

	List<IUndoableAction> actionHistory = new ArrayList<>();
	int currentHistoryPosition = -1;

	boolean executing = false;
	boolean suspended = false;
	
	List<IUndoListener> undoListeners = new ArrayList<IUndoListener>();
	
	ILocalizationService loc;
	
	/**
	 * Maximum count of undoable actions which can be recorderd
	 */
	int maxHistoryLength = 20;

	public DefaultRedoUndoService(ILocalizationService localizationService) {
		
		loc = localizationService;
		
		createCommands();
	}
	
	private void createCommands() {
		undoCommand = new RelayCommand(() -> {

			executing = true;

			try {
				IUndoableAction undoAction = actionHistory.get(currentHistoryPosition);
				
				undoAction.undo();
				currentHistoryPosition--;
				
				raiseActionUndone(undoAction);
			} catch (RedoUndoException e) {
				e.printStackTrace();
			}

			executing = false;								
		},
			() -> currentHistoryPosition >= 0);

		redoCommand = new RelayCommand(() -> {
			executing = true;

			try {								
				currentHistoryPosition++;
				
				IUndoableAction redoAction = actionHistory.get(currentHistoryPosition);
				actionHistory.get(currentHistoryPosition).redo();
				
				raiseActionUndone(redoAction);
			} catch (RedoUndoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			executing = false;			
		},
			() -> currentHistoryPosition <= actionHistory.size() - 2);
	}

	@Override
	public void record(IUndoableAction action) {

		if (!isSuspended() && !isExecuting()) {
			// record a new action			
			
			// all redo items after the current position will be lost
			if (currentHistoryPosition > -1 && currentHistoryPosition < actionHistory.size() - 1)
				actionHistory.subList(currentHistoryPosition + 1, actionHistory.size()).clear();
			
			/*
			 * Trim the list size to the maximum length,
			 * safe one item space for the new action
			 */
			while (actionHistory.size() > maxHistoryLength - 1) {
				actionHistory.remove(0);
				currentHistoryPosition--;
			}
			
			// can this action be merged with the latest one?
			if (currentHistoryPosition >= 0) {
				IUndoableAction latest = actionHistory.get(currentHistoryPosition);
				if (latest.canMerge(action)) {
					action = latest.merge(action);
					actionHistory.remove(latest);
					currentHistoryPosition--;
				}
			}				
			
			actionHistory.add(action);
			currentHistoryPosition++;
			
			raiseActionRecorded(action);
		}
	}
	
	/**
	 * Return the action which can currently be undone (if any)
	 */
	protected IUndoableAction getCurrentUndoAction() {
		if (currentHistoryPosition > -1)
			return actionHistory.get(currentHistoryPosition);
		
		return null;
	}

	/**
	 * Return the action which can currently be redone (if any)
	 */
	protected IUndoableAction getCurrentRedoAction() {
		if (currentHistoryPosition + 1 <= actionHistory.size() - 1)
			return actionHistory.get(currentHistoryPosition + 1);
		
		return null;
	}	
	
	@Override
	public boolean isExecuting() {
		return executing;
	}

	@Override
	public void flush() {
		currentHistoryPosition = -1;
		actionHistory.clear();
	}	

	public boolean isSuspended() {
		return suspended;
	}

	@Override
	public void suspend() {
		suspended = true;
	}

	@Override
	public void resume() {
		suspended = false;
	}

	@Override
	public void addUndoListener(IUndoListener listener) {
		if (!undoListeners.contains(listener))
			undoListeners.add(listener);
	}

	@Override
	public void removeUndoListener(IUndoListener listener) {
		undoListeners.remove(listener);
	}
	
	/**
	 * Calls actionRecorded on all registered undo listeners
	 */
	protected void raiseActionRecorded(IUndoableAction action) {
		for(IUndoListener listener: undoListeners)
			listener.actionRecorded(action);
	}
	
	/**
	 * Calls actionUndone on all registered undo listeners
	 */
	protected void raiseActionUndone(IUndoableAction action) {
		for(IUndoListener listener: undoListeners)
			listener.actionUndone(action);
	}
	
	/**
	 * Calls actionRedone on all registered undo listeners
	 */
	protected void raiseActionRedone(IUndoableAction action) {
		for(IUndoListener listener: undoListeners)
			listener.actionRedone(action);
	}		

	@Override
	public ICommand getUndoCommand() {
		return undoCommand;
	}

	@Override
	public ICommand getRedoCommand() {
		return redoCommand;
	}

	@Override
	public String getUndoDescription() {
		IUndoableAction action = getCurrentUndoAction();
		if (action != null)
			return action.getActionDescription(loc);
		
		return null;
	}

	@Override
	public String getRedoDescription() {
		IUndoableAction action = getCurrentRedoAction();
		if (action != null)
			return action.getActionDescription(loc);
		
		return null;
	}	
}
