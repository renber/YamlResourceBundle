package de.renber.yamlbundleeditor.mvvm;

@FunctionalInterface
public interface FilterFunc<TItem, TFilterValue> {

	public boolean matches(TItem item, TFilterValue filterValue);
	
}
