package de.renber.yamlbundleeditor.redoundo;

import java.util.ArrayList;
import java.util.List;

import de.renber.yamlbundleeditor.services.ILocalizationService;

/**
 * An undoable action which is a container for multiple other undoable actions
 * @author renber
 */
public class CompoundAction implements IUndoableAction {

	String description;
	
	List<IUndoableAction> elements = new ArrayList<>();
	
	public CompoundAction(String description) {
		this.description = description;
	}
	
	public void addAction(IUndoableAction action) {
		elements.add(action);
	}
	
	@Override
	public void undo() throws RedoUndoException {
		// undo as FILO
		for(int i = elements.size() - 1; i >= 0; i--)
			elements.get(i).undo();
		
	}

	@Override
	public void redo() throws RedoUndoException {
		// redo as FIFO
		for(IUndoableAction action: elements)
			action.redo();
	}

	@Override
	public boolean canMerge(IUndoableAction action) {
		return false;
	}

	@Override
	public IUndoableAction merge(IUndoableAction action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getActionDescription(ILocalizationService loc) {
		return "Compound Action";
	}

}
