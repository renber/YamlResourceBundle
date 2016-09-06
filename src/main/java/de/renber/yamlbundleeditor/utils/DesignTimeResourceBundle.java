package de.renber.yamlbundleeditor.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * ResourceBundle for WindowBuilder DesignTime which just returns the
 * key names in braces as values
 * @author berre
 *
 */
public class DesignTimeResourceBundle extends ResourceBundle {

	@Override
	public boolean containsKey(String key) {
		return true;
	}
	
	@Override
	protected Object handleGetObject(String key) {
		// for hierarchical keys only show the last part
		String[] parts = key.split(":");		
		return "{" + parts[parts.length - 1] +"}";
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.emptyEnumeration();
	}

}
