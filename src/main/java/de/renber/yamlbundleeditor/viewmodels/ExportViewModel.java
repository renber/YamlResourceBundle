package de.renber.yamlbundleeditor.viewmodels;

import java.util.ArrayList;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swt.graphics.Image;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.yamlbundleeditor.exporters.ExportException;
import de.renber.yamlbundleeditor.exporters.IExportConfiguration;
import de.renber.yamlbundleeditor.exporters.IExporter;
import de.renber.yamlbundleeditor.exporters.excel.ExcelExporter;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleCollectionViewModel;

public class ExportViewModel extends WindowedViewModelBase {

	ILocalizationService loc;
	IDialogService dialogService;
	
	IObservableList<ExporterViewModel> availableExporters = new WritableList<ExporterViewModel>();	
	
	ExporterViewModel selectedExporter;
	
	public ExportViewModel(BundleCollectionViewModel forCollection, ILocalizationService localizationService, IDialogService dialogService) {
		
		this.loc = localizationService;
		this.dialogService = dialogService;
		
		// add the included exporters		
		availableExporters.add(new ExporterViewModel(forCollection.getModel(), new ExcelExporter(loc, dialogService)));		
	}	
	
	// ----------------------------
	// Property getters and setters
	// ----------------------------	
	
	/***
	 * Return a list of available exporters
	 */
	public IObservableList<ExporterViewModel> getAvailableExporters() {
		return availableExporters;
	}
	
	public ExporterViewModel getSelectedExporter() {
		return selectedExporter;
	}
	
	public void setSelectedExporter(ExporterViewModel newValue) {
		changeProperty("selectedExporter", newValue);
	}
}

/**
 * Holds information about an exporter
 * @author renber
 */
class ExporterViewModel extends ViewModelBase {
	
	BundleCollection bundleCollection;
	IExporter exporterInstance;
	IExportConfiguration exporterConfig;
	
	ICommand exportCommand;
	
	public String getName() {
		return exporterInstance.getName();
	}
	
	public Image getImage() {
		return exporterInstance.getImage();
	}
	
	public BundleCollection getCollection() {
		return bundleCollection;
	}
	
	public IExporter getExporterInstance() {
		return exporterInstance;
	}
	
	public IExportConfiguration getConfiguration() {
		return exporterConfig;
	}
	
	public ExporterViewModel(BundleCollection bundleCollection, IExporter exporter) {
		this.bundleCollection = bundleCollection;
		exporterInstance = exporter;
		exporterConfig = exporter.getDefaultConfiguration();
		
		exportCommand = new RelayCommand( () -> {
			try {
				exporterInstance.doExport(bundleCollection, exporterConfig);
			} catch (ExportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public ICommand getExportCommand() {
		return exportCommand;
	}
	
}