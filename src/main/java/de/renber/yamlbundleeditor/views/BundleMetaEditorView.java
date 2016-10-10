package de.renber.yamlbundleeditor.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.renber.databinding.ComplexBind;
import de.renber.databinding.commands.BindableCommand;
import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.commands.ICommand;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.IValueDataContext;
import de.renber.databinding.context.beans.BeansValueDataContext;
import de.renber.databinding.converters.FuncConverter;
import de.renber.databinding.providers.ImageLabelProvider;
import de.renber.yamlbundleeditor.controls.BorderPainter;
import de.renber.yamlbundleeditor.utils.DesignTimeResourceBundle;
import de.renber.yamlbundleeditor.viewmodels.MainViewModel;

import java.beans.Beans;
import java.util.ResourceBundle;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class BundleMetaEditorView extends Composite {

	private ResourceBundle langBundle;
	private IDataContext dataContext;
	
	private DataBindingContext bindingContext;
	private CommandManager commandManager;
	
	private Button btnAddLanguage;
	private Group propertyGroup;
	private Label lblAuthor;
	private Label lblLanguage;
	private Label lblLocLanguage;
	private Label lblImage;
	private Label lblIcon;
	private Label lblLanguagecode;
	private Text textName;
	private Text textAuthor;
	private Text textLocalizedName;
	private Text textLanguageCode;
	//private Table table;
	private TableViewerColumn tableColumn;
	private TableViewer metaTableViewer;
	private Composite composite;
	private Button btnRemove;
	private Menu menu;
	private MenuItem mntmReplaceIcon;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public BundleMetaEditorView(Composite parent, int style, IDataContext dataContext, ResourceBundle langBundle) {
		super(parent, style);
		
		this.dataContext = dataContext;
		
		// Use the DesignTimeResourceBundle in WindowBuilder
		if (Beans.isDesignTime()) {
			langBundle = new DesignTimeResourceBundle();
		}
		
		createContents(langBundle);
		
		if (!Beans.isDesignTime())
			setupBindings();
	}

	/**
	 * Create contents of the composite
	 */
	protected void createContents(ResourceBundle langBundle) {
		GridLayout gridLayout = new GridLayout(2, false);
		setLayout(gridLayout);		
		
		// create a table with only one column which spans the whole width and looks like a List
		// We  need to use Table since List does not support displaying images through the ILabelProvider
		Composite tableComposite = new Composite(this, SWT.BORDER);
		
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2);
		gd_table.widthHint = 237;
		tableComposite.setLayoutData(gd_table);		
		
		metaTableViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);		
		Table table = metaTableViewer.getTable();
		tableColumn = new TableViewerColumn(metaTableViewer, SWT.NONE);
		tableColumn.getColumn().setResizable(false);
		
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableComposite.setLayout(tableLayout);
		
		tableLayout.setColumnData(tableColumn.getColumn(), new ColumnWeightData(100));
		
		propertyGroup = new Group(this, SWT.NONE);
		propertyGroup.setLayout(new GridLayout(2, false));
		propertyGroup.setText(langBundle.getString("collectionEditor:properties"));
		propertyGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		lblImage = new Label(propertyGroup, SWT.NONE);
		lblImage.setText(langBundle.getString("collectionEditor:properties:icon") + ":");
		
		lblIcon = new Label(propertyGroup, SWT.NONE);
		lblIcon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblIcon.setText("      ");
		
		menu = new Menu(lblIcon);
		lblIcon.setMenu(menu);
		new BorderPainter(lblIcon);
		
		mntmReplaceIcon = new MenuItem(menu, SWT.NONE);
		mntmReplaceIcon.setText("Replace Icon");
		
		lblLanguagecode = new Label(propertyGroup, SWT.NONE);
		lblLanguagecode.setText(langBundle.getString("collectionEditor:properties:languageCode") + ":");
		
		textLanguageCode = new Text(propertyGroup, SWT.BORDER);
		textLanguageCode.setEditable(false);
		
		lblLanguage = new Label(propertyGroup, SWT.NONE);
		lblLanguage.setText(langBundle.getString("collectionEditor:properties:name") + ":");
		
		textName = new Text(propertyGroup, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblLocLanguage = new Label(propertyGroup, SWT.NONE);
		lblLocLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocLanguage.setText(langBundle.getString("collectionEditor:properties:localizedName") + ":");
		
		textLocalizedName = new Text(propertyGroup, SWT.BORDER);
		textLocalizedName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblAuthor = new Label(propertyGroup, SWT.NONE);
		lblAuthor.setText(langBundle.getString("collectionEditor:properties:author") + ":");
		
		textAuthor = new Text(propertyGroup, SWT.BORDER);
		textAuthor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnAddLanguage = new Button(composite, SWT.NONE);
		btnAddLanguage.setText(langBundle.getString("collectionEditor:addLanguage"));
		
		btnRemove = new Button(composite, SWT.NONE);
		btnRemove.setText(langBundle.getString("collectionEditor:removeLanguage"));
	}
	
	private void setupBindings() {
		commandManager = new CommandManager();
		
		bindingContext = new DataBindingContext();
		ComplexBind bind = new ComplexBind();			
						
		IObservableValue obsList = dataContext.value("bundles").observe();		
		bind.list(metaTableViewer, obsList, new ImageLabelProvider(BeanProperties.value("icon"), BeanProperties.value("friendlyText")));					
		
		// show the details for the selected list item		
		IViewerObservableValue selectedMeta = ViewerProperties.singleSelection().observe(metaTableViewer);
		
		// only show the property group box when an item is selected
		bind.visibility(selectedMeta, (v) -> v != null, propertyGroup, false);		
		
		// bind the selection to the ViewModel
		bindingContext.bindValue(selectedMeta, dataContext.value("selectedBundle").observe());
		
		IObservableValue detailLangCode = BeanProperties.value("languageCode").observeDetail(selectedMeta);
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textLanguageCode), detailLangCode);				
				
		IObservableValue detailName = BeanProperties.value("name").observeDetail(selectedMeta);
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textName), detailName);
		
		IObservableValue detailLocalizedName = BeanProperties.value("localizedName").observeDetail(selectedMeta);
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textLocalizedName), detailLocalizedName);
		
		IObservableValue detailIcon = BeanProperties.value("icon").observeDetail(selectedMeta);
		bindingContext.bindValue(WidgetProperties.image().observe(lblIcon), detailIcon);
		
		IObservableValue detailAuthor = BeanProperties.value("author").observeDetail(selectedMeta);
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textAuthor), detailAuthor);		
		
		// commands				
		commandManager.bind(btnAddLanguage, dataContext.value("addBundleCommand"));
		commandManager.bind(btnRemove, dataContext.value("removeSelectedBundleCommand"));
		commandManager.bind(mntmReplaceIcon, dataContext.value("selectedBundle").value("replaceIconCommand"));		
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}	
}
