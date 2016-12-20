package de.renber.yamlbundleeditor.services.impl;

import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import de.renber.yamlbundleeditor.services.ILocalizationService;

public class DesignTimeLocalizationService implements ILocalizationService {

	@Override
	public String getString(String key) {
		String[] parts = key.split(":");		
		return "{" + parts[parts.length - 1] +"}";
	}

	@Override
	public String getString(String key, String... arguments) {
		return getString(key);
	}

	@Override
	public void changeLanguage(String languageCode) {
		// no design-time support
	}

	@Override
	public void localizeWidget(Widget widget, String key) {
		WidgetProperties.text().setValue(widget, getString(key));
	}

}
