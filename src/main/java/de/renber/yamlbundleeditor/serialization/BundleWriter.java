package de.renber.yamlbundleeditor.serialization;

import java.io.InputStream;
import java.io.OutputStream;

import de.renber.yamlbundleeditor.models.Bundle;
import de.renber.yamlbundleeditor.models.BundleCollection;

/**
 * Interface for classes which serializes a ResourceBundle (values & meta-information)
 * @author renber
 */
public interface BundleWriter {
	public void write(OutputStream stream, BundleCollection bundleCollection, String languageCode);	
}
