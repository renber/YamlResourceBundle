package de.renber.yamlbundleeditor.redoundo;

import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.viewmodels.datatypes.ResourceKeyViewModel;

/**
 * Undoable action which describes that a key was moved / renamed
 * @author renber
 */
public class KeyMoveAction implements IUndoableAction {

	ResourceKeyViewModel key;
	String oldPath;
	String newPath;
	
	/**
	 * Undoable action which indicates that the given key has been moved / renamed
	 * @param key THe affected key
	 * @param oldPath The key's original path
	 * @param newPath The key's new path
	 */
	public KeyMoveAction(ResourceKeyViewModel key, String oldPath, String newPath) {
		this.key = key;
		this.oldPath = oldPath;
		this.newPath = newPath;
	}
	
	@Override
	public void undo() throws RedoUndoException {
		key.move(oldPath);
	}

	@Override
	public void redo() throws RedoUndoException {
		key.move(newPath);
	}

	@Override
	public boolean canMerge(IUndoableAction action) {
		return false;
	}

	@Override
	public IUndoableAction merge(IUndoableAction action) {
		throw new UnsupportedOperationException("Cannot merge a KeyMoveAction");
	}

	@Override
	public String getActionDescription(ILocalizationService loc) {
		return loc.getString("redoundo:movedkey");
	}

}
