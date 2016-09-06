package de.renber.yamlbundleeditor.models;

/**
 * Represents a localized version of a single resource key value
 * @author renber
 */
public class LocalizedValue {

	public String languageCode;
	public Object value;
	
	public LocalizedValue(String languageCode, String value) {
		this.languageCode = languageCode;
		this.value = value;
	}
}
