package de.renber.yamlbundleeditor.export.excel;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import de.renber.databinding.ComplexBind;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.templating.ItemsControl;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;

import java.beans.Beans;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.ScrolledComposite;

public class ExcelExportConfigurationComposite extends Composite {

	DataBindingContext bindingContext;
	IDataContext dataContext;

	private Label lblSeparator;
	private Combo comboSeparator;
	private Label lblZuExportierendeSprachen;
	private Label lblNewLabel;
	private Button checkHighlightMissingValues;
	private ScrolledComposite scrolledComposite;
	private ItemsControl languagesToExportControl;

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
		setLayout(new GridLayout(2, false));

		lblSeparator = new Label(this, SWT.NONE);
		lblSeparator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSeparator.setText("Separator:");

		comboSeparator = new Combo(this, SWT.NONE);
		GridData gd_comboSeparator = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_comboSeparator.widthHint = 80;
		comboSeparator.setLayoutData(gd_comboSeparator);
		comboSeparator.setItems(new String[] { ":", "_", "/", "\\" });

		lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Highlight missing values:");

		checkHighlightMissingValues = new Button(this, SWT.CHECK);

		lblZuExportierendeSprachen = new Label(this, SWT.NONE);
		lblZuExportierendeSprachen.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblZuExportierendeSprachen.setText("Languages to export:");

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		languagesToExportControl = new ItemsControl(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(languagesToExportControl);
		scrolledComposite.setMinSize(languagesToExportControl.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void setupBindings() {		
		bindingContext = new DataBindingContext();
		
		bindingContext.bindValue(WidgetProperties.text().observe(comboSeparator), dataContext.value("levelSeparator").observe());
		bindingContext.bindValue(WidgetProperties.selection().observe(checkHighlightMissingValues), dataContext.value("highlightMissingValues").observe());
		
		//languagesToExportControl.setItemFactory(new ITemplatingControlFactory() {});
		languagesToExportControl.setInput(dataContext.value("languagesToExport").observe());		
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
