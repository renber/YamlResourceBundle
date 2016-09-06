package de.renber.yamlbundleeditor.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single resource entry with localized values
 * @author renber
 *
 */
public class ResourceKey {

	public String name;
	public String comment;
		
	List<LocalizedValue> values = new ArrayList<>();
	
	List<ResourceKey> children = new ArrayList<>();
	
	public List<ResourceKey> getChildren() {
		return children;
	}	
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	public List<LocalizedValue> getLocalizedValues() {
		return values;
	}
	
	public Object getLocalizedValue(String languageIdentifier) {
		languageIdentifier = languageIdentifier.toLowerCase();
		
		for(LocalizedValue v: values) {
			if (v.languageCode.toLowerCase().equals(languageIdentifier))
				return v.value;
		}
		
		return null;
	}
}
