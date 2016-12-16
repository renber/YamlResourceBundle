package de.renber.yamlbundleeditor.views;

import java.beans.Beans;
import java.util.ResourceBundle;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.renber.databinding.ComplexBind;
import de.renber.databinding.commands.BindableCommand;
import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.commands.ICommand;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.IValueDataContext;
import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.databinding.converters.FuncConverter;
import de.renber.resourcebundles.yaml.YamlResourceBundle;
import de.renber.yamlbundleeditor.Starter;
import de.renber.yamlbundleeditor.mvvm.FormatStringConverter;
import de.renber.yamlbundleeditor.services.IconProvider;
import de.renber.yamlbundleeditor.utils.DesignTimeResourceBundle;
import de.renber.yamlbundleeditor.viewmodels.MainViewModel;
import de.renber.yamlbundleeditor.viewmodels.ViewCallback;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;

public class MainView extends Shell implements ViewCallback {
	IDataContext dataContext;	
	DataBindingContext bindingContext;
	CommandManager commandManager;	
	
	ResourceBundle loc;
	
	private Menu menu;
	private MenuItem mntmFile;
	private Menu menu_1;
	private MenuItem mntmCollectionOpen;
	private Group groupLanguages;
	private Group groupResources;
	private ToolBar toolBar;
	private ToolItem tltmCollectionOpen;
	private Composite mainContentComposite;
	private Label label;
	private ToolItem tltmCollectionNew;
	private ToolItem tltmCollectionSave;
	private ToolItem toolItem;
	private ToolItem tltmCollectionSaveAs;
	private CollectionView collectionView;
	private ToolItem toolItem_1;
	private ToolItem tltmCollectionExport;
	private MenuItem mntmCollectionSave;
	private MenuItem mntmCollectionNew;
	private MenuItem mntmCollectionSaveAs;
	private MenuItem mntmQuit;
	private ToolItem tltmUndo;
	private ToolItem tltmRedo;
	private ToolItem toolItem_2;
	private Group groupCollectionInfo;
	private MenuItem mntmEdit;
	private Menu menu_2;
	private MenuItem mntmUndo;
	private MenuItem mntmRedo;
	private MenuItem mntmCollectionExport;
	private MenuItem mntmNewItem;
	private MenuItem mntmTools;
	private Menu menu_3;
	private MenuItem mntmCleanCollection;
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public MainView(Display display, ResourceBundle langBundle, IDataContext dataContext) {
		super(display, SWT.SHELL_TRIM);
		
		this.dataContext = dataContext;		
		((MainViewModel)dataContext.getValue()).setViewCallback(this);		
		
		// Use the DesignTimeResourceBundle and an empty DataContext in WindowBuilder
		if (Beans.isDesignTime()) {
			langBundle = new DesignTimeResourceBundle();
			dataContext = new BeansDataContext(null);
		}
		
		loc = langBundle;
		
		createContents(langBundle);	
		
		if (!Beans.isDesignTime()) {
			setupBindings();		
			setupViewActions();
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents(ResourceBundle langBundle) {
		setText(langBundle.getString("general:applicationTitle"));
		setSize(855, 695);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);
		
		mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText(langBundle.getString("menuBar:file"));
		
		menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		mntmCollectionNew = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionNew.setText(langBundle.getString("menuBar:file:new"));
		
		mntmCollectionOpen = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionOpen.setText(langBundle.getString("menuBar:file:open"));
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		mntmCollectionSave = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionSave.setText(langBundle.getString("menuBar:file:save"));
		
		mntmCollectionSaveAs = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionSaveAs.setText(langBundle.getString("menuBar:file:saveAs"));
		
		mntmNewItem = new MenuItem(menu_1, SWT.SEPARATOR);
		
		mntmCollectionExport = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionExport.setText(langBundle.getString("menuBar:file:export"));
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		mntmQuit = new MenuItem(menu_1, SWT.NONE);
		mntmQuit.setText(langBundle.getString("menuBar:file:quit"));		
		
		mntmEdit = new MenuItem(menu, SWT.CASCADE);
		mntmEdit.setText(langBundle.getString("menuBar:edit"));
		
		menu_2 = new Menu(mntmEdit);
		mntmEdit.setMenu(menu_2);
		
		mntmUndo = new MenuItem(menu_2, SWT.NONE);
		mntmUndo.setText("undo");
		
		mntmRedo = new MenuItem(menu_2, SWT.NONE);
		mntmRedo.setText("redo");
		
		mntmTools = new MenuItem(menu, SWT.CASCADE);
		mntmTools.setText(langBundle.getString("menuBar:tools"));
		
		menu_3 = new Menu(mntmTools);
		mntmTools.setMenu(menu_3);
		
		mntmCleanCollection = new MenuItem(menu_3, SWT.NONE);
		mntmCleanCollection.setText("cleanCollection");
		
		toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		mainContentComposite = new Composite(this, SWT.NONE);
		mainContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mainContentComposite.setLayout(new GridLayout(1, false));
		
		tltmCollectionNew = new ToolItem(toolBar, SWT.NONE);
		tltmCollectionNew.setImage(IconProvider.getImage("collection_new"));
		
		tltmCollectionOpen = new ToolItem(toolBar, SWT.NONE);
		tltmCollectionOpen.setImage(IconProvider.getImage("collection_open"));			
		
		toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
		
		tltmCollectionSave = new ToolItem(toolBar, SWT.NONE);
		tltmCollectionSave.setImage(IconProvider.getImage("collection_save"));
		
		tltmCollectionSaveAs = new ToolItem(toolBar, SWT.NONE);
		tltmCollectionSaveAs.setImage(IconProvider.getImage("collection_saveas"));
		
		toolItem_1 = new ToolItem(toolBar, SWT.SEPARATOR);
		
		tltmUndo = new ToolItem(toolBar, SWT.NONE);
		tltmUndo.setImage(IconProvider.getImage("undo"));
		
		tltmRedo = new ToolItem(toolBar, SWT.NONE);
		tltmRedo.setImage(IconProvider.getImage("redo"));
		
		toolItem_2 = new ToolItem(toolBar, SWT.SEPARATOR);
		toolItem_2.setText("");
		
		tltmCollectionExport = new ToolItem(toolBar, SWT.NONE);
		tltmCollectionExport.setImage(IconProvider.getImage("collection_export"));
		
		groupCollectionInfo = new Group(mainContentComposite, SWT.NONE);
		groupCollectionInfo.setText(langBundle.getString("collectionEditor"));
		GridLayout gl_groupCollectionInfo = new GridLayout(1, false);
		gl_groupCollectionInfo.horizontalSpacing = 0;
		gl_groupCollectionInfo.verticalSpacing = 0;
		gl_groupCollectionInfo.marginWidth = 0;
		gl_groupCollectionInfo.marginHeight = 0;
		groupCollectionInfo.setLayout(gl_groupCollectionInfo);
		groupCollectionInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		collectionView = new CollectionView(groupCollectionInfo, SWT.NONE, dataContext.value("currentCollection"), langBundle);
		collectionView.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					
		ExpandableComposite expComp = new ExpandableComposite(mainContentComposite, ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);
		expComp.setText(langBundle.getString("collectionEditor:availableLanguages"));		
		expComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		groupLanguages = new Group(expComp, SWT.NONE);		
		//groupCollection.setText(langBundle.getString("collectionEditor"));
		GridLayout gl_groupLanguages = new GridLayout(1, false);
		gl_groupLanguages.horizontalSpacing = 0;
		gl_groupLanguages.marginWidth = 0;
		gl_groupLanguages.marginHeight = 0;
		gl_groupLanguages.verticalSpacing = 0;
		groupLanguages.setLayout(gl_groupLanguages);				
		expComp.setClient(groupLanguages);
		
		expComp.addExpansionListener(new ExpansionAdapter() {
			  public void expansionStateChanged(ExpansionEvent e) {
				  mainContentComposite.layout(true);
				  }
				 });
		
		BundleMetaEditorView editor = new BundleMetaEditorView(groupLanguages, SWT.NONE, dataContext.value("currentCollection"), langBundle);
		editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		groupResources = new Group(mainContentComposite, SWT.NONE);
		groupResources.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupResources.setText(langBundle.getString("keyEditor"));
		groupResources.setLayout(new GridLayout(1, false));
		
		ResourceKeyView keyView = new ResourceKeyView(groupResources, SWT.NONE, dataContext.value("currentCollection"), langBundle);		
		keyView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	
	protected void setupBindings() {		
		bindingContext = new DataBindingContext();

		// undo /redo
		bindingContext.bindValue(WidgetProperties.text().observe(mntmUndo), dataContext.value("currentCollection").value("undoDescription").observe(), null,
				UpdateValueStrategy.create(FuncConverter.create(String.class,				
				(s) -> {
					if (s == null)
						return loc.getString("menuBar:edit:undo");
					else
						return loc.getString("menuBar:edit:undo") + " - " + s;
				})));
		bindingContext.bindValue(WidgetProperties.text().observe(mntmRedo), dataContext.value("currentCollection").value("redoDescription").observe(), null,
				UpdateValueStrategy.create(FuncConverter.create(String.class,				
						(s) -> {
							if (s == null)
								return loc.getString("menuBar:edit:redo");
							else
								return loc.getString("menuBar:edit:redo") + " - " + s;
						})));
		
		// setup commands
		commandManager = new CommandManager();
				
		commandManager.bind(mntmCollectionNew, dataContext.value("newCollectionCommand"));
		commandManager.bind(mntmCollectionOpen, dataContext.value("loadCollectionCommand"));
		commandManager.bind(mntmCollectionSave, dataContext.value("currentCollection").value("saveCollectionCommand"));
		commandManager.bind(mntmCollectionSaveAs, dataContext.value("currentCollection").value("saveCollectionAsCommand"));
		commandManager.bind(mntmCollectionExport, dataContext.value("currentCollection").value("exportCollectionCommand"));
		commandManager.bind(mntmUndo, dataContext.value("currentCollection").value("undoCommand"));
		commandManager.bind(mntmRedo, dataContext.value("currentCollection").value("redoCommand"));
		
		commandManager.bind(mntmCleanCollection, dataContext.value("currentCollection").value("cleanCollectionCommand"));
		
		commandManager.bind(tltmCollectionNew, dataContext.value("newCollectionCommand"));
		commandManager.bind(tltmCollectionOpen, dataContext.value("loadCollectionCommand"));		
		commandManager.bind(tltmCollectionSave, dataContext.value("currentCollection").value("saveCollectionCommand"));
		commandManager.bind(tltmCollectionSaveAs, dataContext.value("currentCollection").value("saveCollectionAsCommand"));
		commandManager.bind(tltmCollectionExport, dataContext.value("currentCollection").value("exportCollectionCommand"));
		commandManager.bind(tltmUndo, dataContext.value("currentCollection").value("undoCommand"));
		commandManager.bind(tltmRedo, dataContext.value("currentCollection").value("redoCommand"));		
	}
	
	/**
	 * Setup actions not managed by the ViewModel
	 */
	protected void setupViewActions() {
		// close the window on clicking quit
		mntmQuit.addListener(SWT.Selection, (e) -> close());
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	// ---------------------------
	// ViewCallback implementation
	// ---------------------------	
	
	@Override
	public Shell viewObjectRequested() {
		return this;
	}	
}
