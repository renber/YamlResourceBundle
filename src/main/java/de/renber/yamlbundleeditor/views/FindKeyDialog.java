package de.renber.yamlbundleeditor.views;

import java.beans.Beans;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.impl.DesignTimeLocalizationService;
import de.renber.yamlbundleeditor.utils.SearchOptions;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class FindKeyDialog extends Dialog {

	ILocalizationService loc;
	
	protected SearchOptions result = null;
	
	protected Shell shell;
	private Label lblNewLabel;
	private Text searchText;
	private Button chSearchNames;
	private Button chSearchValues;
	private Composite composite;
	private Button btnCancel;
	private Button btnOk;
	private Composite composite_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FindKeyDialog(Shell parent, int style, ILocalizationService localizationService) {
		super(parent, style);
		
		if (Beans.isDesignTime()) {
			this.loc = new DesignTimeLocalizationService();
		} else		
			this.loc = localizationService;		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public SearchOptions open(SearchOptions defaultOptions) {
		createContents();
		
		// center on parent
		shell.setLocation(getParent().getLocation().x + (getParent().getSize().x - shell.getSize().x) / 2, getParent().getLocation().y + (getParent().getSize().y - shell.getSize().y) / 2); 
		
		if (defaultOptions == null) {
			defaultOptions = new SearchOptions();
		}
		
		searchText.setText(defaultOptions.getSearchTerm());
		chSearchNames.setSelection(defaultOptions.searchInNames);
		chSearchValues.setSelection(defaultOptions.searchInValues);
		
		searchText.selectAll();
		
		shell.open();
		shell.layout();			
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return result;
	}
	
	/**
	 * CLose the dialog and signal success to the caller
	 */
	void submitAndClose() {
		result = new SearchOptions();
		result.setSearchTerm(searchText.getText());
		result.searchInNames = chSearchNames.getSelection();
		result.searchInValues = chSearchValues.getSelection();
		shell.close();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		setText(loc.getString("keyEditor:find:prompt:title"));
		
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 182);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		
		shell.addListener(SWT.Traverse, new Listener() {
	          public void handleEvent(Event e) {
	            if (e.detail == SWT.TRAVERSE_RETURN) {
	            	submitAndClose();
	            }
	          }
	        });
		
		lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText(loc.getString("keyEditor:find:prompt:message"));
		
		searchText = new Text(shell, SWT.BORDER);
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		chSearchNames = new Button(shell, SWT.CHECK);
		chSearchNames.setText(loc.getString("keyEditor:find:prompt:searchNames"));
		
		chSearchValues = new Button(shell, SWT.CHECK);
		chSearchValues.setText(loc.getString("keyEditor:find:prompt:searchValues"));
		new Label(shell, SWT.NONE);
		
		composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.horizontalSpacing = 0;
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		
		composite = new Composite(composite_1, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(2, true));		
		btnOk = new Button(composite, SWT.NONE);
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnOk.setText(loc.getString("keyEditor:find:prompt:find"));
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				 submitAndClose();
			}});
		
		btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCancel.setText(loc.getString("general:cancel"));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				 result = null;
				 shell.close();
			}});

	}

}
