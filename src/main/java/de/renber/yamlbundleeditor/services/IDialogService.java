package de.renber.yamlbundleeditor.services;

import java.io.File;
import java.util.function.Function;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.IValueDataContext;
import de.renber.databinding.templating.ITemplatingControlFactory;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;
import de.renber.yamlbundleeditor.utils.SearchOptions;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleCollectionViewModel;

public interface IDialogService {

	public File showOpenFileDialog(String title, FileExtFilter...fileFilters);
	
	public File showSaveFileDialog(String title, FileExtFilter...fileFilters);
	
	public boolean showQuestionDialog(String message);
	
	public void showInformationDialog(String message);
	
	public void showMessageDialog(String title, String message);
	
	public String showTextPrompt(String title, String message, String defaultValue);
	
	public String showTextPrompt(String title, String message, String defaultValue, boolean apppendToDefault);
	
	public SearchOptions showFindKeyDialog(SearchOptions defaultValues);
	
	public void showExportDialog(BundleCollectionViewModel forCollection);
		
	public void showDialogFor(IDataContext dataContext, ITemplatingControlFactory contentFactory);
}
