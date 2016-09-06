package de.renber.yamlbundleeditor.viewmodels.datatypes;

import java.util.List;

/**
 * Interface for ViewModels which are hierarchical
 * @author renber
 */
public interface IHierarchicalViewModel {
	public IHierarchicalViewModel getParent();
	
	public List getChildren();
	
	public boolean getHasChildren();
	
	/**
	 * Inform this object that its children collection has changed
	 */
	public void childrenChanged();	
}
