package de.renber.yamlbundleeditor.services.impl;

import java.io.File;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.Starter;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.viewmodels.ExportViewModel;
import de.renber.yamlbundleeditor.viewmodels.datatypes.BundleCollectionViewModel;
import de.renber.yamlbundleeditor.views.ExportView;

public class DefaultDialogService implements IDialogService {
	
	ILocalizationService loc;
	
	public Shell getParent() {
		return Starter.mainShell;
	}
	
	public DefaultDialogService(ILocalizationService localizationService) {
		this.loc = localizationService;
	}
	
	public boolean showQuestionDialog(String message)
	{		
		return MessageDialog.openQuestion(getParent(), loc.getString("dialogs:confirmation:title"), message);
	}
	
	public void showInformationDialog(String message) {
		MessageDialog.openInformation(getParent(), loc.getString("dialogs:information:title"), message);
	}
	
	@Override
	public File showOpenFileDialog(String title, FileExtFilter...fileFilters) {
		FileDialog dialog = new FileDialog(getParent());
		
		dialog.setText(title);		
		dialog.setFilterExtensions(QuIterables.query(fileFilters).select(x -> x.extensions).toArray(String.class));
		dialog.setFilterNames(QuIterables.query(fileFilters).select(x -> x.name + " (" + x.extensions + ")" ).toArray(String.class));
		
		String fname = dialog.open();
		if (fname == null)
			return null;
		
		return new File(fname);
	}
	
	@Override
	public File showSaveFileDialog(String title, FileExtFilter...fileFilters) {
		FileDialog dialog = new FileDialog(getParent(), SWT.SAVE);
		
		dialog.setText(title);		
		dialog.setFilterExtensions(QuIterables.query(fileFilters).select(x -> x.extensions).toArray(String.class));
		dialog.setFilterNames(QuIterables.query(fileFilters).select(x -> x.name + " (" + x.extensions + ")" ).toArray(String.class));
		
		String fname = dialog.open();
		if (fname == null)
			return null;
		
		return new File(fname);
	}

	@Override
	public void showMessageDialog(String title, String message) {
		MessageDialog.openInformation(getParent(), title, message);
	}

	@Override
	public String showTextPrompt(String title, String message, String defaultValue) {
		return showTextPrompt(title, message, defaultValue, false);
	}
	
	@Override
	public String showTextPrompt(String title, String message, String defaultValue, boolean apppendToDefault) {
		InputDialog dialog = new InputDialog(getParent(), title, message, defaultValue, null);		
		
		dialog.create();		
		
		if (apppendToDefault)
			moveCaretToEnd(dialog.getShell().getChildren());
		
		if (dialog.open() == InputDialog.OK)
		{
			return dialog.getValue();
		} else
			return null;
	}
	
	/**
	 * Find the Text control in list (or nested) and move its caret to the end
	 * @param list
	 * @return
	 */
	boolean moveCaretToEnd(Control[] list) {
		for(Control c: list) {
			if (c instanceof Text) {
				((Text)c).setSelection(((Text)c).getText().length(), ((Text)c).getText().length());
				return true;
			}			
			if (c instanceof Composite) {
				if (moveCaretToEnd(((Composite)c).getChildren()))
					return true;
			}
		}
		
		return false;
	}
	
	public void showExportDialog(BundleCollectionViewModel forCollection) {
		ExportViewModel vm = new ExportViewModel(forCollection, loc, this);
		ExportView view = new ExportView(getParent(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM, loc, new BeansDataContext(vm));
		view.setVisible(true);
	}
}
