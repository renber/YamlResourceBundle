package de.renber.yamlbundleeditor.importers.excel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;

import de.renber.databinding.collections.TransformedList;
import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.viewmodels.WindowedViewModelBase;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleMetaViewModel;

/**
 * ViewModel to configure excel import
 * @author berre
 *
 */
public class ExcelImportConfigurationViewModel extends WindowedViewModelBase {

	List<ImportMetaViewModel> wrappedContainedLanguages = new ArrayList<ImportMetaViewModel>();
	ObservableList<ImportMetaViewModel> containedLanguages;
	
	ExcelImportConfiguration configuration;
	
	boolean importConfirmed = false;
	
	ICommand okCommand;
	ICommand cancelCommand;
	
	/**
	 * The user confirmed that the import should be carried out
	 */
	public boolean isConfirmed() {
		return importConfirmed;
	}
	
	public ExcelImportConfigurationViewModel(ExcelImportConfiguration configuration, BundleCollection targetCollection, List<ImportBundleInfo> importableBundles) {		
		this.configuration = configuration;
		
		for(ImportBundleInfo bundle: importableBundles) {
			boolean existsInCollection = QuIterables.query(targetCollection.getBundles()).exists(x -> x.languageCode.equals(bundle.languageCode));
			wrappedContainedLanguages.add(new ImportMetaViewModel(bundle, existsInCollection));
		}
		
		containedLanguages = new WritableList<ImportMetaViewModel>(wrappedContainedLanguages, ImportMetaViewModel.class);
				
		// commands
		okCommand = new RelayCommand( () -> {
			importConfirmed = true;
			requestViewClose();
		}, () -> QuIterables.query(wrappedContainedLanguages).exists(x -> x.getInclude()));
		
		cancelCommand = new RelayCommand( () -> {
			importConfirmed = false;
			requestViewClose();
		});
	}
	
	public ObservableList<ImportMetaViewModel> getContainedLanguages() {
		return containedLanguages;
	}
	
	public String getLevelSeparator() {
		return configuration.separator;
	}
	
	public void setLevelSeparator(String newValue) {
		changeProperty(configuration, "separator", newValue);
	}
	
	public ICommand getOkCommand() {
		return okCommand;
	}
	
	public ICommand getCancelCommand() {
		return cancelCommand;
	}
}

class ImportMetaViewModel extends ViewModelBase {
	
	ImportBundleInfo model;	
	boolean existsInCollection;
	
	public ImportMetaViewModel(ImportBundleInfo model, boolean existsInCollection) {
		if (model == null)
			throw new IllegalArgumentException("Parameter model must not be null");
		
		this.model = model;
		
		// preselect bundle for import when it does not exist in the collection yet
		model.includeInImport = !existsInCollection;
	}
	
	public String getLanguageCode() {
		return model.languageCode;
	}
	
	public String getName() {
		return model.name;
	}
	
	public boolean getExistsInCollection() {
		return existsInCollection;
	}
	
	public void setInclude(boolean newValue) {		
		this.changeProperty(model, "includeInImport", "include", newValue);
	}
	
	public boolean getInclude() {
		return model.includeInImport;
	}
	
}