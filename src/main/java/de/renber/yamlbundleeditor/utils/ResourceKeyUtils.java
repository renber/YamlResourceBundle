package de.renber.yamlbundleeditor.utils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import de.renber.databinding.collections.ItemTransformer;
import de.renber.quiterables.QuIterables;
import de.renber.quiterables.Queriable;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleCollectionViewModel;
import de.renber.yamlbundleeditor.viewmodels.datatypes.ResourceKeyViewModel;

public class ResourceKeyUtils {

	private ResourceKeyUtils() {
		// --
	}
	
	/**
	 * Return/Create the key at the end of the given path, creates all intermediate keys if they do not exist
	 * @param parent
	 * @param path
	 * @return
	 */
	public static ResourceKeyViewModel createPath(BundleCollectionViewModel bundle, ResourceKeyViewModel parent, Queriable<String> path, BiFunction<ResourceKey, ResourceKeyViewModel, ResourceKeyViewModel> newKeyFunc) {		
		if (path.isEmpty())
			return parent;
		
		List<ResourceKeyViewModel> list;
		if (parent == null)
			list = bundle.getValues();
		else
			list = parent.getChildren();
		
		String pathPart = path.take(1).single();
		
		// does this part already exist?
		ResourceKeyViewModel key = QuIterables.query(list).firstOrDefault(x -> x.getName().compareToIgnoreCase(pathPart) == 0);
		if (key == null) {
			// create this key
			ResourceKey pathKey = new ResourceKey();
			pathKey.name = pathPart;
			key = newKeyFunc.apply(pathKey, parent);
			
			//key = new ResourceKeyViewModel(pathKey, parent, bundle, bundle.getUndoSupport(), bundle.getd, loc);
			ListUtils.insertSorted(key, list, (o1, o2) -> o1.getName().compareTo(o2.getName()));			
		} else {
			// if this key exists but has no value
			// convert it to an intermediate node
			if (QuIterables.query(key.getLocalizedValues()).all(x -> !x.getHasValue())) {
				key.getLocalizedValues().clear();
			}
		}
		
		return createPath(bundle, key, path.skip(1), newKeyFunc);
	}
	
}
