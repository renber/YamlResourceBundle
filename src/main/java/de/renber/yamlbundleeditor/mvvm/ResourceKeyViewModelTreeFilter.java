package de.renber.yamlbundleeditor.mvvm;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.viewmodels.datatypes.ResourceKeyViewModel;

/**
 * ViewerFilter for trees containing ResourceKeyViewModels whose search term can be bound to a ObservableValue and which auto updates
 * the tree view when the search term changes
 * @author renber
 *
 */
public class ResourceKeyViewModelTreeFilter extends ViewerFilter {

	AbstractTreeViewer viewer;
	String filterText = "";

	ConcurrentHashMap<ResourceKeyViewModel, ResourceKeyViewModel> itemsToExpand = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public ResourceKeyViewModelTreeFilter(AbstractTreeViewer viewer, IObservableValue filterTextValue) {
		this.viewer = viewer;

		filterTextValue.addValueChangeListener((x) -> {
			Object o = x.getObservableValue().getValue();
			if (o == null)
				filterText = "";
			else
				filterText = ((String) o).toLowerCase();

			refresh();
		});
	}

	private void refresh() {
		itemsToExpand.clear();

		viewer.refresh();

		// reveal matched items
		viewer.setExpandedElements(itemsToExpand.keySet().toArray());		
	}

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {

		if (filterText.isEmpty())
			return true;

		ResourceKeyViewModel keyVm = (ResourceKeyViewModel) element;

		boolean matches = keyVm.getPath().toLowerCase().contains(filterText);		
		
		if (!matches) {
			// check if any child fulfills the condition
			// if so, we include this node
			matches = QuIterables.query(keyVm.getChildren()).exists(x -> select(viewer, keyVm, x));
			
			if (matches)
				itemsToExpand.put(keyVm, keyVm);
		}

		return matches;

	}

}
