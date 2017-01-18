package de.renber.yamlbundleeditor.viewmodels.datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import de.renber.databinding.collections.TransformedList;
import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.quiterables.QuIterables;
import de.renber.quiterables.Queriable;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.mvvm.TransformedModelList;
import de.renber.yamlbundleeditor.redoundo.IUndoListener;
import de.renber.yamlbundleeditor.redoundo.IUndoableAction;
import de.renber.yamlbundleeditor.redoundo.UndoableList;
import de.renber.yamlbundleeditor.serialization.BundleUtils;
import de.renber.yamlbundleeditor.serialization.YamlBundleWriter;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.services.IconProvider;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;
import de.renber.yamlbundleeditor.utils.ListUtils;
import de.renber.yamlbundleeditor.utils.ResourceKeyUtils;
import de.renber.yamlbundleeditor.utils.SearchOptions;

/**
 * Represents a collection of resource bundles
 * for different languages
 * @author renber
 *
 */
public class BundleCollectionViewModel extends DataViewModelBase<BundleCollection> {

	private ILocalizationService loc;
	private IDialogService dialogService;

	IObservableList<BundleMetaViewModel> bundles;
	IObservableList<ResourceKeyViewModel> values;	
	
	private boolean hasUnsavedChanges = false;
	
	// The last search term used by the FindCommand	
	private SearchOptions currentSearchOptions = null;
	
	private BundleMetaViewModel selectedBundle;
	private ResourceKeyViewModel selectedResourceKey;	
	
	private ICommand saveCollectionCommand;
	private ICommand saveCollectionAsCommand;
	
	private ICommand exportCollectionCommand;
	
	private ICommand addBundleCommand;
	private ICommand removeSelectedBundleCommand;
	
	private ICommand addResourceKeyCommand;
	private ICommand removeResourceKeyCommand;
	
	private ICommand findCommand;
	private ICommand findNextCommand;
	
	private ICommand jumpToKeyCommand;
	
	private ICommand cleanCollectionCommand;
	
	/**
	 * Base name of the merged bundles
	 */
	private String basename;
	private String path;
	
	public BundleCollectionViewModel(BundleCollection model, String basename, IDialogService dialogService, IUndoSupport undoSupport, ILocalizationService localizationService) {
		super(model, undoSupport);
		
		this.basename = basename;
		this.dialogService = dialogService;
		this.loc = localizationService;
		
		undoSupport.addUndoListener(new IUndoListener() {
			@Override
			public void actionRecorded(IUndoableAction action) {
				setHasUnsavedChanges(true);
				updateDescriptions();
			}

			@Override
			public void actionUndone(IUndoableAction action) {
				updateDescriptions();
			}

			@Override
			public void actionRedone(IUndoableAction action) {
				updateDescriptions();
			}
		
			private void updateDescriptions() {
				firePropertyChanged("undoDescription", "", getUndoDescription());
				firePropertyChanged("redoDescription", "", getRedoDescription());
			}
		});
		
		// wrap bundle meta information in view models
		bundles = new UndoableList<BundleMetaInfo, BundleMetaViewModel>(model.getBundles(),
				(metaModel) -> new BundleMetaViewModel(metaModel, dialogService, getUndoSupport()), getUndoSupport());		
		
		// wrap resource keys in view models
		values = new UndoableList<ResourceKey, ResourceKeyViewModel>(model.getValues(),
				(keyModel) -> new ResourceKeyViewModel(keyModel, this, getUndoSupport(), dialogService, loc), getUndoSupport());				
		
		createCommands();
	}	
	
	private void createCommands() {
		saveCollectionCommand = new RelayCommand( () -> {	
			
			if (path == null || path.isEmpty()) {
				saveCollectionAsCommand.execute();
				return;
			}
			
			YamlBundleWriter writer = new YamlBundleWriter();
			for(String languageCode: QuIterables.query(getBundles()).select(x -> x.getLanguageCode())) {
				
				// write language resources
				try(FileOutputStream fStream = new FileOutputStream(getPath() + File.separatorChar + getBasename() + "_" + languageCode + ".yaml")) {
					writer.write(fStream, getModel(), languageCode);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// write comment file
				try(FileOutputStream fStream = new FileOutputStream(getPath() + File.separatorChar + getBasename() + "_comments.yaml")) {
					writer.write(fStream, getModel(), YamlBundleWriter.COMMENT_LANGUAGE_CODE);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// reset change mark
				setHasUnsavedChanges(false);
			}			
		}, () -> getHasUnsavedChanges());
		
		saveCollectionAsCommand = new RelayCommand ( () -> {
			
			// let the user select a new directory and basename
			File f = dialogService.showSaveFileDialog(loc.getString("dialogs:file:saveCollection:title"), new FileExtFilter(loc.getString("dialogs:file:extensions:descriptions:yaml"), "*.yaml;*.yml"));
			if (f != null) {				
				// set path and basename
				
				setBasename(BundleUtils.deduceBundleBaseName(f.getAbsolutePath()));
				setPath(Paths.get(f.getAbsolutePath()).getParent().toString());
				
				if (basename.isEmpty() || path.isEmpty())
					dialogService.showMessageDialog(loc.getString("dialogs:error:title"), loc.getString("dialogs:save:error:invalidPath"));
				else				
					// save
					saveCollectionCommand.execute();
			}
		});
		
		exportCollectionCommand = new RelayCommand( () -> {
			dialogService.showExportDialog(this);
		});
		
		addBundleCommand = new RelayCommand( () -> {
			
			String langCode = dialogService.showTextPrompt(loc.getString("collectionEditor:addLanguage:promptTitle"), loc.getString("collectionEditor:addLanguage:promptMessage"), "");
			
			// Todo: check if already exists
			if (langCode != null && !langCode.isEmpty()) {
				BundleMetaInfo newBundle = new BundleMetaInfo();						
				
				// try to auto-determine the language
				Locale locale = new Locale(langCode);				
				newBundle.languageCode = langCode;
				newBundle.name = locale.getDisplayName(Locale.US);
				newBundle.localizedName = locale.getDisplayName(locale);
				
				// check if we have a flag icon for this language code
				Image img = IconProvider.getFlagIcon(locale.getLanguage());
				if (img != null)
					newBundle.flagImage = img;
				
				bundles.add(new BundleMetaViewModel(newBundle, dialogService, getUndoSupport()));	
			}			
		});
		
		removeSelectedBundleCommand = new RelayCommand( () -> {
			bundles.remove(selectedBundle);
		}, () -> selectedBundle != null);
		
		addResourceKeyCommand = new RelayCommand( () -> {			
			String defValue = selectedResourceKey == null ? "" : selectedResourceKey.getPath() + ":";			
			String value = dialogService.showTextPrompt(loc.getString("keyEditor:addKey:promptTitle"), loc.getString("keyEditor:addKey:promptMessage"), defValue, true);
			
			if (value != null) {				
				// split the key path				
				String[] parts = value.split("\\:");										
				// make sure that there are no empty parts
				if (parts.length == 0 || QuIterables.query(parts).exists(x -> x.isEmpty() || ":".equals(x))) {
					dialogService.showMessageDialog(loc.getString("dialogs:error:title"), loc.getString("keyEditor:addKey:invalidName"));
					return;
				}
				
				ResourceKey key;
				ResourceKeyViewModel keyvm;
				
				if (parts.length > 1) {					
					// find the parent key
					keyvm = ResourceKeyUtils.createPath(this, null, QuIterables.query(parts),
							(newKey, parentKey) -> new ResourceKeyViewModel(newKey, parentKey, this, getUndoSupport(), dialogService, loc));
				} else {
					key = new ResourceKey();
					key.name = value;
					keyvm = new ResourceKeyViewModel(key, null, this, getUndoSupport(), dialogService, loc);
					getValues().add(keyvm);
				}
				
				keyvm.addMissingValuesCommand.execute();
				setSelectedResourceKey(keyvm);
			}									
		});
		
		removeResourceKeyCommand = new RelayCommand( () -> {
			// if the key has children, ask for confirmation
			if (selectedResourceKey.getHasChildren()) {
				if (!dialogService.showQuestionDialog(loc.getString("keyEditor:removeKey:confirmation")))
						// cancel
						return;
			}
			
			if (selectedResourceKey.getParent() == null)
				getValues().remove(selectedResourceKey);
			else
				selectedResourceKey.getParent().getChildren().remove(selectedResourceKey);
		}, 
		() -> getSelectedResourceKey() != null);
		
		findCommand = new RelayCommand( () -> {
			
			SearchOptions searchOptions = dialogService.showFindKeyDialog(currentSearchOptions);
			
			if (searchOptions != null && !searchOptions.getSearchTerm().isEmpty()) {
				currentSearchOptions = searchOptions;
				gotoNextMatchingKey(true);
			}					
			
		});
		
		findNextCommand = new RelayCommand( () -> {
			gotoNextMatchingKey(false);
		}, () -> currentSearchOptions != null);
		
		jumpToKeyCommand = new RelayCommand( () -> {
			
			String path = dialogService.showTextPrompt(loc.getString("keyEditor:jumpToKey:prompt:title"), loc.getString("keyEditor:jumpToKey:prompt:message"), "");
			if (path != null) {
				// find the key with this path
				
				try {
					String[] parts = ResourceKeyUtils.segmentPath(path);					
					ResourceKeyViewModel key = ResourceKeyUtils.findKey(this, null, QuIterables.query(parts));
					
					if (key == null)
					 throw new IllegalArgumentException("No key found with this path");
					
					// highlight the found key
					setSelectedResourceKey(key);
				} catch (IllegalArgumentException e) {
					// path is invalid -> key cannot exist
					dialogService.showInformationDialog(loc.getString("keyEditor:jumpToKey:notFound"));
				}							
			}			
		});
		
		cleanCollectionCommand = new RelayCommand( () -> {			
			if (dialogService.showQuestionDialog(loc.getString("tools:cleanCollection:prompt"))) {
				
				// TODO: add undo support (compound actions?)
				getUndoSupport().suspend();				
				ResourceKeyUtils.removeUntranslatedArtifacts(getValues(), getSelectedBundle().getLanguageCode());				
				getUndoSupport().resume();
			}			
		}, () -> getSelectedBundle() != null);
	}
	
	/**
	 * Select the next key which matches the current search term
	 * @param fromStart true - Search all keys, false - Begin at the currently selected key
	 */
	void gotoNextMatchingKey(boolean fromStart) {
		if (getValues().size() == 0)
			return;
		
		ResourceKeyViewModel startFromKey = null;
		
		if (!fromStart)
			startFromKey = getSelectedResourceKey();
					
		fromStart = startFromKey == null;
		if (startFromKey == null) {
			startFromKey = getValues().get(0);
		}
		
		final ResourceKeyViewModel skipToKey = startFromKey;		
		
		ResourceKeyViewModel foundKey = QuIterables.query(ResourceKeyUtils.IterateChildren(getValues()))				
			.skipWhile(x -> x != skipToKey)
			.skip(fromStart ? 0 : 1)
			.firstOrDefault(x -> currentSearchOptions.matches(x.getModel()));
		
		if (foundKey == null) {
			dialogService.showInformationDialog(loc.getString("keyEditor:find:nokeyfound"));
		} else
			setSelectedResourceKey(foundKey);

	}
	
	// --------------------------
	// property getters / setters
	// --------------------------
	
	public String getBasename() {
		return basename;
	}
	
	public void setBasename(String newValue) {
		changeProperty("basename", newValue);
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String newValue) {
		changeProperty("path", newValue);
	}
	
	public boolean getHasUnsavedChanges() {
		return hasUnsavedChanges;
	}
	
	protected void setHasUnsavedChanges(boolean newValue) {
		changeProperty("hasUnsavedChanges", newValue);
	}
	
	public BundleMetaViewModel getSelectedBundle() {
		return selectedBundle;
	}
	
	public void setSelectedBundle(BundleMetaViewModel newValue) {
		changeProperty("selectedBundle", newValue);
	}
	
	public ResourceKeyViewModel getSelectedResourceKey() {
		return selectedResourceKey;
	}
	
	public void setSelectedResourceKey(ResourceKeyViewModel newValue) {
		changeProperty("selectedResourceKey", newValue);
	}
	
	/**
	 * Return all bundles contained in this collection
	 */
	public IObservableList<BundleMetaViewModel> getBundles() {
		return bundles;
	}
	
	/**
	 * Return all top-level resource keys of this bundle collection
	 */
	public IObservableList<ResourceKeyViewModel> getValues() {
		return values;
	}	
	
	// ---------------
	// command getters
	// ---------------
	
	public ICommand getSaveCollectionCommand() {
		return saveCollectionCommand;
	}
	
	public ICommand getSaveCollectionAsCommand() {
		return saveCollectionAsCommand;
	}
	
	public ICommand getExportCollectionCommand() {
		return exportCollectionCommand;
	}
	
	public ICommand getAddBundleCommand() {
		return addBundleCommand;
	}
	
	public ICommand getRemoveSelectedBundleCommand() {
		return removeSelectedBundleCommand;
	}
	
	public ICommand getAddResourceKeyCommand() {
		return addResourceKeyCommand;
	}
	
	public ICommand getRemoveResourceKeyCommand() {
		return removeResourceKeyCommand;
	}

	public ICommand getFindCommand() {
		return findCommand;
	}
	
	public ICommand getFindNextCommand() {
		return findNextCommand;
	}
	
	public ICommand getJumpToKeyCommand() {
		return jumpToKeyCommand;
	}
	
	public ICommand getCleanCollectionCommand() {
		return cleanCollectionCommand;
	}
	
	/**
	 * The command to undo action
	 */
	public ICommand getUndoCommand() {
		return getUndoSupport().getUndoCommand();
	}
	
	public String getUndoDescription() {
		return getUndoSupport().getUndoDescription();
	}	
	
	/**
	 * The command to redo action
	 */
	public ICommand getRedoCommand() {
		return getUndoSupport().getRedoCommand();
	}

	public String getRedoDescription() {
		return getUndoSupport().getRedoDescription();
	}	
}
