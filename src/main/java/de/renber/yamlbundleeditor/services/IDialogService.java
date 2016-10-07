package de.renber.yamlbundleeditor.services;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

import de.renber.yamlbundleeditor.services.impl.FileExtFilter;

public interface IDialogService {

	public File showOpenFileDialog(String title, FileExtFilter...fileFilters);
	
	public File showSaveFileDialog(String title, FileExtFilter...fileFilters);
	
	public boolean showQuestionDialog(String message);
	
	public void showInformationDialog(String message);
	
	public void showMessageDialog(String title, String message);
	
	public String showTextPrompt(String title, String message, String defaultValue);
	
	public String showTextPrompt(String title, String message, String defaultValue, boolean apppendToDefault);
}
