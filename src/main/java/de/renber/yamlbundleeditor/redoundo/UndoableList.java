package de.renber.yamlbundleeditor.redoundo;

import java.util.List;

import org.eclipse.core.databinding.observable.list.ListDiffEntry;

import de.renber.databinding.collections.ItemTransformer;
import de.renber.yamlbundleeditor.mvvm.TransformedModelList;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.viewmodels.datatypes.DataViewModelBase;

/**
 * A TransformedModelList implementation which has undo/redo support
 * 
 * @author renber
 */
public class UndoableList<TModel, TViewModel extends DataViewModelBase<TModel>> extends TransformedModelList<TModel, TViewModel> {

	IUndoSupport undoSupport;

	public UndoableList(List<TModel> wrappedList, ItemTransformer<TModel, TViewModel> transformer, IUndoSupport undoSupport) {
		super(wrappedList, transformer);

		this.undoSupport = undoSupport;
		this.addListChangeListener((evt) -> {
			for (ListDiffEntry<? extends TViewModel> diff : evt.diff.getDifferences()) {
				// record the change
				undoSupport.record(new ListChangeAction(this, diff.getElement(), diff.getPosition(), diff.isAddition()));
			}
		});
	}



}
