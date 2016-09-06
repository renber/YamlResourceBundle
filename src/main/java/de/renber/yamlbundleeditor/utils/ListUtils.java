package de.renber.yamlbundleeditor.utils;

import java.util.Comparator;
import java.util.List;

import de.renber.quiterables.QuIterables;

public class ListUtils {
	
	private void ListUtils() {
		// --
	}

	/**
	 * Insert the element into the already sorted list at the appropriate position
	 * @param element
	 * @param sortedList
	 * @param compFunc
	 */
	public static <T> void insertSorted(T element, List<T> sortedList, Comparator<T> compFunc) {		
		T predecessor = QuIterables.query(sortedList).skipWhile(x -> compFunc.compare(x, element) < 0).firstOrDefault();
		
		if (predecessor == null)
			sortedList.add(element);
		else
			sortedList.add(sortedList.indexOf(predecessor), element);
	}
	
}
