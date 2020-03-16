package de.renber.yamlbundleeditor.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Image;
import org.yaml.snakeyaml.Yaml;

import de.renber.resourcebundles.yaml.YamlBundleMeta;
import de.renber.resourcebundles.yaml.ext.ImageConstructor;
import de.renber.yamlbundleeditor.models.Bundle;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;

/**
 * BundleReader which reads yaml ResourceBundle files
 * @author renber
 *
 */
public class YamlBundleReader implements BundleReader {

	static final String PATH_SEPARATOR = "\t"; 
	
	@Override
	public Bundle read(InputStream stream) {				
		Yaml yaml = new Yaml(new ImageConstructor());		 
		
		try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
			Object yamlDoc = yaml.load(reader);
			if (yamlDoc instanceof Map) {
				Map<String, Object> docMap = (Map<String, Object>)yamlDoc;
				
				 BundleMetaInfo meta = metaFromDoc(docMap);
				 Map<String, String> values = readValues(docMap);
				 
				 return new Bundle(meta, values);
			} else
				throw new RuntimeException("Unsupported yaml file structure");											
		} catch (IOException e) {						
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads only the bundle's meta information from the stream
	 */
	@Override
	public BundleMetaInfo readMeta(InputStream yamlStream) {
		Yaml yaml = new Yaml(new ImageConstructor());		 

		try (Reader reader = new InputStreamReader(yamlStream, StandardCharsets.UTF_8)) {
			Object yamlDoc = yaml.load(reader);
			if (yamlDoc instanceof Map) {
				Map<String, Object> docMap = (Map<String, Object>)yamlDoc;
				return metaFromDoc(docMap);				
			} else
				throw new RuntimeException("Unsupported yaml file structure");											
		} catch (IOException e) {						
			throw new RuntimeException(e);
		}
	}	
	
	/**
	 * Reads the bundle meta information from the given map (content of the meta node)
	 */
	private static BundleMetaInfo metaFromDoc(Map<String, Object> docMap) {
		BundleMetaInfo meta = new BundleMetaInfo();
		
		Map<String, Object> metaNode = (Map<String, Object>)docMap.get("meta");	
		
		if (metaNode == null)
			throw new RuntimeException("Yaml does not contain a meta node.");
		
		meta.isCommentBundle = "comments".equals(getFromMap(metaNode, "type", "resources"));
		meta.languageCode = getFromMap(metaNode, "languageCode", "??");
		meta.name = getFromMap(metaNode, "language", "unknown");
		meta.variantName = getFromMap(metaNode, "variant", "");
		meta.localizedName = getFromMap(metaNode, "localizedLanguage", "unknown");
		meta.author = getFromMap(metaNode, "author", "unknown author");
		meta.flagImage = getFromMap(metaNode, "flagImage", (Image)null);
		
		return meta;
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
	
	private Map<String, String> readValues(Map<String, Object> document) {
		Map<String, String> targetMap = new TreeMap<String, String>();
		
		// the localization values are in the "values" node
		// all other nodes are meta-data for the localization file
		Object valueNode = document.get("values");
		if (valueNode instanceof Map) {
			readValues((Map<String, Object>) valueNode, "", targetMap);
		}
		
		return targetMap;
	}

	/**
	 * Load the localization values from the given YAML document
	 */
	private void readValues(Map<String, Object> document, String parentPath, Map<String, String> targetMap) {
		for (Entry<String, Object> entry : document.entrySet()) {
			if (entry.getValue() instanceof String) {
				String keyName = (parentPath + entry.getKey());				
				String value = entry.getValue().toString();
				targetMap.put(keyName, value);
			} else {
				if (entry.getValue() instanceof Map) {
					readValues((Map<String, Object>) entry.getValue(), parentPath + entry.getKey() + PATH_SEPARATOR,
							targetMap);
				} else
					throw new RuntimeException("Unknown value type of entry '" + (parentPath + entry.getKey()) + "': " + entry.getValue().getClass().getName());
			}
		}
	}	
}
