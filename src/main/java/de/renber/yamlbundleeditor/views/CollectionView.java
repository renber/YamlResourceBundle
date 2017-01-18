package de.renber.yamlbundleeditor.views;

import java.beans.Beans;
import java.util.ResourceBundle;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.renber.databinding.context.IDataContext;
import de.renber.yamlbundleeditor.services.ILocalizationService;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class CollectionView extends Composite {

	DataBindingContext bindingContext;
	IDataContext dataContext;	
	private Label lblBasisname;
	private Text textBasename;
	private Label lblPfad;
	private Text textPath;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CollectionView(Composite parent, int style, IDataContext dataContext, ILocalizationService localizationService) {
		super(parent, style);		
		this.dataContext = dataContext;
		
		createContents(localizationService);
		
		if (!Beans.isDesignTime())
			setupBindings();
	}
	
	protected void createContents(ILocalizationService loc) {
		setLayout(new GridLayout(2, false));
		
		lblBasisname = new Label(this, SWT.NONE);
		lblBasisname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBasisname.setText("Basisname:");
		
		textBasename = new Text(this, SWT.BORDER);
		textBasename.setEditable(false);
		GridData gd_textBasename = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textBasename.minimumWidth = 120;
		textBasename.setLayoutData(gd_textBasename);
		textBasename.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {												
				// update the width of the text field dynamically								
				layout(new Control[] {textBasename});				
			}
		});
		
		lblPfad = new Label(this, SWT.NONE);
		lblPfad.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPfad.setText("Pfad:");
		
		textPath = new Text(this, SWT.BORDER);
		textPath.setEditable(false);
		textPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}
	
	protected void setupBindings() {
		bindingContext = new DataBindingContext();
		
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textBasename), dataContext.value("basename").observe());
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textPath), dataContext.value("path").observe());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
