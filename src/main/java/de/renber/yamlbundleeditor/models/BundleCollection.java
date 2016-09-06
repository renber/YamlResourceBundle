package de.renber.yamlbundleeditor.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one ore more YamlResourceBundles which are used for
 * the localization of an application
 * @author renber
 */
public class BundleCollection {
	
	/**
	 * List of bundles in this collection (e.g. the available localizations)
	 */
	protected List<BundleMetaInfo> bundles = new ArrayList<>();
	
	/**
	 * Hierarchical list of resource values in this collection (values from all bundles combined)
	 */
	protected List<ResourceKey> values = new ArrayList<>();
	
	/**
	 * Return the meta information for all bundles in this collection
	 */
	public List<BundleMetaInfo> getBundles() {
		return bundles;
	}
	
	/**
	 * Return all values declared in the ResourceBundles of this collections
	 */
	public List<ResourceKey> getValues() {
		return values;
	}	
}
