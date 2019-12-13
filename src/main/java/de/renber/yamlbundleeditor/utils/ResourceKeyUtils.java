package de.renber.yamlbundleeditor.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;

import de.renber.databinding.collections.ItemTransformer;
import de.renber.quiterables.QuIterables;
import de.renber.quiterables.Queriable;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleCollectionViewModel;
import de.renber.yamlbundleeditor.viewmodels.datatypes.LocalizedValueViewModel;
import de.renber.yamlbundleeditor.viewmodels.datatypes.ResourceKeyViewModel;

public class ResourceKeyUtils {

	private ResourceKeyUtils() {
		// --
	}
	
	/**
	 * Removes all "translated" artifacts which are present in other languages than mainLanguage but are just a copy
	 * of the main language entry and are more than 5 characters long (skips OK etc.)
	 * @param keys
	 * @param mainLanguage
	 */
	public static void removeUntranslatedArtifacts(List<ResourceKeyViewModel> keys, String mainLanguageCode)
	{
		if (keys == null)
			throw new IllegalArgumentException("Argument keys must not be null.");
		if (mainLanguageCode == null)
			throw new IllegalArgumentException("Argument mainLanguageCode must not be null.");
		
		for(ResourceKeyViewModel key: keys) {
			LocalizedValueViewModel vm = QuIterables.query(key.getLocalizedValues()).firstOrDefault(x -> mainLanguageCode.equals(x.getLanguageCode()));
			if (vm != null && vm.getHasValue())
			{
				String artifact = vm.getValue().toString();
				if (artifact.length() > 5) {
					
					Queriable<LocalizedValueViewModel> toRem = QuIterables.query(key.getLocalizedValues()).where(x -> !mainLanguageCode.equals(x.getLanguageCode()) && artifact.equals(x.getValue()));
					// remove the artifact since it is not really translated
					toRem.forEach(x -> x.setValue(null));					
				}
			}
			
			if (key.getHasChildren()) {
				removeUntranslatedArtifacts(key.getChildren(), mainLanguageCode);
			}
		}
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
	
	/**
	 * Return/Create the key at the end of the given path, creates all intermediate keys if they do not exist
	 * @param parent
	 * @param path
	 * @return
	 */
	public static ResourceKey createPath(BundleCollection bundle, ResourceKey parent, Queriable<String> path) {		
		if (path.isEmpty())
			return parent;
		
		List<ResourceKey> list;
		if (parent == null)
			list = bundle.getValues();
		else
			list = parent.getChildren();
		
		String pathPart = path.take(1).single();
		
		// does this part already exist?
		ResourceKey key = QuIterables.query(list).firstOrDefault(x -> x.name.compareToIgnoreCase(pathPart) == 0);
		if (key == null) {
			// create this key
			key = new ResourceKey();
			key.name = pathPart;					
						
			ListUtils.insertSorted(key, list, (o1, o2) -> o1.name.compareTo(o2.name));			
		} else {
			// if this key exists but has no value
			// convert it to an intermediate node
			if (QuIterables.query(key.getLocalizedValues()).all(x -> x.value == null)) {
				key.getLocalizedValues().clear();
			}
		}
		
		return createPath(bundle, key, path.skip(1));
	}	
	
	/**
	 * Returns the key with the given path or null if it does not exist	 
	 */
	public static ResourceKeyViewModel findKey(BundleCollectionViewModel bundle, ResourceKeyViewModel parent, Queriable<String> path) {		
		if (path.isEmpty())
			return parent;
		
		List<ResourceKeyViewModel> list;
		if (parent == null)
			list = bundle.getValues();
		else
			list = parent.getChildren();
		
		String pathPart = path.take(1).single();
		
		// does the next part exist?
		ResourceKeyViewModel key = QuIterables.query(list).firstOrDefault(x -> x.getName().compareToIgnoreCase(pathPart) == 0);
		if (key == null) {
			return null;			
		}
		
		return findKey(bundle, key, path.skip(1));
	}	
	
	/**
	 * Returns the key with the given path or null if it does not exist	 
	 */
	public static ResourceKey findKey(BundleCollection bundle, ResourceKey parent, Queriable<String> path) {		
		if (path.isEmpty())
			return parent;
		
		List<ResourceKey> list;
		if (parent == null)
			list = bundle.getValues();
		else
			list = parent.getChildren();
		
		String pathPart = path.take(1).single();
		
		// does the next part exist?
		ResourceKey key = QuIterables.query(list).firstOrDefault(x -> x.name.compareToIgnoreCase(pathPart) == 0);
		if (key == null) {
			return null;			
		}
		
		return findKey(bundle, key, path.skip(1));
	}	
	
	/**
	 * Split the given key path into its segments and checks it for validity.
	 * If the path is invalid an IllegalArgumentException is thrown
	 */
	public static String[] segmentPath(String path) {
		String[] parts = path.split("\\:");
		// make sure that there are no empty parts
		if (parts.length == 0 || QuIterables.query(parts).exists(x -> x.isEmpty() || ":".equals(x))) {
			throw new IllegalArgumentException("path");				
		}
		return parts;
	}
	
	/**
	 * Return the keys in list and their children (and grandchildren, etc.) as Iterable	 
	 */
	public static Iterable<ResourceKeyViewModel> IterateChildren(List<ResourceKeyViewModel> list) {
		List<Iterable<ResourceKeyViewModel>> iterables = new ArrayList<>();
		
		for(ResourceKeyViewModel item: list) {
			iterables.add(new SingleElementIterable(item));
			if (item.getHasChildren())
				iterables.add(IterateChildren(item.getChildren()));	
		}
		
		return new CompoundIterable(iterables.toArray(new Iterable[iterables.size()]));
	}
	
}

class SingleElementIterable<T> implements Iterable<T> {

	T element;
	
	public SingleElementIterable(T element) {
		this.element = element;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new SingleElementIterator(element);
	}
	
	class SingleElementIterator<T> implements Iterator<T> {

		T element;
		boolean iterated = false;
		
		public SingleElementIterator(T element) {
			this.element = element;
		}
		
		@Override
		public boolean hasNext() {
			return !iterated;
		}

		@Override
		public T next() {
			if (iterated)
				throw new NoSuchElementException();
			
			iterated = true;
			return element;
		}
		
	}
}

class CompoundIterable<T> implements Iterable<T> {

	Iterable<T>[] iterables;
	
	public CompoundIterable(Iterable<T>[] iterables) {
		this.iterables = iterables;
	}
	
	@Override
	public Iterator<T> iterator() {
		Iterator<T>[] iterators = new Iterator[iterables.length];
		for(int i = 0; i < iterables.length; i++) {
			iterators[i] = iterables[i].iterator();
		}
		
		return new CompoundIterator<T>(iterators);
	}

	class CompoundIterator<T> implements Iterator<T>
	{

		Iterator<T>[] iterators;
		int currentIndex = 0;
		
		public CompoundIterator(Iterator<T>[] iterators) {
			this.iterators = iterators;
		}
		
		@Override
		public boolean hasNext() {
			if (currentIndex < iterators.length) {			
				return iterators[currentIndex].hasNext();
			} else {
				return false;
			}			
		}

		@Override
		public T next() {
			T item = iterators[currentIndex].next();
			if (!iterators[currentIndex].hasNext())
				currentIndex++;
			return item;
		}
		
	}
	
}
