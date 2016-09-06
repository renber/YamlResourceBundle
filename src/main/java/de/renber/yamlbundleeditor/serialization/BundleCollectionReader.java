package de.renber.yamlbundleeditor.serialization;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.models.Bundle;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.LocalizedValue;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.utils.ListUtils;

/**
 * Reads a collection of bundles using a BundleReader
 * @author renber
 */
public class BundleCollectionReader {

	/**
	 * Create a BundleCollection from the given streams which cotnain bundles 
	 * @param reader The reader to read the bundles
	 * @param bundles The stream data of the bundles which are contained in this collection
	 * @return The read collection
	 */
	public BundleCollection read(BundleReader bundleReader, InputStream... bundleStreams) {						
		// retrieve all Bundles from the given stream
		List<Bundle> bundles = new ArrayList<Bundle>();		
		for(InputStream inStream: bundleStreams) {
			Bundle bundle = bundleReader.read(inStream);
						
			bundles.add(bundle);			
		}		
		
		return read(bundles.toArray(new Bundle[bundles.size()]));
	}
	
	/**
	 * Create a BundleCollection from the given Bundles 
	 * @param bundles The bundles to merge
	 * @return The read collection
	 */
	public BundleCollection read(Bundle...bundles) {
		BundleCollection collection = new BundleCollection();
				
		for(Bundle bundle: bundles) {									
			// convert the keys to hierarchical form and add/merge them to the collection
			List<String> keys = QuIterables.query(bundle.getValues().keySet()).orderBy(x -> x).toList();
			
			for(String keyName: keys) {
				ResourceKey resKey = getResourceKey(keyName, collection.getValues());				
				String value = bundle.getValues().get(keyName);
				
				if (bundle.getMeta().isCommentBundle)
					resKey.comment = value;
				else
					resKey.getLocalizedValues().add(new LocalizedValue(bundle.getMeta().languageCode, value));
			}
			
			// add the bundle's meta data to the collection (except it is a comments bundle)
			if (!bundle.getMeta().isCommentBundle)
				collection.getBundles().add(bundle.getMeta());			
		}
			
		return collection;
	}
	
	/**
	 * Return the resource key with the given path or create it if it does not exist already
	 */
	private ResourceKey getResourceKey(String path, List<ResourceKey> root) {
		StringTokenizer tokenizer = new StringTokenizer(path, "\t");
		ResourceKey virtualRootKey = new ResourceKey();
		virtualRootKey.getChildren().addAll(root);
		ResourceKey currentKey = virtualRootKey;
		
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			
			if ("__value".equals(token)) {
				return currentKey;
			}
			
			ResourceKey nextKey = QuIterables.query(currentKey.getChildren()).firstOrDefault(x -> x.name.equals(token));
			if (nextKey == null) {
				nextKey = new ResourceKey();
				nextKey.name = token;
				
				ListUtils.insertSorted(nextKey, currentKey.getChildren(), (o1, o2) -> o1.name.compareTo(o2.name));				
				
				if (currentKey == virtualRootKey) {
					ListUtils.insertSorted(nextKey, root, (o1, o2) -> o1.name.compareTo(o2.name));					
				}					
			}
			
			currentKey = nextKey;			
		}
		return currentKey;
	}
	
}
