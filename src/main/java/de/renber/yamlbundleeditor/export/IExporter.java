package de.renber.yamlbundleeditor.export;

import java.awt.Composite;
import java.io.OutputStream;

import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;

public interface IExporter {
	
	/**
	 * Export the given BundleCollection to the stream using the file type (which has been retrieved through getSupportedFileTypes())
	 */
	void export(OutputStream stream, BundleCollection collection, IExportConfiguration configuration) throws ExportException;
	
	/**
	 * Return a configuration for this exporter with default values
	 */
	IExportConfiguration getDefaultConfiguration();
	
	/**
	 * Return an SWT composite which allows to change the given configuration for the given collection
	 */
	Composite getConfigurationComposite(BundleCollection collection, IExportConfiguration configuration);
	
	/**
	 * Serialize the given configuration to a string
	 */
	public String serializeConfiguration(IExportConfiguration configuration);
	
	/**
	 * Deserialize a configuration from the given string (previously retrieved through serializeConfiguration())
	 */
	public IExportConfiguration deserializeConfiguration(String serializedString);
}
