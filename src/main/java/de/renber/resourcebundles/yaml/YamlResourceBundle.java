package de.renber.resourcebundles.yaml;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.StreamReader;

import de.renber.resourcebundles.yaml.ext.ImageConstructor;

/**
 * A resource bundle which retrieves its keys from a yaml document
 * 
 * Sample yaml:
 * 
 * values: testValue: EntryOne: HelloWorld
 * 
 * YamlResourceBundle.getString("testValue:EntryOne") returns HelloWorld
 * 
 * @author berre
 *
 */
public class YamlResourceBundle extends ResourceBundle {

	static Control CONTROL = new Control();
	static char PATH_SEPARATOR = ':';

	HashMap<String, String> keys = new HashMap<String, String>();

	public YamlResourceBundle(InputStream yamlStream) {
		Yaml yaml = new Yaml(new ImageConstructor());
		Map<String, Object> map = null;

		try (Reader reader = new InputStreamReader(yamlStream, StandardCharsets.UTF_8)) {
			map = (Map<String, Object>) yaml.load(reader);
			loadValues(map, keys);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	private void loadValues(Map<String, Object> document, Map<String, String> targetMap) {
		keys.clear();

		// the localization values are in the "values" node
		// all other nodes are meta-data for the localization file

		Object valueNode = document.get("values");
		if (valueNode instanceof Map) {
			loadValues((Map<String, Object>) valueNode, "", targetMap);
		}
	}

	/**
	 * Load the localization values from the given YAML document
	 */
	private void loadValues(Map<String, Object> document, String parentPath, Map<String, String> targetMap) {
		for (Entry<String, Object> entry : document.entrySet()) {
			if (entry.getValue() instanceof String) {
				String keyName = (parentPath + entry.getKey()).toLowerCase();
				// trim trailing line-breaks etc.
				String value = entry.getValue().toString().trim();
				targetMap.put(keyName, value);
			} else {
				if (entry.getValue() instanceof Map) {
					loadValues((Map<String, Object>) entry.getValue(), parentPath + entry.getKey() + PATH_SEPARATOR,
							targetMap);
				} else
					throw new RuntimeException("Unknown value type: " + entry.getValue().getClass().getName());
			}
		}
	}

	@Override
	protected Object handleGetObject(String key) {

		key = key.toLowerCase().replace(':', PATH_SEPARATOR);

		Object o = keys.get(key);
		if (o == null) {
			// could be that this key has children and a value itself
			// which is not possible in yaml
			// in this case, the value can be found in the special __value child
			// node (this will be resolved in handleGetObject(), so the path
			// does not need to include the __value part)
			o = keys.get(key + ":" + "__value");
			if (o == null) {
				// indicate, that no such key exists but do not throw an
				// exception
				return "{" + key + "}";
			}
		}

		return o;
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(keys.values());
	}

	/**
	 * Appropriate Control implementation for YamlResourceBundle Allows the
	 * ResourceBundle API to load yaml resource files
	 * 
	 * @author renber
	 */
	public static class Control extends ResourceBundle.Control {

		public List<String> getFormats(String baseName) {
			if (baseName == null)
				throw new NullPointerException();
			return Arrays.asList("yaml", "yml");
		}

		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
				boolean reload) throws IllegalAccessException, InstantiationException, IOException {
			if (baseName == null || locale == null || format == null || loader == null)
				throw new NullPointerException();
			ResourceBundle bundle = null;
			if (format.equals("yaml") || format.equals("yml")) {
				String bundleName = toBundleName(baseName, locale);
				String resourceName = toResourceName(bundleName, format);
				InputStream stream = null;
				if (reload) {
					URL url = loader.getResource(resourceName);
					if (url != null) {
						URLConnection connection = url.openConnection();
						if (connection != null) {
							// Disable caches to get fresh data for
							// reloading.
							connection.setUseCaches(false);
							stream = connection.getInputStream();
						}
					}
				} else {
					stream = loader.getResourceAsStream(resourceName);
				}
				if (stream != null) {
					try (BufferedInputStream bis = new BufferedInputStream(stream)) {
						bundle = new YamlResourceBundle(bis);
					}
				}
			}
			return bundle;
		}
	}
}
