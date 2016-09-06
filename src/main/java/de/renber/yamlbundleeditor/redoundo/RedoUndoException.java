package de.renber.yamlbundleeditor.redoundo;

/**
 * Exception which is thrown when an error occurs uring undoing/redoing an action
 * @author renber
 */
public class RedoUndoException extends Exception {

	public RedoUndoException(String msg, Exception innerException) {
		super(msg, innerException);
	}	
}
