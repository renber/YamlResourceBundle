package de.renber.yamlbundleeditor.serialization;

import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

public class BundleUtils {

	private BundleUtils() {
		// --
	}
	
	/**
	 * Returns the bundle's basename from the given filename (e.g. Language for Language_de.yaml or Language_en-us.yaml) 
	 */
	public static String deduceBundleBaseName(String filepath) {
		String filename = FilenameUtils.removeExtension(Paths.get(filepath).getFileName().toString());
		
		int uscoreIdx = filename.indexOf("_");
		if (uscoreIdx > -1)
			return filename.substring(0, uscoreIdx);
		else {
			// remove extension
			return filename;
		}			
	}
	
}
