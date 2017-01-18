package de.renber.yamlbundleeditor.services.impl;

import java.lang.ref.WeakReference;

import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import de.renber.yamlbundleeditor.services.ILocalizationService;

class LocalizedWidget {
	public WeakReference<Widget> widgetRef;
	public String key;
	
	public LocalizedWidget(Widget widget, String key) {
		widgetRef = new WeakReference<Widget>(widget);
		this.key = key;
	}
	
	public boolean isValid() {
		return widgetRef.get() != null;
	}
	
	public void update(ILocalizationService localizationService) {
		Widget w = widgetRef.get();
		if (w == null)
			return;
		
		WidgetProperties.text().setValue(w, localizationService.getString(key));
	}
}
