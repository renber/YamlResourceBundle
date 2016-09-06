package de.renber.yamlbundleeditor.views;

import java.awt.SystemColor;
import java.util.ResourceBundle;

import org.eclipse.swt.widgets.Composite;

import de.renber.databinding.context.IDataContext;
import de.renber.databinding.converters.FuncConverter;
import de.renber.yamlbundleeditor.controls.WatermarkText;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.events.ModifyEvent;

public class LocalizedValueView extends Composite {
	
	private DataBindingContext bindingContext;
	
	private Label lblLanguage;
	private WatermarkText textValue;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LocalizedValueView(Composite parent, int style, IDataContext dataContext, ResourceBundle langBundle) {
		super(parent, style);

		createContents(langBundle);
		setupBindings(dataContext);
	}

	/**
	 * Create contents of the composite
	 */
	protected void createContents(ResourceBundle langBundle) {
		setLayout(new GridLayout(2, false));
		
		lblLanguage = new Label(this, SWT.NONE);
		GridData gd_lblLanguage = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_lblLanguage.widthHint = 80;
		lblLanguage.setLayoutData(gd_lblLanguage);
		lblLanguage.setText("New Label");
		
		textValue = new WatermarkText(this, SWT.BORDER | SWT.MULTI, langBundle.getString("keyEditor:properties:valueMissing"));
		textValue.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {								
				// since this is a multi-line text field we want it to auto-grow
				// therefore we have to update the parent layout to adapt to the changed height requirements
				// when the user adds/removes a line-break
				getParent().layout();
			}
		});
		// use tab to go to the next Control instead of inserting a tab as text
		textValue.addTraverseListener(new TraverseListener() {
		    public void keyTraversed(TraverseEvent e) {
		        if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
		            e.doit = true;
		        }
		    }
		});
		
		textValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));		
	}
	
	private void setupBindings(IDataContext dataContext) {
		bindingContext = new DataBindingContext();		
		
		bindingContext.bindValue(WidgetProperties.text().observe(lblLanguage), dataContext.value("languageDescription").observe());
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textValue), dataContext.value("value").observe());
		
		// indicate a missing value
		FuncConverter<Boolean, Color> boolToColorConverter = FuncConverter.create(Boolean.class, Color.class, (v) -> v ? null /*default system color*/ : SWTResourceManager.getColor(255, 128, 128));		
		bindingContext.bindValue(WidgetProperties.background().observe(textValue), dataContext.value("hasValue").observe(), null, UpdateValueStrategy.create(boolToColorConverter));				
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
