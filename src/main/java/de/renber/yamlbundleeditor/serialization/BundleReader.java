package de.renber.yamlbundleeditor.serialization;

import java.io.InputStream;
import java.util.List;

import de.renber.yamlbundleeditor.models.Bundle;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;

/**
 * Interface for classes which deserialize a ResourceBundle (values & meta-information)
 * @author renber
 */
public interface BundleReader {
	
	public Bundle read(InputStream stream);
	
	public BundleMetaInfo readMeta(InputStream stream);	
	
}
