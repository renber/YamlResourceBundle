package de.renber.yamlbundleeditor.exporters;

/**
 * Exception which can be thrown by an Exporter
 */
public class ExportException extends Exception {

	public ExportException(String message, Exception innerException) {
		super(message, innerException);
	}
	
	public ExportException(String message) {
		super(message);
	}	
}
