package de.renber.yamlbundleeditor.exporters.excel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;

import de.renber.databinding.collections.TransformedList;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.viewmodels.datatypes.DataViewModelBase;

public class ExcelExportConfigurationViewModel extends DataViewModelBase<ExcelExportConfiguration> {

	private IObservableList<String> languagesToExport;
	
	public ExcelExportConfigurationViewModel(ExcelExportConfiguration model, BundleCollection forCollection) {
		super(model, null);
		// --
		
		languagesToExport = new TransformedList(model.getLanguagesToExport(), (m) -> m, (vm) -> vm);
		
		for(BundleMetaInfo meta: forCollection.getBundles()) {
			languagesToExport.add(meta.languageCode);
		}
	}
	
	public String getLevelSeparator() {
		return model.levelSeparator;
	}
	
	public void setLevelSeparator(String newValue) {
		changeProperty(model, "levelSeparator", newValue);
	}
	
	public boolean getHighlightMissingValues() {
		return model.highlightMissingValues;
	}
	
	public void setHighlightMissingValues(boolean newValue) {
		changeProperty(model, "highlightMissingValues", newValue);
	}	
	
	public IObservableList<String> getLanguagesToExport() {
		return languagesToExport;
	}	

}
