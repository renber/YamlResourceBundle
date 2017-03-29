package de.renber.yamlbundleeditor.viewmodels;

import java.util.List;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swt.graphics.Image;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.exporters.ExportException;
import de.renber.yamlbundleeditor.exporters.IExportConfiguration;
import de.renber.yamlbundleeditor.exporters.IExporter;
import de.renber.yamlbundleeditor.exporters.excel.ExcelExporter;
import de.renber.yamlbundleeditor.importers.IImportConfiguration;
import de.renber.yamlbundleeditor.importers.IImporter;
import de.renber.yamlbundleeditor.importers.ImportException;
import de.renber.yamlbundleeditor.importers.excel.ExcelImporter;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleCollectionViewModel;
import de.renber.yamlbundleeditor.viewmodels.datatypes.ResourceKeyViewModel;

public class ImportViewModel extends ViewModelBase {

	MainViewModel mainViewModel;
	ILocalizationService loc;
	IDialogService dialogService;
	
	IObservableList<ImporterViewModel> availableImporters = new WritableList<ImporterViewModel>();		
	
	/**
	 * Creates an ImportViewModel
	 * @param mainViewModel Needed to check the currently opened collection (if any)
	 * @param localizationService
	 * @param dialogService
	 */
	public ImportViewModel(MainViewModel mainViewModel, ILocalizationService localizationService, IDialogService dialogService) {
		
		this.mainViewModel = mainViewModel;		
		this.loc = localizationService;
		this.dialogService = dialogService;
		
		// add the included exporters		
		availableImporters.add(new ImporterViewModel(mainViewModel, new ExcelImporter(loc, dialogService)));		
	}	
	
	// ----------------------------
	// Property getters and setters
	// ----------------------------	
	
	/***
	 * Return a list of available exporters
	 */
	public IObservableList<ImporterViewModel> getAvailableImporters() {
		return availableImporters;
	}
}

/**
 * Holds information about an importer
 * @author renber
 */
class ImporterViewModel extends ViewModelBase {
	
	IImporter importerInstance;
	IImportConfiguration importerConfig;
	
	ICommand importCommand;
	
	public String getName() {
		return importerInstance.getName();
	}
	
	public Image getImage() {
		return importerInstance.getImage();
	}
	
	public IImporter getImporterInstance() {
		return importerInstance;
	}
	
	public IImportConfiguration getConfiguration() {
		return importerConfig;
	}
	
	public ICommand getImportCommand() {
		return importCommand;
	}
	
	 /**
	  * Creates a ViewModel for the given importer
	  * @param mainViewModel Needed to check the currently opened collection (if any)
	  * @param importer
	  */
	public ImporterViewModel(MainViewModel mainViewModel, IImporter importer) {
		importerInstance = importer;
		importerConfig = importer.getDefaultConfiguration();		
		
		importCommand = new RelayCommand( () -> {
			try {
				// if there is an active collection
				// pass it to the importer
				BundleCollectionViewModel targetBundle = mainViewModel.getCurrentCollection();
				if (targetBundle != null) {
					importerInstance.doImport(targetBundle.getModel(), importerConfig);
					// since we do not know what has been changed in the model, recreate the ViewModel		
					mainViewModel.setCurrentCollection(targetBundle.getModel());
				} else {
					// import into a new bundle
					BundleCollection importedBundle = importerInstance.doImport(importerConfig);
										
					mainViewModel.setCurrentCollection(importedBundle);
				}
			} catch (ImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			}
		});
	}	
	
}