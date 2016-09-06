package de.renber.yamlbundleeditor.redoundo;

import java.util.ResourceBundle;

import de.renber.yamlbundleeditor.services.ILocalizationService;

/**
 * An action which is undoable
 * @author renber
 */
public interface IUndoableAction {

	/**
	 * Undo this action
	 */
	public void undo() throws RedoUndoException;
	
	/**
	 * Redo this action
	 */
	public void redo() throws RedoUndoException;
	
	/**
	 * Return whether this action can be merged with the given action
	 */
	public boolean canMerge(IUndoableAction action);
	
	/**
	 * Merge this and the given action to a new one
	 * (only possible if canMerge(...) returns true for the given action)
	 */
	public IUndoableAction merge(IUndoableAction action);
	
	/**
	 * Return a human-readable, localized description of this action (e.g. to display in the UI)
	 */
	public String getActionDescription(ILocalizationService loc);
}
