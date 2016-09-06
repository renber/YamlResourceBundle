package de.renber.yamlbundleeditor.serialization;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Image;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.representer.Representer;

import de.renber.quiterables.QuIterables;
import de.renber.resourcebundles.yaml.ext.ImageConstructor;
import de.renber.resourcebundles.yaml.ext.ImageRepresenter;
import de.renber.yamlbundleeditor.models.Bundle;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.models.ResourceKey;

public class YamlBundleWriter implements BundleWriter {

	public final static String COMMENT_LANGUAGE_CODE = "COMMENTS";
	
	@Override
	public void write(OutputStream stream, BundleCollection bundleCollection, String languageCode) {

		// convert the bundle to a hash-map to serialize with SnakeYaml
		HashMap<String, Object> root = new HashMap<>();

		if (COMMENT_LANGUAGE_CODE.equals(languageCode)) {
			root.put("meta", getCommentsMetaMap());
		} else
			root.put("meta", getMetaMap(QuIterables.query(bundleCollection.getBundles()).firstOrDefault(x -> languageCode.equals(x.languageCode))));
		
		root.put("values", getValueMap(bundleCollection.getValues(), languageCode));
		
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);	
		options.setDefaultScalarStyle(ScalarStyle.PLAIN);
		Yaml yaml = new Yaml(new ImageRepresenter(), options);		

		try (Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
			yaml.dump(root, writer);													
		} catch (IOException e) {						
			throw new RuntimeException(e);
		}
	}

	private Map<String, Object> getCommentsMetaMap() {
		Map<String, Object> metaNode = new TreeMap<>();		
		metaNode.put("type", "comments");		
		return metaNode;
	}
	
	private Map<String, Object> getMetaMap(BundleMetaInfo metaInfo) {

		TreeMap<String, Object> metaNode = new TreeMap<>();

		if (metaInfo.isCommentBundle)
			metaNode.put("type", "comments");

		metaNode.put("languageCode", metaInfo.languageCode);
		metaNode.put("language", metaInfo.name);
		metaNode.put("localizedLanguage", metaInfo.localizedName);
		metaNode.put("author", metaInfo.author);
		metaNode.put("flagImage", metaInfo.flagImage);

		return metaNode;
	}

	private Map<String, Object> getValueMap(List<ResourceKey> keys, String languageCode) {
		TreeMap<String, Object> map = new TreeMap<>();

		for (ResourceKey key : keys) {
			
			Object value;
			
			if (COMMENT_LANGUAGE_CODE.equals(languageCode))
				value = key.comment;
			else	
				value = key.getLocalizedValue(languageCode);

			if (key.hasChildren()) {
				Map<String, Object> subMap = getValueMap(key.getChildren(), languageCode);

				if (value != null) {
					subMap.put("__value", value);					
				}
				
				// the actual node value is the submap
				value = subMap;
			}
			
			if (value != null) {
				map.put(key.name, value);
			}
		}

		return map;
	}

}
