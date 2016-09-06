package de.renber.resourcebundles.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.yaml.snakeyaml.Yaml;

import de.renber.resourcebundles.yaml.ext.ImageConstructor;

/**
 * Reads meta-information from a YamlResourceBundle
 * (author, language name, etc.)
 * @author renber
 */
public class YamlBundleMeta {
	/**
	 * ISO code of the language (e.g. en for english, de for german)
	 */
	public String languageCode;		
	/**
	 * Variant of the language (e.g. us for USA, gb for great britain)
	 */
	public String variantName;	
	/**
	 * The English name for the language (e.g. Espanol for Spanish)
	 */
	public String name;
	/**
	 * The name of the language in the language itself (e.g. Deutsch for German)
	 */
	public String localizedName;
	/**
	 * Descriptor of the language author(s)
	 */
	public String author;

	/**
	 * Path of the resource bundle file
	 */
	public String resourcePath;	
	
	/**
	 * The country's flag this localization file represents 
	 */
	public Image flagImage;
	
	/**
	 * Read the contents from the given yaml file from the node 'meta'
	 * @param yamlStream
	 * @return
	 */
	public static YamlBundleMeta fromYaml(InputStream yamlStream) {
		Yaml yaml = new Yaml(new ImageConstructor());		 

		try (Reader reader = new InputStreamReader(yamlStream)) {
			Map<String, Object> map = (Map<String, Object>) yaml.load(reader);
			
			Map<String, Object> metaNode = (Map<String, Object>)map.get("meta");
			
			if (metaNode == null)
				throw new RuntimeException("Yaml does not contain a meta node.");
			
			return fromMap(metaNode);
		} catch (IOException e) {						
			throw new RuntimeException(e);
		}
	}
	
	private static YamlBundleMeta fromMap(Map<String, Object> map) {
		YamlBundleMeta locFile = new YamlBundleMeta();
		
		locFile.languageCode = getFromMap(map, "languageCode", "??");
		locFile.name = getFromMap(map, "language", "unknown");
		locFile.localizedName = getFromMap(map, "localizedLanguage", "unknown");
		locFile.author = getFromMap(map, "author", "unknown author");
		locFile.flagImage = getFromMap(map, "flagImage", (Image)null);
		
		return locFile;
	}
	
	/**
	 * Return the value of key from the given map or defaultValue if the map
	 * does not contain key. Result is cast to type T 
	 */
	private static <T> T getFromMap(Map<String, Object> map, String key, T defaultValue) {
		Object v = map.get(key);
		if (v == null)
			return defaultValue;
		
		return (T)v;
	}
}