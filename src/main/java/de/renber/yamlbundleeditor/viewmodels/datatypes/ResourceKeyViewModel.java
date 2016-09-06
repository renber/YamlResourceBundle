package de.renber.yamlbundleeditor.viewmodels.datatypes;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;

import de.renber.databinding.collections.IChildChangeListener;
import de.renber.databinding.collections.TransformedList;
import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.models.LocalizedValue;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.mvvm.TransformedModelList;
import de.renber.yamlbundleeditor.redoundo.Undoable;
import de.renber.yamlbundleeditor.redoundo.UndoableList;
import de.renber.yamlbundleeditor.services.IUndoSupport;

/**
 * Represents a single key of the ResourceBundle with its versions for different
 * languages
 * 
 * @author renber
 *
 */
public class ResourceKeyViewModel extends DataViewModelBase<ResourceKey> implements IHierarchicalViewModel {

	ResourceKeyViewModel parent;

	BundleCollectionViewModel owningCollection;

	IObservableList<ResourceKeyViewModel> children;

	// all available localized values
	IObservableList<LocalizedValueViewModel> localizedValues;
	
	// command which adds missing localized values
	ICommand addMissingValuesCommand;

	/**
	 * Create a ResourceKeyViewModel without parent
	 * 
	 * @param model
	 * @param owningCollection
	 */
	public ResourceKeyViewModel(ResourceKey model, BundleCollectionViewModel owningCollection, IUndoSupport undoSupport) {
		this(model, null, owningCollection, undoSupport);
	}

	public ResourceKeyViewModel(ResourceKey model, ResourceKeyViewModel parent, BundleCollectionViewModel owningCollection, IUndoSupport undoSupport) {
		super(model, undoSupport);

		this.parent = parent;
		this.owningCollection = owningCollection;

		localizedValues = new TransformedModelList<LocalizedValue, LocalizedValueViewModel>(model.getLocalizedValues(),
				(valueModel) -> new LocalizedValueViewModel(valueModel, this, undoSupport));
		
		// changes in the localized values, may affect the hasMissingValues property
		((TransformedModelList<LocalizedValue, LocalizedValueViewModel>)localizedValues).addChildChangeListener(new IChildChangeListener() {
			@Override
			public void childChanged(PropertyChangeEvent evt) {
				firePropertyChanged("hasMissingValues", null, getHasMissingValues());
			}});

		// add missing langCodes to localized values without value, but only if
		// we have at least one value
		// otherwise it is an intermediate node
		if (!getIsIntermediateNode()) {
			addMissingValues();
		}

		// children will be loaded lazily
		children = null;

		// commands
		addMissingValuesCommand = new RelayCommand(() -> {
			addMissingValues();						
		});
	}

	private void addMissingValues() {			
		List<LocalizedValueViewModel> items = new ArrayList<>(localizedValues);
		
		for (String langCode : QuIterables.query(owningCollection.bundles).select(x -> x.getLanguageCode()).except(QuIterables.query(localizedValues).select(x -> x.getLanguageCode()))) {
			LocalizedValue loc = new LocalizedValue(langCode, null);
			items.add(new LocalizedValueViewModel(loc, this, getUndoSupport()));						
		}
		
		localizedValues.clear();
		localizedValues.addAll(QuIterables.query(items).orderBy(x -> x.getLanguageCode()).toList());
		
		firePropertyChanged("isIntermediateNode", null, getIsIntermediateNode());
	}
	
	// -------------------
	// getters and setters
	// -------------------

	public String getPath() {
		if (getParent() == null)
			return getName();
		else
			return getParent().getPath() + ":" + getName();
	}

	public String getName() {
		return model.name;
	}

	public void setName(String newValue) {
		changeProperty(model, "name", newValue);
	}

	public ResourceKeyViewModel getParent() {
		return parent;
	}

	private void setParent(ResourceKeyViewModel newValue) {
		changeProperty("parent", newValue);
	}

	@Undoable
	public String getComment() {
		return model.comment;
	}

	public void setComment(String newValue) {
		changeProperty(model, "comment", newValue);
	}

	public BundleCollectionViewModel getOwningCollection() {
		return owningCollection;
	}

	/**
	 * Return whether this key has child keys
	 */
	public boolean getHasChildren() {
		return model.hasChildren();
	}

	public IObservableList<LocalizedValueViewModel> getLocalizedValues() {
		return localizedValues;
	}

	/**
	 * Return if this node has no values associated with it but is only a parent
	 * for child nodes
	 */
	public boolean getIsIntermediateNode() {
		return getLocalizedValues().size() == 0;
	}

	/**
	 * Return all children of this key (if any)
	 */
	public IObservableList<ResourceKeyViewModel> getChildren() {
		if (children == null) {
			children = new UndoableList<ResourceKey, ResourceKeyViewModel>(model.getChildren(), (keyModel) -> new ResourceKeyViewModel(keyModel, this, owningCollection, getUndoSupport()), getUndoSupport());			
		}

		return children;
	}
	
	public void childrenChanged() {
		firePropertyChanged("hasChildren", null, null);
	}
	
	/**
	 * Return if this key has missing values (if it is not a intermediate node)
	 */
	public boolean getHasMissingValues() {
		return !getIsIntermediateNode() && QuIterables.query(getLocalizedValues()).exists((x) -> !x.getHasValue());
	}
	
	// ---------------
	// Command getters
	// ---------------
	
	public ICommand getAddMissingValuesCommand() {
		return addMissingValuesCommand;
	}
}
