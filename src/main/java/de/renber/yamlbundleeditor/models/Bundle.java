package de.renber.yamlbundleeditor.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes a single ResourceBundle (values & meta information) 
 */
public class Bundle {

	private BundleMetaInfo meta;
	private Map<String, String> values;
	
	public Bundle(BundleMetaInfo meta, Map<String, String> values) {
		if (meta == null)
			throw new IllegalArgumentException("Parameter meta must not be null");
		if (values == null)
			throw new IllegalArgumentException("Parameter values must not be null");
		
		this.meta = meta;
		this.values = new HashMap<String, String>(values);
	}
	
	public BundleMetaInfo getMeta() {
		return meta;
	}
	
	public Map<String, String> getValues() {
		return values;
	}
	
}
