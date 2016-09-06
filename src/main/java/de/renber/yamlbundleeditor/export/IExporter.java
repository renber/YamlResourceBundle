package de.renber.yamlbundleeditor.export;

import java.awt.Composite;
import java.io.OutputStream;

import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;

public interface IExporter {

	/**
	 * Return the file types which are supported by this exporter
	 */
	FileExtFilter[] getSupportedFileTypes();
	
	/**
	 * Export the given BundleCollection to the stream using the file type (which has been retrieved through getSupportedFileTypes())
	 */
	void export(OutputStream stream, BundleCollection collection, String exportExtension) throws ExportException;
	
	/**
	 * Return an SWT composite which allows to this exporter's settings, if any or null
	 */
	Composite getConfigurationComposite();
}
