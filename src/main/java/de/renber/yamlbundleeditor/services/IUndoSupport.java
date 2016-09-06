package de.renber.yamlbundleeditor.services;

import de.renber.databinding.commands.ICommand;
import de.renber.yamlbundleeditor.redoundo.IUndoListener;
import de.renber.yamlbundleeditor.redoundo.IUndoableAction;

/**
 * Interface for classes which record actions and can undo/redo them
 * @author renber
 */
public interface IUndoSupport {

	/**
	 * Records the given action and makes it available to be undone
	 */
	public void record(IUndoableAction action);
	
	/**
	 * Clear the history of all actions
	 */
	public void flush();
	
	/**
	 * Indicates whether an action is currently in the process of being undone/redone
	 * If this is true, no new action can be recorded since all changes are regarded part of
	 * the undo/redo process 
	 */
	public boolean isExecuting();

	/**
	 * No more actions will be recorded
	 */
	public void suspend();
	
	/**
	 * Continue to record actions
	 */
	public void resume();	
	
	/**
	 * Add a new undo listener
	 */
	public void addUndoListener(IUndoListener listener);
	
	/**
	 * Remove the given undo listener
	 */
	public void removeUndoListener(IUndoListener listener);	
	
	/**
	 * Return a human-readable description of the action which can be undone
	 */
	public String getUndoDescription();
	
	/**
	 * Return a human-readable description of the action which can be redone
	 */
	public String getRedoDescription();	
	
	/**
	 * The undo command object
	 */
	public ICommand getUndoCommand();
	
	/**
	 * The redo command object
	 */
	public ICommand getRedoCommand();

}
