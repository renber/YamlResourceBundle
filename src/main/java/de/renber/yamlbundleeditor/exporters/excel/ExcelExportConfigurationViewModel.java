package de.renber.yamlbundleeditor.exporters.excel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.databinding.viewmodels.ViewModelBase;
import org.eclipse.core.databinding.observable.list.IObservableList;

import de.renber.databinding.collections.TransformedList;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.viewmodels.datatypes.DataViewModelBase;
import org.eclipse.core.databinding.observable.list.WritableList;

public class ExcelExportConfigurationViewModel extends DataViewModelBase<ExcelExportConfiguration> {

	private IObservableList<CheckableItem> languagesToExport;

	private ICommand selectAllLanguagesCommand;
	private ICommand deselectAllLanguagesCommand;

	public ExcelExportConfigurationViewModel(ExcelExportConfiguration model, BundleCollection forCollection) {
		super(model, null);
		// --

		languagesToExport = new WritableList();

		for(BundleMetaInfo meta: forCollection.getBundles()) {
			CheckableItem item = new CheckableItem(meta.languageCode, model.getLanguagesToExport().contains(meta.languageCode));
			item.addPropertyChangeListener( (pce) -> {
				CheckableItem sourceItem = (CheckableItem)pce.getSource();

				if ("selected".equals(pce.getPropertyName())) {
					if ((boolean)pce.getNewValue()) {
						if (!model.getLanguagesToExport().contains(sourceItem.getName())) {
							model.getLanguagesToExport().add(sourceItem.getName());
						}
					} else {
						model.getLanguagesToExport().remove(sourceItem.getName());
					}
				}
			});
			languagesToExport.add(item);
		}

		selectAllLanguagesCommand = new RelayCommand( () -> {
			languagesToExport.forEach(x -> x.setSelected(true));
		});

		deselectAllLanguagesCommand = new RelayCommand( () -> {
			languagesToExport.forEach(x -> x.setSelected(false));
		});
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
	
	public IObservableList<CheckableItem> getLanguagesToExport() {
		return languagesToExport;
	}

	public void setOnlyExportKeysWithMissingValues(boolean newValue) {
		changeProperty(model, "onlyExportKeysWithMissingValues", newValue);
	}

	public boolean getOnlyExportKeysWithMissingValues() {
		return model.onlyExportKeysWithMissingValues;
	}

	public void setExportFilter(String newValue) {
		changeProperty(model, "exportFilter", newValue);
	}

	public String getExportFilter() {
		return model.exportFilter;
	}

	public ICommand getSelectAllLanguagesCommand() {
		return selectAllLanguagesCommand;
	}

	public ICommand getDeselectAllLanguagesCommand() {
		return deselectAllLanguagesCommand;
	}

	public static class CheckableItem extends ViewModelBase {

		private boolean selected;
		private String name;

		public CheckableItem(String name, boolean selected) {
			this.name = name;
			this.selected = selected;
		}

		public String getName() {
			return name;
		}

		public void setSelected(boolean newValue) {
			changeProperty("selected", newValue);
		}

		public boolean isSelected() {
			return selected;
		}

	}

}
