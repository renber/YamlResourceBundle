package de.renber.yamlbundleeditor.redoundo;

import java.util.List;
import java.util.ResourceBundle;

import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.viewmodels.datatypes.IHierarchicalViewModel;

public class ListChangeAction implements IUndoableAction {

	List sourceList;
	Object item;
	ListChangeType changeType;
	int position;

	public ListChangeAction(List sourceList, Object item, int position, boolean isAddition) {
		this.sourceList = sourceList;
		this.item = item;
		this.position = position;
		this.changeType = isAddition ? ListChangeType.ItemAdded : ListChangeType.ItemRemoved;
	}

	@Override
	public void undo() throws RedoUndoException {
		switch (changeType) {
		case ItemAdded:
			removeItem();
			break;
		case ItemRemoved:
			addItem();
			break;
		}
	}

	@Override
	public void redo() throws RedoUndoException {
		switch (changeType) {
		case ItemAdded:
			addItem();
			break;
		case ItemRemoved:
			removeItem();
			break;
		}
	}

	private void addItem() {
		if (position != -1 && position < sourceList.size() - 1)
			sourceList.add(position, item);
		else
			sourceList.add(item);

		// if item is a hierarchical ViewModel, inform its parent
		// that we added a new item when this is the first item
		// to update the UI components (JFace does not listen for changes
		// in the child list when the parent has no children)
		// but firing childrenChanged everytime will add items twice(!)
		if (item instanceof IHierarchicalViewModel) {
			IHierarchicalViewModel vm = (IHierarchicalViewModel) item;
			if (vm.getParent() != null && vm.getParent().getChildren().size() == 1) {
				vm.getParent().childrenChanged();
			}
		}
	}

	private void removeItem() {
		sourceList.remove(item);

		// if item is a hierarchical ViewModel, inform its parent
		// that we removed the last item (if true)
		if (item instanceof IHierarchicalViewModel) {
			IHierarchicalViewModel vm = (IHierarchicalViewModel) item;
			if (vm.getParent() != null && vm.getParent().getChildren().size() == 00) {
				vm.getParent().childrenChanged();
			}
		}
	}

	@Override
	public boolean canMerge(IUndoableAction action) {
		return false;
	}

	@Override
	public IUndoableAction merge(IUndoableAction action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getActionDescription(ILocalizationService loc) {
		switch (changeType)
		{
			case ItemAdded:
				return item.getClass().getName() + " hinzugefügt";
			case ItemRemoved:
				return item.getClass().getName() + " entfernt";
			default:
				throw new IllegalStateException("Unimplemented change type: " + changeType.toString());
		}
	}
}

enum ListChangeType {
	ItemAdded, ItemRemoved
}
