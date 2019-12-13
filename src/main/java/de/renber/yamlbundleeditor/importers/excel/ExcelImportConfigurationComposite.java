package de.renber.yamlbundleeditor.importers.excel;

import java.beans.Beans;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.renber.databinding.ComplexBind;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.providers.ControlCellProvider;
import de.renber.databinding.providers.PropertyColumnLabelProvider;
import de.renber.databinding.templating.ITemplatingControlFactory;
import de.renber.databinding.templating.ItemsControl;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.layout.TableColumnLayout;

public class ExcelImportConfigurationComposite extends Composite {

	DataBindingContext bindingContext;
	IDataContext dataContext;
	private Label lblLanguagesContainedIn;		
	private Composite tableComposite;
	private TableViewer languagesTableViewer;
	private Label lblNewLabel;
	private Combo comboSeparator;
	private Label lblNewLabel_1;
	private Button cbWarnForNonExistingKeys;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ExcelImportConfigurationComposite(Composite parent, int style, IDataContext dataContext) {
		super(parent, style);
		
		this.dataContext = dataContext;
		
		createContents();
		
		if (!Beans.isDesignTime()) {
			setupBindings();
		}
	}
	
	private void createContents() {
		setLayout(new GridLayout(2, false));
		
		lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText("Separator:");
		
		comboSeparator = new Combo(this, SWT.NONE);
		comboSeparator.setItems(new String[] {":", "_", "/", "\\"});
		GridData gd_comboSeparator = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_comboSeparator.widthHint = 60;
		comboSeparator.setLayoutData(gd_comboSeparator);
		
		lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setText("Warn when importing non-existing keys: ");
		
		cbWarnForNonExistingKeys = new Button(this, SWT.CHECK);
		
		lblLanguagesContainedIn = new Label(this, SWT.WRAP);
		lblLanguagesContainedIn.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		lblLanguagesContainedIn.setText("The following languages are contained in the file to be imported. Please check the languages which shall be imported into the active collection.");
		
		tableComposite = new Composite(this, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_table.widthHint = 237;
		tableComposite.setLayoutData(gd_table);		
		
		languagesTableViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);				
		languagesTableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn importColumn = new TableViewerColumn(languagesTableViewer, SWT.NONE);
		TableViewerColumn languageCodeColumn = new TableViewerColumn(languagesTableViewer, SWT.NONE);		
		TableViewerColumn nameColumn = new TableViewerColumn(languagesTableViewer, SWT.NONE);				
		
		importColumn.getColumn().setText("");		
		languageCodeColumn.getColumn().setText("Language code");				
		nameColumn.getColumn().setText("Name");										
		
		// set label providers
		importColumn.setLabelProvider(new ControlCellProvider(languagesTableViewer.getTable(), new ITemplatingControlFactory() {			
			@Override
			public Control create(Composite parent, IDataContext itemDataContext) {				
				Button checkbox = new Button(parent, SWT.CHECK);				
				bindingContext.bindValue(WidgetProperties.selection().observe(checkbox), itemDataContext.value("include").observe());				
				return checkbox;
			}			
			@Override
			public Object getLayoutData(Layout parentLayout, Control itemControl, IDataContext itemDataContext) {
				// CellProvider does not support LayoutData
				return null;
			}
		}));		
		nameColumn.setLabelProvider(new PropertyColumnLabelProvider(BeanProperties.value("name")));
		languageCodeColumn.setLabelProvider(new PropertyColumnLabelProvider(BeanProperties.value("languageCode")));
		
		// column layout
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableComposite.setLayout(tableLayout);
		tableLayout.setColumnData(importColumn.getColumn(), new ColumnWeightData(5));
		tableLayout.setColumnData(languageCodeColumn.getColumn(), new ColumnWeightData(25));
		tableLayout.setColumnData(nameColumn.getColumn(), new ColumnWeightData(70));			
	}
	
	private void setupBindings() {
		bindingContext = new DataBindingContext();
		ComplexBind bind = new ComplexBind();
					
		bindingContext.bindValue(WidgetProperties.text().observe(comboSeparator), dataContext.value("levelSeparator").observe());
		bindingContext.bindValue(WidgetProperties.selection().observe(cbWarnForNonExistingKeys), dataContext.value("warnForNonExistingKeys").observe());
		
		bind.table(languagesTableViewer, dataContext.value("containedLanguages").observe());		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
