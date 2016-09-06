package de.renber.yamlbundleeditor.services;

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;

/**
 * Helper class to load image resources
 * @author renber
 */
public class IconProvider {

	static final String iconPath = "de/renber/yamlbundleeditor/icons/";
	static final String flagIconPath = "de/renber/yamlbundleeditor/icons/flags/";
	
	private IconProvider() {
		// --
	}

	/**
	 * Return the image with the given name if it exists, otherwise null
	 */
	public static Image getImage(String name) {		
		InputStream resStream = IconProvider.class.getClassLoader().getResourceAsStream(iconPath + name + ".png");
		if (resStream != null)		
			return new Image(null, resStream);
					
		return null;		
	}
	
	public static Image getFlagIcon(String isoCode) {
		InputStream resStream = IconProvider.class.getClassLoader().getResourceAsStream(flagIconPath + isoCode + ".png");
		if (resStream != null)		
			return new Image(null, resStream);
					
		return null;		
	}
	
}
