package de.renber.yamlbundleeditor.mvvm;

import java.util.List;

import de.renber.databinding.collections.ItemTransformer;
import de.renber.databinding.collections.TransformedList;
import de.renber.yamlbundleeditor.viewmodels.datatypes.DataViewModelBase;

/**
 * TransformedList which syncs a List<TModel> with DataViewModelBase<TModel> view models
 * (Helper class which needs less parameters since DataViewModelBase is a known class)
 * @author renber
 *
 * @param <TModel> Type of the model class
 */
public class TransformedModelList<TModel, TViewModel extends DataViewModelBase<TModel>> extends TransformedList<TModel, TViewModel> {

	public TransformedModelList(List<TModel> wrappedList, ItemTransformer<TModel, TViewModel> transformer) {
		super(wrappedList, transformer, (vm) -> vm.getModel());
	}

}
