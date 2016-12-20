package de.renber.yamlbundleeditor.services.impl;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import de.renber.resourcebundles.yaml.YamlResourceBundle;
import de.renber.yamlbundleeditor.Starter;
import de.renber.yamlbundleeditor.services.ILocalizationService;

/**
 * Localization service which wraps a ResourceBundle
 * @author renber
 */
public class DefaultLocalizationService implements ILocalizationService {

	final String yamlPath = "de/renber/yamlbundleeditor/localization/";
	
	List<LocalizedWidget> localizedWidgets = new ArrayList<>();
	
	ResourceBundle langBundle;
	
	public DefaultLocalizationService(String languageCode) {
		changeLanguage(languageCode);		
	}

	@Override
	public String getString(String key) {
		return langBundle.getString(key);
	}
	
	@Override
	public String getString(String key, String...arguments) {
		return String.format(getString(key), arguments);
	}

	@Override
	public void localizeWidget(Widget widget, String key) {
		LocalizedWidget lw = new LocalizedWidget(widget, key);
		lw.update(this);
		localizedWidgets.add(lw);
	}
	
	void updateAll() {
		for(int i = localizedWidgets.size() - 1; i >= 0; i--) {
			if (localizedWidgets.get(i).isValid())
				localizedWidgets.get(i).update(this);
			else
				localizedWidgets.remove(i);
		}
	}

	@Override
	public void changeLanguage(String languageCode) {
		InputStream inStream = DefaultLocalizationService.class.getClassLoader().getResourceAsStream(yamlPath + "lang_" + languageCode + ".yaml");
		if (inStream != null) {
			langBundle = new YamlResourceBundle(inStream);
			updateAll();
		}
	}
}

