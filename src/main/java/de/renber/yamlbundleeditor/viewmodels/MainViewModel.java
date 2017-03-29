package de.renber.yamlbundleeditor.viewmodels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.models.Bundle;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.redoundo.IUndoListener;
import de.renber.yamlbundleeditor.redoundo.IUndoableAction;
import de.renber.yamlbundleeditor.serialization.BundleCollectionReader;
import de.renber.yamlbundleeditor.serialization.BundleUtils;
import de.renber.yamlbundleeditor.serialization.YamlBundleReader;
import de.renber.yamlbundleeditor.serialization.YamlBundleWriter;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleCollectionViewModel;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleMetaViewModel;

public class MainViewModel extends WindowedViewModelBase {

	// the currently loaded bundle collection
	private BundleCollectionViewModel currentCollection; // = new BundleCollectionViewModel(new BundleCollection());

	private IDialogService dialogService;
	private ILocalizationService loc;

	private ImportViewModel importViewModel;
	
	// Commands
	ICommand newCollectionCommand;
	ICommand loadCollectionCommand;
	ICommand closeCollectionCommand;

	// Constructor
	public MainViewModel(IDialogService dialogService, IUndoSupport undoSupport, ILocalizationService localizationService) {
		super(undoSupport);
		
		if (dialogService == null)
			throw new IllegalArgumentException("The parameter dialogService must not be null.");
		if (localizationService == null)
			throw new IllegalArgumentException("The parameter langBundle must not be null.");

		this.dialogService = dialogService;
		this.loc = localizationService;				
		this.importViewModel = new ImportViewModel(this, localizationService, dialogService);
		
		createCommands();
	}

	void createCommands() {
		
		newCollectionCommand = new RelayCommand( () -> {			
			// TODO: ask if current collection (if any) should be saved
			BundleCollection collection = new BundleCollection();
			setCurrentCollection(new BundleCollectionViewModel(collection, "", dialogService, getUndoSupport(), loc));			
		});
		
		loadCollectionCommand = new RelayCommand(
				() -> {
					File file = dialogService.showOpenFileDialog(loc.getString("dialogs:file:loadCollection:title"), new FileExtFilter(loc.getString("dialogs:file:extensions:descriptions:yaml"), "*.yaml;*.yml"));
					if (file != null) {
						String basename = BundleUtils.deduceBundleBaseName(file.getAbsolutePath());

						YamlBundleReader bundleReader = new YamlBundleReader();
						BundleCollectionReader collectionReader = new BundleCollectionReader();
						Path folder = Paths.get(file.getAbsolutePath()).getParent();

						List<Bundle> bundleList = new ArrayList<Bundle>();

						// find all yaml files with the same base name in
						// this directory and load them
						try {
							for (Path path : Files.newDirectoryStream(folder)) {
								String fname = path.getFileName().toString();
								if (fname.startsWith(basename + "_") && (fname.endsWith(".yaml") || fname.endsWith(".yml"))) {
									try (FileInputStream stream = new FileInputStream(path.toString())) {
										Bundle b = bundleReader.read(stream);
										b.getMeta().filePath = path.toString();
										bundleList.add(b);
									}
								}
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// create a BundleCollection from all loaded Bundles
						BundleCollection collection = collectionReader.read(bundleList.toArray(new Bundle[0]));
						BundleCollectionViewModel collectionVm = new BundleCollectionViewModel(collection, basename, dialogService, getUndoSupport(), loc);
						collectionVm.setPath(folder.toString());

						setCurrentCollection(collectionVm);		
						
						getUndoSupport().flush();
					}
				});		
		
		closeCollectionCommand = new RelayCommand( () -> {
			// if the current collection has unsaved changes,
			// aks the user whether he wants to save before closing
			if (getCurrentCollection().getHasUnsavedChanges()) {
				if (dialogService.showQuestionDialog("general:saveChangesBeforeClose")) {
					getCurrentCollection().getSaveCollectionCommand().execute();
				} else
					return;
			}
			
			setCurrentCollection((BundleCollectionViewModel)null);
		}, () -> getCurrentCollection() != null);
	}
	
	public void checkOpenedFilesChanged() {
		if (currentCollection != null) {
			if (currentCollection.checkBundleFilesChanged()) {
				if (dialogService.showQuestionDialog(loc.getString("general:filesChangedOutside"))) {
					// TODO: reload the collection
				} else {
					// ignore the current changes
					currentCollection.updateFileTrackIds();
				}
			}
		}
	}

	// --------------------------
	// Property setters / getters
	// --------------------------

	public BundleCollectionViewModel getCurrentCollection() {
		return currentCollection;
	}

	public void setCurrentCollection(BundleCollectionViewModel newValue) {
		changeProperty("currentCollection", newValue);
	}
	
	public void setCurrentCollection(BundleCollection newValue) {
		BundleCollectionViewModel vm = new BundleCollectionViewModel(newValue, "", dialogService, getUndoSupport(), loc);
		setCurrentCollection(vm);
	}
	
	public ImportViewModel getImportViewModel() {
		return importViewModel;
	}
	
	// ---------------
	// Command getters
	// ---------------

	/**
	 * The command to create a new collection
	 */
	public ICommand getNewCollectionCommand() {
		return newCollectionCommand;
	}
	
	/**
	 * The command to load a bundle (collection) from files
	 */
	public ICommand getLoadCollectionCommand() {
		return loadCollectionCommand;
	}	
	
	/**
	 * The command to close the currently open collection
	 */
	public ICommand getCloseCollectionCommand() {
		return closeCollectionCommand;
	}
}
