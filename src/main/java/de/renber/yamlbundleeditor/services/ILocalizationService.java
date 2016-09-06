package de.renber.yamlbundleeditor.services;

/**
 * Interface for Service which handles resolving localization resources
 * @author renber
 */
public interface ILocalizationService {
	
	/**
	 * Returns the value of the given localization key
	 */
	public String getString(String key);
		
	/**
	 * The value of the given localization key is treated as a format string for
	 * String.format(...) with the given arguments
	 */
	public String getString(String key, String...arguments);
	
}
