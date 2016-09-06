package de.renber.yamlbundleeditor.redoundo;

/**
 * Listener for the IUndoSupport interface
 * @author renber
 */
public interface IUndoListener {

	/**
	 * Called when a new action has been recorded
	 */
	public void actionRecorded(IUndoableAction action);
	
	/**
	 * Called when an action has been undone
	 */
	public void actionUndone(IUndoableAction action);
	
	/**
	 * Called when an action has been redone
	 */
	public void actionRedone(IUndoableAction action);	
}
