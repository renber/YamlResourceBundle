package de.renber.yamlbundleeditor.viewmodels.datatypes;

import java.io.File;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.redoundo.AutoUndoable;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.IUndoSupport;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;
import de.renber.yamlbundleeditor.utils.FileRevisionId;

public class BundleMetaViewModel extends DataViewModelBase<BundleMetaInfo> {	
	
	IDialogService dialogService;
	
	// revision id to track outside changes
	FileRevisionId revisionId;
	
	ICommand replaceIconCommand;
	
	public BundleMetaViewModel(BundleMetaInfo model, IDialogService dialogService, IUndoSupport undoSupport) {
		super(model, undoSupport);
		
		this.dialogService = dialogService;
		createCommands();
	}
	
	private void createCommands() {
		replaceIconCommand = new RelayCommand( () -> {			
			File f = dialogService.showOpenFileDialog("dialogs:file:loadImage:title", new FileExtFilter("dialogs:file:extensions:descriptions:images", "*.png;"));
			
			if (f != null) {
				ImageLoader imgLoader = new ImageLoader();
				Image img = new Image(Display.getDefault(), imgLoader.load(f.toString())[0]);
				
				if (img != null)
					setIcon(img);
			}
			
		});
	}
	
	// wrapping getters/setters
	
	/**
	 * Return the LanguageCode (cannot be edited)
	 */
	public String getLanguageCode() {
		return model.languageCode;
	}
	
	@AutoUndoable
	public String getName() {
		return model.name;
	}
	
	public void setName(String newValue) {
		changeProperty(model,  "name", newValue);
		firePropertyChanged("friendlyText", "", getFriendlyText());
	}
	
	@AutoUndoable
	public String getLocalizedName() {
		return model.localizedName;
	}
	
	public void setLocalizedName(String newValue) {
		changeProperty(model,  "localizedName", newValue);
		firePropertyChanged("friendlyText", "", getFriendlyText());
	}
	
	@AutoUndoable
	public String getAuthor() {
		return model.author;
	}
	
	public void setAuthor(String newValue) {
		changeProperty(model,  "author", newValue);
	}	
	
	@AutoUndoable
	public Image getIcon() {
		return model.flagImage;
	}
	
	public void setIcon(Image newValue) {
		changeProperty(model, "flagImage", "icon", newValue);
	}
	
	public String getFriendlyText() {
		if (getLanguageCode() != null && !getLanguageCode().isEmpty())		
			return getName() + " (" + getLanguageCode() + ")";
		
		return getName();
	}
	
	public void setRevisionId(FileRevisionId revisionId) {
		this.revisionId = revisionId;
	}
	
	public FileRevisionId getRevisionId() {
		return revisionId;
	}
	
	// ---------------
	// Command getters
	// ---------------
	public ICommand getReplaceIconCommand() {
		return replaceIconCommand;
	}
}
