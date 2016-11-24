package de.renber.yamlbundleeditor.mvvm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;


public class BindableElementFilter<TItem, TFilterValue> {
	
	private HashSet<FilterChangedListener> changeListeners = new HashSet<FilterChangedListener>();		
	
	TFilterValue defaultFilterValue;
	TFilterValue filterValue;
	
	FilterFunc<TItem, TFilterValue> filterFunc;
	
	/**
	 * 
	 * @param filterValueObservable The observable to get the filter value from
	 * @param filterFunc The function to evaluate the filter
	 * @param defaultFilterValue The filter value for which the filter does not need to be evaluated since it matches all elements
	 */
	public BindableElementFilter(IObservableValue filterValueObservable, FilterFunc<TItem, TFilterValue> filterFunc, TFilterValue defaultFilterValue)
	{
		this.filterFunc = filterFunc;		
		this.filterValue = defaultFilterValue;
		this.defaultFilterValue = defaultFilterValue;
		
		filterValueObservable.addValueChangeListener((x) -> {
			Object o = x.getObservableValue().getValue();					
			if (o == null)
				filterValue = defaultFilterValue;
			else
				filterValue = (TFilterValue)o;

			for(FilterChangedListener listener: changeListeners)
				listener.filterRefreshSuggested();
		});
	}
	
	public void addChangeListener(FilterChangedListener changeListener)
	{
		changeListeners.add(changeListener);
	}
	
	public void removeChangeListener(FilterChangedListener changeListener)
	{
		changeListeners.remove(changeListener);
	}
		
	public boolean evaluationRequired()
	{		
		// we do not need to evaluate the filter if the filter value is the default one
		return !filterValue.equals(defaultFilterValue);
	}
	
	public boolean matches(TItem item) {
		return filterFunc.matches(item, filterValue);
	}	
}
