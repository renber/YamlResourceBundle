package de.renber.yamlbundleeditor.views;

import java.beans.Beans;
import java.util.ResourceBundle;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
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
import de.renber.databinding.templating.ITemplatingFactory;
import de.renber.databinding.templating.MenuTemplate;
import de.renber.resourcebundles.yaml.YamlResourceBundle;
import de.renber.yamlbundleeditor.Starter;
import de.renber.yamlbundleeditor.controls.DropDownSelectionListener;
import de.renber.yamlbundleeditor.mvvm.FormatStringConverter;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IconProvider;
import de.renber.yamlbundleeditor.services.impl.DesignTimeLocalizationService;
import de.renber.yamlbundleeditor.viewmodels.MainViewModel;
import de.renber.yamlbundleeditor.viewmodels.ViewCallback;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class MainView extends Shell implements ViewCallback {
	IDataContext dataContext;	
	DataBindingContext bindingContext;
	CommandManager commandManager;	
	
	ILocalizationService loc;
	
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
	private ToolItem tltmCollectionImport;
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
	private MenuItem mntmCollectionImport;
	private MenuItem mntmCollectionExport;
	private MenuItem mntmNewItem;
	private MenuItem mntmTools;
	private Menu menu_3;
	private MenuItem mntmCleanCollection;
	private MenuItem mntmLanguage_de;
	private MenuItem mntmLanguage_en;
	private MenuItem mntmFind;
	private MenuItem mntmFindNext;
	private MenuItem mntmCollectionClose;
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public MainView(Display display, ILocalizationService locService, IDataContext dataContext) {
		super(display, SWT.SHELL_TRIM);
		
		this.dataContext = dataContext;		
		((MainViewModel)dataContext.getValue()).setViewCallback(this);		
		
		// Use the DesignTimeResourceBundle and an empty DataContext in WindowBuilder
		if (Beans.isDesignTime()) {
			locService = new DesignTimeLocalizationService();
			dataContext = new BeansDataContext(null);
		}
		
		loc = locService;
		
		createContents();	
		
		if (!Beans.isDesignTime()) {
			setupBindings();		
			setupViewActions();
		}
		
		this.addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent event) {
				((MainViewModel)MainView.this.dataContext.getValue()).checkOpenedFilesChanged();
				event.doit = true;
		      }			
		});
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText(loc.getString("general:applicationTitle"));
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
		mntmFile.setText("menuBar:file");
		loc.localizeWidget(mntmFile);
		
		menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		mntmCollectionNew = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionNew.setText("menuBar:file:new");
		loc.localizeWidget(mntmCollectionNew);
		
		mntmCollectionOpen = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionOpen.setText("menuBar:file:open");
		loc.localizeWidget(mntmCollectionOpen);		
		
		mntmCollectionClose = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionClose.setText("menuBar:file:close");
		loc.localizeWidget(mntmCollectionClose);
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		mntmCollectionSave = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionSave.setText(loc.getString("menuBar:file:save"));
		
		mntmCollectionSaveAs = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionSaveAs.setText(loc.getString("menuBar:file:saveAs"));
		
		mntmNewItem = new MenuItem(menu_1, SWT.SEPARATOR);
		
		mntmCollectionImport = new MenuItem(menu_1, SWT.CASCADE);
		mntmCollectionImport.setText(loc.getString("menuBar:file:import"));
		
		mntmCollectionExport = new MenuItem(menu_1, SWT.NONE);
		mntmCollectionExport.setText(loc.getString("menuBar:file:export"));
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		mntmQuit = new MenuItem(menu_1, SWT.NONE);		
		mntmQuit.setText(loc.getString("menuBar:file:quit"));		
		
		mntmEdit = new MenuItem(menu, SWT.CASCADE);
		mntmEdit.setText("menuBar:edit");
		loc.localizeWidget(mntmEdit);
		
		menu_2 = new Menu(mntmEdit);
		mntmEdit.setMenu(menu_2);
		
		mntmUndo = new MenuItem(menu_2, SWT.NONE);
		mntmUndo.setAccelerator(SWT.CTRL | 'Z');
		mntmUndo.setText("undo");
		
		mntmRedo = new MenuItem(menu_2, SWT.NONE);
		mntmUndo.setAccelerator(SWT.CTRL | 'Y');
		mntmRedo.setText("redo");
		
		new MenuItem(menu_2, SWT.SEPARATOR);
		
		mntmFind = new MenuItem(menu_2, SWT.NONE);
		mntmFind.setText(loc.getString("keyEditor:find"));
		mntmFind.setAccelerator(SWT.CTRL + 'F');
		
		mntmFindNext = new MenuItem(menu_2, SWT.NONE);
		mntmFindNext.setText(loc.getString("keyEditor:findNext"));
		mntmFindNext.setAccelerator(SWT.F3);
		
		mntmTools = new MenuItem(menu, SWT.CASCADE);
		mntmTools.setText(loc.getString("menuBar:tools"));
				
		menu_3 = new Menu(mntmTools);
		mntmTools.setMenu(menu_3);
		
		/*
		mntmCleanCollection = new MenuItem(menu_3, SWT.NONE);
		mntmCleanCollection.setText(loc.getString("menuBar:tools:cleanUntranslated"));
		
		new MenuItem(menu_3, SWT.SEPARATOR);*/
		
		MenuItem mntmLanguage = new MenuItem(menu_3, SWT.CASCADE);
		mntmLanguage.setText("Language");
		
		Menu menu_4 = new Menu(mntmLanguage);
		mntmLanguage.setMenu(menu_4);
		
		mntmLanguage_de = new MenuItem(menu_4, SWT.NONE);
		mntmLanguage_de.setText("Deutsch");
		mntmLanguage_de.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		    	  loc.changeLanguage("de");
		        }});
		
		mntmLanguage_en = new MenuItem(menu_4, SWT.NONE);
		mntmLanguage_en.setText("English");
		mntmLanguage_en.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		    	  loc.changeLanguage("en");
		        }});
		
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
		
		tltmCollectionImport = new ToolItem(toolBar, SWT.NONE);
		tltmCollectionImport.setImage(IconProvider.getImage("collection_import_dropdown"));		
		
		tltmCollectionExport = new ToolItem(toolBar, SWT.NONE);
		tltmCollectionExport.setImage(IconProvider.getImage("collection_export"));
		
		groupCollectionInfo = new Group(mainContentComposite, SWT.NONE);
		groupCollectionInfo.setText(loc.getString("collectionEditor"));
		GridLayout gl_groupCollectionInfo = new GridLayout(1, false);
		gl_groupCollectionInfo.horizontalSpacing = 0;
		gl_groupCollectionInfo.verticalSpacing = 0;
		gl_groupCollectionInfo.marginWidth = 0;
		gl_groupCollectionInfo.marginHeight = 0;
		groupCollectionInfo.setLayout(gl_groupCollectionInfo);
		groupCollectionInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		collectionView = new CollectionView(groupCollectionInfo, SWT.NONE, dataContext.value("currentCollection"), loc);
		collectionView.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					
		ExpandableComposite expComp = new ExpandableComposite(mainContentComposite, ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);
		expComp.setText(loc.getString("collectionEditor:availableLanguages"));
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
		
		BundleMetaEditorView editor = new BundleMetaEditorView(groupLanguages, SWT.NONE, dataContext.value("currentCollection"), loc);
		editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		groupResources = new Group(mainContentComposite, SWT.NONE);
		groupResources.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupResources.setText(loc.getString("keyEditor"));
		groupResources.setLayout(new GridLayout(1, false));
		
		ResourceKeyView keyView = new ResourceKeyView(groupResources, SWT.NONE, dataContext.value("currentCollection"), loc);		
		keyView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	
	protected void setupBindings() {		
		bindingContext = new DataBindingContext();
		commandManager = new CommandManager();		

		// bind list of importers
		ITemplatingFactory<Menu, MenuItem> importerMenuItemFactory = new ITemplatingFactory<Menu, MenuItem>() {			
			@Override
			public MenuItem create(Menu parent, IDataContext itemDataContext) {		
				// create a MenuItem for this importer
				MenuItem mItem = new MenuItem(parent, SWT.NONE);
				mItem.setText((String)itemDataContext.value("name").getValue());
				mItem.setImage((Image)itemDataContext.value("image").getValue());				
				commandManager.bind(mItem, itemDataContext.value("importCommand"));				
				return mItem;				
			}
		};
		
		Menu exportMenu = new Menu(mntmCollectionImport);
		mntmCollectionImport.setMenu(exportMenu);
		MenuTemplate importMenuTemplate = new MenuTemplate(exportMenu);
		importMenuTemplate.setItemFactory(importerMenuItemFactory);
		importMenuTemplate.setInput(dataContext.value("importViewModel").value("availableImporters").observe());
		
		DropDownSelectionListener importDropwDown = new DropDownSelectionListener(SWT.None, tltmCollectionImport);		
		importMenuTemplate = new MenuTemplate(importDropwDown.getMenu());
		importMenuTemplate.setItemFactory(importerMenuItemFactory);
		importMenuTemplate.setInput(dataContext.value("importViewModel").value("availableImporters").observe());
		tltmCollectionImport.addSelectionListener(importDropwDown);
		
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
				
		commandManager.bind(mntmCollectionNew, dataContext.value("newCollectionCommand"));
		commandManager.bind(mntmCollectionOpen, dataContext.value("loadCollectionCommand"));
		commandManager.bind(mntmCollectionClose, dataContext.value("closeCollectionCommand"));		
		commandManager.bind(mntmCollectionSave, dataContext.value("currentCollection").value("saveCollectionCommand"));
		commandManager.bind(mntmCollectionSaveAs, dataContext.value("currentCollection").value("saveCollectionAsCommand"));
		commandManager.bind(mntmCollectionExport, dataContext.value("currentCollection").value("exportCollectionCommand"));
		commandManager.bind(mntmUndo, dataContext.value("currentCollection").value("undoCommand"));
		commandManager.bind(mntmRedo, dataContext.value("currentCollection").value("redoCommand"));
		commandManager.bind(mntmFind, dataContext.value("currentCollection").value("findCommand"));
		commandManager.bind(mntmFindNext, dataContext.value("currentCollection").value("findNextCommand"));
		
		//commandManager.bind(mntmCleanCollection, dataContext.value("currentCollection").value("cleanCollectionCommand"));
		
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
	public void requestClose() {
		this.close();
	}	
}
