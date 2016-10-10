package de.renber.yamlbundleeditor.viewmodels.datatypes;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
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
import de.renber.yamlbundleeditor.redoundo.KeyMoveAction;
import de.renber.yamlbundleeditor.redoundo.AutoUndoable;
import de.renber.yamlbundleeditor.redoundo.UndoableList;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.utils.ResourceKeyUtils;

/**
 * Represents a single key of the ResourceBundle with its versions for different
 * languages
 * 
 * @author renber
 *
 */
public class ResourceKeyViewModel extends DataViewModelBase<ResourceKey> implements IHierarchicalViewModel {

	// required services
	IDialogService dialogService;
	ILocalizationService loc;

	ResourceKeyViewModel parent;

	BundleCollectionViewModel owningCollection;

	IObservableList<ResourceKeyViewModel> children;

	// all available localized values
	IObservableList<LocalizedValueViewModel> localizedValues;

	// command which adds missing localized values
	ICommand addMissingValuesCommand;

	// command to change the path of this key
	ICommand renameCommand;

	// command to copy the path of the key to the clipboard
	ICommand copyPathToClipboardCommand;

	/**
	 * Create a ResourceKeyViewModel without parent
	 * 
	 * @param model
	 * @param owningCollection
	 */
	public ResourceKeyViewModel(ResourceKey model, BundleCollectionViewModel owningCollection, IUndoSupport undoSupport, IDialogService dialogService, ILocalizationService localizationService) {
		this(model, null, owningCollection, undoSupport, dialogService, localizationService);
	}

	public ResourceKeyViewModel(ResourceKey model, ResourceKeyViewModel parent, BundleCollectionViewModel owningCollection, IUndoSupport undoSupport, IDialogService dialogService, ILocalizationService localizationService) {
		super(model, undoSupport);

		this.dialogService = dialogService;
		this.loc = localizationService;

		this.parent = parent;
		this.owningCollection = owningCollection;

		localizedValues = new TransformedModelList<LocalizedValue, LocalizedValueViewModel>(model.getLocalizedValues(),
				(valueModel) -> new LocalizedValueViewModel(valueModel, this, undoSupport));

		// changes in the localized values, may affect the hasMissingValues
		// property
		((TransformedModelList<LocalizedValue, LocalizedValueViewModel>) localizedValues).addChildChangeListener(new IChildChangeListener() {
			@Override
			public void childChanged(PropertyChangeEvent evt) {
				firePropertyChanged("hasMissingValues", null, getHasMissingValues());
			}
		});

		// add missing langCodes to localized values without value, but only if
		// we have at least one value
		// otherwise it is an intermediate node
		if (!getIsIntermediateNode()) {
			addMissingValues();
		}

		// children will be loaded lazily
		children = null;

		createCommands();
	}

	private void createCommands() {
		addMissingValuesCommand = new RelayCommand(() -> {
			addMissingValues();
		});

		renameCommand = new RelayCommand(() -> {
			
			String oldPath = getPath();
			String newPath = dialogService.showTextPrompt(loc.getString("keyEditor:editKey:promptTitle"), loc.getString("keyEditor:editKey:promptMessage"), oldPath, true);

			if (newPath == null || oldPath.equals(newPath))
				return;
			
			try {
				move(newPath);	
			} catch (IllegalArgumentException e) {
				dialogService.showMessageDialog(loc.getString("dialogs:error:title"), loc.getString("keyEditor:editKey:invalidName"));
			}
		}, () -> owningCollection != null);

		copyPathToClipboardCommand = new RelayCommand(() -> {
			// copy the path of this resource key to the clipboard
			StringSelection stringSelection = new StringSelection(getPath());
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);
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
	
	/**
	 * Move this key to a new path, Can also be used to rename the key
	 * @param newPath The new path of the key (including its (new) name)
	 */
	public void move(String newPath) {
		try {
			String oldPath = getPath();
			
			// split the key path
			String[] oldParts = oldPath.split("\\:");
			String[] newParts = newPath.split("\\:");
			// make sure that there are no empty parts
			if (newParts.length == 0 || QuIterables.query(newParts).exists(x -> x.isEmpty() || ":".equals(x))) {
				throw new IllegalArgumentException("newPath");				
			}
			
			// we'll create a custom undo action, so disable auto-recording
			getUndoSupport().suspend();					

			// check if only the name (and not parent) has changed
			if (oldParts.length == newParts.length && QuIterables.query(oldParts).take(oldParts.length - 1).sequenceEquals(QuIterables.query(newParts).take(newParts.length - 1))) {
				// nothing to do
			} else {
				// if the path has changed, remove the key from its current
				// parent and insert it to the new one
				if (parent == null)
					owningCollection.getValues().remove(this);
				else
					parent.getChildren().remove(this);

				if (newParts.length > 1) {
					// create/get the new parent
					ResourceKeyViewModel newParent = ResourceKeyUtils.createPath(owningCollection, null, QuIterables.query(newParts).take(newParts.length - 1),
							(newKey, parentKey) -> new ResourceKeyViewModel(newKey, parentKey, owningCollection, getUndoSupport(), dialogService, loc));
					setParent(newParent);
					newParent.getChildren().add(this);
				} else {
					// add the key to root
					setParent(null);
					owningCollection.getValues().add(this);
				}
			}

			// change this key's name
			setName(newParts[newParts.length - 1]);
			
			getUndoSupport().resume();
			getUndoSupport().record(new KeyMoveAction(this, oldPath, newPath));

			owningCollection.setSelectedResourceKey(this);
		} finally {
			// make sure that redo/undo continues to work even on error			
			getUndoSupport().resume();
		}
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

	/**
	 * Name cannot be changed from the outside Use move() to update it
	 */
	private void setName(String newValue) {
		String oldPath = getPath();
		changeProperty(model, "name", newValue);

		// path is directly affected by the name change
		firePropertyChanged("path", oldPath, getPath());
	}

	public ResourceKeyViewModel getParent() {
		return parent;
	}

	private void setParent(ResourceKeyViewModel newValue) {
		changeProperty("parent", newValue);
	}

	@AutoUndoable
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
			children = new UndoableList<ResourceKey, ResourceKeyViewModel>(model.getChildren(), (keyModel) -> new ResourceKeyViewModel(keyModel, this, owningCollection, getUndoSupport(), dialogService, loc), getUndoSupport());
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

	public ICommand getCopyPathToClipboardCommand() {
		return copyPathToClipboardCommand;
	}

	public ICommand getRenameCommand() {
		return renameCommand;
	}
}
