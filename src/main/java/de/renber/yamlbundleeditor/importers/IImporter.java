package de.renber.yamlbundleeditor.importers;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.renber.yamlbundleeditor.exporters.ExportException;
import de.renber.yamlbundleeditor.exporters.IExportConfiguration;
import de.renber.yamlbundleeditor.models.BundleCollection;

public interface IImporter {
	/**
	 * Return the name of this importer
	 */
	public String getName();
	
	/**
	 * Return an image for this importer
	 */
	public Image getImage();
	
	/**
	 * Import data into the given BundleCollection using the given configuration
	 * and return the configuration used to do it
	 */
	public IImportConfiguration doImport(BundleCollection collection, IImportConfiguration configuration) throws ImportException;
	
	/**
	 * Imports data into a new BundleCollection (to be created by the importer)
	 * @param configuration
	 * @return The created collection
	 * @throws ImportException
	 */
	public BundleCollection doImport(IImportConfiguration configuration) throws ImportException;
	
	/**
	 * Return a configuration for this exporter with default values
	 */
	public IImportConfiguration getDefaultConfiguration();	
	
	/**
	 * Serialize the given configuration to a string
	 */
	public String serializeConfiguration(IImportConfiguration configuration);
	
	/**
	 * Deserialize a configuration from the given string (previously retrieved through serializeConfiguration())
	 */
	public IImportConfiguration deserializeConfiguration(String serializedString);
}
