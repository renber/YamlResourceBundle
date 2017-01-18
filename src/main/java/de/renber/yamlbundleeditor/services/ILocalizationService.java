package de.renber.yamlbundleeditor.services;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Widget;

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
	
	/**
	 * Change the application's language
	 * @param languageCode
	 */
	public void changeLanguage(String languageCode);
	
	/**
	 * Binds the localized value to the given Widget's text property
	 */
	public void localizeWidget(Widget widget, String key);
	
}
