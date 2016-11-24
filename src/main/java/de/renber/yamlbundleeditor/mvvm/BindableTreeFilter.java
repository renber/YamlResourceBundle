package de.renber.yamlbundleeditor.mvvm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.renber.databinding.viewmodels.IPropertyChangeSupport;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.viewmodels.datatypes.IHierarchicalViewModel;
import de.renber.yamlbundleeditor.viewmodels.datatypes.ResourceKeyViewModel;

/**
 * A generic filter for SWT TreeViewer which uses a conjunction of
 * BindableElementFilters to determine if an item matches the filter
 * @author renber
 *
 */
public class BindableTreeFilter extends ViewerFilter implements FilterChangedListener, IPropertyChangeSupport {

	AbstractTreeViewer viewer;
	
	List<BindableElementFilter> filters;

	ConcurrentHashMap<Object, Object> itemsToExpand = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public BindableTreeFilter(AbstractTreeViewer viewer, BindableElementFilter... filters) {
		this.viewer = viewer;	
				
		this.filters = Arrays.asList(filters);
		for(BindableElementFilter filter: filters)
			filter.addChangeListener(this);
	}

	private void refresh() {
		boolean oldIsFiltered = getIsFiltered();
		
		itemsToExpand.clear();

		viewer.refresh();

		// reveal matched items
		viewer.setExpandedElements(itemsToExpand.keySet().toArray());
		
		firePropertyChanged("isFiltered", oldIsFiltered, getIsFiltered());
	}

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {				
		boolean matches = true;
		
		for(BindableElementFilter filter: filters) {
			if (filter.evaluationRequired() && !filter.matches(element)) {
				matches = false;
				break;
			}
		}
		
		if (!matches) {						
			// check if any child fulfills the condition
			// if so, we include this node
			if (element instanceof IHierarchicalViewModel)
			{
				matches = QuIterables.query(((IHierarchicalViewModel)element).getChildren()).exists(x -> select(viewer, element, x));
				
				if (matches)
					itemsToExpand.put(element, element);	
			}					
		}

		return matches;

	}

	@Override
	public void filterRefreshSuggested() {
		refresh();
	}
		
	/**
	 * Indicates whether any filters are active
	 */
	public boolean getIsFiltered() {
		return QuIterables.query(filters).exists(x -> x.evaluationRequired());
	}

	// PropertyChangeSupport
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }
	
}
