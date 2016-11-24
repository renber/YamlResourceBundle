package de.renber.yamlbundleeditor.export;

import java.io.OutputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;

public interface IExporter {
	
	/**
	 * Return the name of this exporter
	 */
	public String getName();
	
	/**
	 * Return an image for this exporter
	 */
	public Image getImage();
	
	/**
	 * Export the given BundleCollection using the given configuration)
	 */
	public void export(BundleCollection collection, IExportConfiguration configuration) throws ExportException;
	
	/**
	 * Return a configuration for this exporter with default values
	 */
	public IExportConfiguration getDefaultConfiguration();
	
	/**
	 * Return an SWT composite which allows to change the exporter's configuration
	 * (changes have to be written directly to the passed IExportConfiguration instance)
	 */
	public Control getConfigurationControl(Composite parent, BundleCollection collection, IExportConfiguration configuration);
	
	/**
	 * Serialize the given configuration to a string
	 */
	public String serializeConfiguration(IExportConfiguration configuration);
	
	/**
	 * Deserialize a configuration from the given string (previously retrieved through serializeConfiguration())
	 */
	public IExportConfiguration deserializeConfiguration(String serializedString);
}
