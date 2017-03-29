package de.renber.yamlbundleeditor.services.impl;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.IValueDataContext;
import de.renber.databinding.templating.ContentPresenter;
import de.renber.databinding.templating.ITemplatingControlFactory;
import de.renber.yamlbundleeditor.viewmodels.ViewCallback;
import de.renber.yamlbundleeditor.viewmodels.WindowedViewModelBase;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.beans.Beans;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;

public class GenericDialog extends Dialog implements ViewCallback {

	IDataContext dataContext;
	ITemplatingControlFactory contentFactory;
	CommandManager commandManager;
	
	protected Shell shell;
	private ContentPresenter contentPresenter;
	private Composite composite_1;
	private Composite composite_2;
	private Button btnOk;
	private Button btnCancel;	

	/**
	 * Create the dialog
	 * DataContext needs to have to properties of type ICommand named okCommand and cancelCommand which will be bound to the appropriate buttons
	 * @param parent
	 * @param style
	 */
	public GenericDialog(Shell parent, int style, String title, IDataContext dataContext, ITemplatingControlFactory contentFactory) {
		super(parent, style);
		setText(title);
		
		this.dataContext = dataContext;
		this.contentFactory = contentFactory;
		
		if (dataContext.getValue() instanceof WindowedViewModelBase) {
			((WindowedViewModelBase)dataContext.getValue()).setViewCallback(this);
		}
		
		createContents();
		
		if (!Beans.isDesignTime()) {
			setupBindings();
		}
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public void open() {		
		shell.open();
		shell.layout();
		
		// center on parent shell
		shell.setLocation(getParent().getLocation().x + (getParent().getSize().x - shell.getSize().x) / 2, getParent().getLocation().y + (getParent().getSize().y - shell.getSize().y) / 2);
		
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		
		contentPresenter = new ContentPresenter(shell, SWT.NONE);
		contentPresenter.setItemFactory(contentFactory);
		contentPresenter.setInput(dataContext.observe());
		contentPresenter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);
		
		composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		btnOk = new Button(composite_2, SWT.NONE);
		btnOk.setText("OK");
		
		btnCancel = new Button(composite_2, SWT.NONE);
		btnCancel.setText("Cancel");

	}
	
	private void setupBindings() {
		this.commandManager = new CommandManager();
		
		commandManager.bind(btnOk, dataContext.value("okCommand"));
		commandManager.bind(btnCancel, dataContext.value("cancelCommand"));
	}

	@Override
	public void requestClose() {
		shell.close();
	}
}