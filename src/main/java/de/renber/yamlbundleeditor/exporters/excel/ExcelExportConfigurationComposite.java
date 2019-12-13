package de.renber.yamlbundleeditor.exporters.excel;

import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.templating.ITemplatingControlFactory;
import net.miginfocom.swt.MigLayout;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;

import de.renber.databinding.context.IDataContext;
import de.renber.databinding.templating.ItemsControl;

import org.eclipse.swt.layout.GridData;

import java.beans.Beans;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.custom.ScrolledComposite;

public class ExcelExportConfigurationComposite extends Composite {

	DataBindingContext bindingContext;
	CommandManager commandManager;
	IDataContext dataContext;

	private Label lblSeparator;
	private Combo comboSeparator;
	private Label lblExportLanguages;
	private Label lblExportFilter;
	private StyledText txtExportFilter;
	private Label lblOnlyExportMissingValues;
	private Button checkOnlyExportMissingValues;
	private Label lblHighlightMissingValues;
	private Button checkHighlightMissingValues;
	private ScrolledComposite scrolledComposite;
	private ItemsControl languagesToExportControl;
	private Button btnSelectAllLanguages;
	private Button btnDeselectAllLanguages;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ExcelExportConfigurationComposite(Composite parent, int style, IDataContext dataContext) {
		super(parent, style);

		this.dataContext = dataContext;
		
		createContents();
		
		if (!Beans.isDesignTime()) {
			setupBindings();
		}
	}


	private void createContents() {
		setLayout(new MigLayout("", "[][fill]", ""));

		lblSeparator = new Label(this, SWT.NONE);
		lblSeparator.setText("Separator:");

		comboSeparator = new Combo(this, SWT.NONE);
		comboSeparator.setLayoutData("wmin 80, wrap");
		comboSeparator.setItems(new String[] { ":", "_", "/", "\\" });

		lblExportFilter = new Label(this, SWT.NONE);
		lblExportFilter.setText("Only export keys which match\nat least one of these filters:");
		lblExportFilter.setLayoutData("aligny top");

		txtExportFilter = new StyledText(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		txtExportFilter.setAlwaysShowScrollBars(false);
		txtExportFilter.setLayoutData("pushx, growx, hmin 80, wrap");

		lblOnlyExportMissingValues = new Label(this, SWT.NONE);
		lblOnlyExportMissingValues.setText("Only export missing values:");

		checkOnlyExportMissingValues = new Button(this, SWT.CHECK);
		checkOnlyExportMissingValues.setLayoutData("wrap");

		lblHighlightMissingValues = new Label(this, SWT.NONE);
		lblHighlightMissingValues.setText("Highlight missing values:");

		checkHighlightMissingValues = new Button(this, SWT.CHECK);
		checkHighlightMissingValues.setLayoutData("wrap");

		lblExportLanguages = new Label(this, SWT.NONE);
		lblExportLanguages.setLayoutData("aligny top");
		lblExportLanguages.setText("Languages to export:");

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData("push, grow, wrap");
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		languagesToExportControl = new ItemsControl(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(languagesToExportControl);
		scrolledComposite.setMinSize(languagesToExportControl.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		btnSelectAllLanguages = new Button(this, SWT.NONE);
		btnSelectAllLanguages.setText("Select all");
		btnSelectAllLanguages.setLayoutData("skip, split 2");

		btnDeselectAllLanguages = new Button(this, SWT.NONE);
		btnDeselectAllLanguages.setText("Deselect all");
	}
	
	private void setupBindings() {		
		bindingContext = new DataBindingContext();
		commandManager = new CommandManager();

		bindingContext.bindValue(WidgetProperties.text().observe(comboSeparator), dataContext.value("levelSeparator").observe());
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(txtExportFilter), dataContext.value("exportFilter").observe());
		bindingContext.bindValue(WidgetProperties.selection().observe(checkOnlyExportMissingValues), dataContext.value("onlyExportKeysWithMissingValues").observe());
		bindingContext.bindValue(WidgetProperties.selection().observe(checkHighlightMissingValues), dataContext.value("highlightMissingValues").observe());
		
		languagesToExportControl.setItemFactory(new ITemplatingControlFactory() {
			@Override
			public Control create(Composite composite, IDataContext dataContext) {
				Button check = new Button(composite, SWT.CHECK);
				check.setText(dataContext.value("name").getValue().toString());
				bindingContext.bindValue(WidgetProperties.selection().observe(check), dataContext.value("selected").observe());
				return check;
			}

			@Override
			public Object getLayoutData(Layout layout, Control control, IDataContext dataContext) {
				return null;
			}
		});
		languagesToExportControl.setInput(dataContext.value("languagesToExport").observe());		

		commandManager.bind(btnSelectAllLanguages, dataContext.value("selectAllLanguagesCommand"));
		commandManager.bind(btnDeselectAllLanguages, dataContext.value("deselectAllLanguagesCommand"));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
