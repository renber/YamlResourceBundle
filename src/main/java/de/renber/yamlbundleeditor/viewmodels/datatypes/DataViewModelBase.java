package de.renber.yamlbundleeditor.viewmodels.datatypes;

import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.viewmodels.UndoableViewModelBase;;

/**
 * Base class for ViewModels which wrap data models 
 * @author renber
 *
 * @param <TModel> Type of the model class
 */
public class DataViewModelBase<TModel> extends UndoableViewModelBase {

	protected TModel model;
	
	public DataViewModelBase(TModel model, IUndoSupport undoSupport) {
		super(undoSupport);
		
		this.model = model;
	}
	
	public TModel getModel() {
		return model;
	}
	
}
