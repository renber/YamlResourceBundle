package de.renber.yamlbundleeditor.importers;

/**
 * Exception which can be thrown by an Importer
 */
public class ImportException extends Exception {

	public ImportException(String message, Exception innerException) {
		super(message, innerException);
	}
	
	public ImportException(String message) {
		super(message);
	}	
}