package de.renber.yamlbundleeditor.services.impl;

import java.util.ResourceBundle;

import de.renber.yamlbundleeditor.services.ILocalizationService;

/**
 * Localization service which wraps a ResourceBundle
 * @author renber
 */
public class ResBundleLocalizationService implements ILocalizationService {

	ResourceBundle langBundle;
	
	public ResBundleLocalizationService(ResourceBundle langBundle) {
		this.langBundle = langBundle;			
	}

	@Override
	public String getString(String key) {
		return langBundle.getString(key);
	}
	
	@Override
	public String getString(String key, String...arguments) {
		return String.format(getString(key), arguments);
	}
	
}
