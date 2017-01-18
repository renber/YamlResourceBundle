package de.renber.yamlbundleeditor.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;

import java.beans.Beans;
import java.util.ResourceBundle;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wb.swt.SWTResourceManager;

import de.renber.databinding.ComplexBind;
import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.commands.ICommand;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.IValueDataContext;
import de.renber.databinding.converters.FuncConverter;
import de.renber.databinding.providers.ImageLabelProvider;
import de.renber.databinding.providers.PropertyColumnLabelProvider;
import de.renber.databinding.templating.ItemsControl;
import de.renber.databinding.templating.ITemplatingControlFactory;
import de.renber.yamlbundleeditor.controls.DropDownSelectionListener;
import de.renber.yamlbundleeditor.controls.WatermarkText;
import de.renber.yamlbundleeditor.mvvm.BindableElementFilter;
import de.renber.yamlbundleeditor.mvvm.BindableTreeFilter;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IconProvider;
import de.renber.yamlbundleeditor.services.impl.DesignTimeLocalizationService;
import de.renber.yamlbundleeditor.utils.providers.ResourceKeyLabelProvider;
import de.renber.yamlbundleeditor.viewmodels.datatypes.ResourceKeyViewModel;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.FocusCellHighlighter;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ResourceKeyView extends Composite {

	IValueDataContext dataContext;
	DataBindingContext bindingContext;
	CommandManager commandManager;

	private Composite treeComposite;
	private TreeViewer treeViewer;
	private Group grpSelectedKey;
	private Label lblKeyPath;
	private Label lblPath;
	private Label lblResourceType;
	private Combo combo;
	private Label lblComment;
	private Text textComment;
	private Group grpLocalizedValues;
	private ItemsControl localizedValuePresenter;
	private SashForm sashForm;
	private ScrolledComposite scrolledComposite;
	private Button btnAddLocalizedValues;
	private Composite rightComposite;
	private Composite leftComposite;
	private ToolBar toolBarKeyTree;	
	private ToolItem tltmKeyAdd;
	private Composite composite_1;
	private Button btnCopyPathToClipboard;
	private ToolItem tltmKeyRemove;
	private Button btnRenameKey;
	private WatermarkText txtFilterKeys;
	private Composite compositeFilter;	
	private ToolBar toolBarFilter;
	private ToolItem tltmResetFilter;	
	private ToolItem tltmJumpToKey;	
	private ToolItem tltmFind;
	private ToolItem tltmFindNext;	
	private Label lblSeparator;		
	private ToolItem tltmFilterDropDown;
	private MenuItem mtmOnlyShowMissing;	
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ResourceKeyView(Composite parent, int style, IValueDataContext dataContext, ILocalizationService localizationService) {
		super(parent, style);

		this.dataContext = dataContext;

		if (Beans.isDesignTime())
			localizationService = new DesignTimeLocalizationService();

		createContents(localizationService);

		if (!Beans.isDesignTime())
			setupBindings(localizationService);
	}

	protected void createContents(ILocalizationService loc) {
		setLayout(new GridLayout(1, false));

		sashForm = new SashForm(this, SWT.BORDER | SWT.SMOOTH);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		leftComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_leftComposite = new GridLayout(1, false);
		gl_leftComposite.marginHeight = 0;
		gl_leftComposite.marginWidth = 0;
		gl_leftComposite.horizontalSpacing = 0;
		gl_leftComposite.verticalSpacing = 0;
		leftComposite.setLayout(gl_leftComposite);				

		toolBarKeyTree = new ToolBar(leftComposite, SWT.FLAT | SWT.RIGHT);
		toolBarKeyTree.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolBarKeyTree.setSize(221, 22);

		tltmKeyAdd = new ToolItem(toolBarKeyTree, SWT.NONE);
		tltmKeyAdd.setImage(IconProvider.getImage("key_add"));
		tltmKeyAdd.setToolTipText(loc.getString("keyEditor:addKey:tooltip"));

		tltmKeyRemove = new ToolItem(toolBarKeyTree, SWT.NONE);
		tltmKeyRemove.setImage(IconProvider.getImage("key_delete"));
		tltmKeyRemove.setToolTipText(loc.getString("keyEditor:removeKey:tooltip"));
		
		ToolItem toolItem = new ToolItem(toolBarKeyTree, SWT.SEPARATOR);
		
		tltmFind = new ToolItem(toolBarKeyTree, SWT.NONE);
		tltmFind.setImage(IconProvider.getImage("find"));
		tltmFind.setToolTipText(loc.getString("keyEditor:find:tooltip"));
		
		tltmFindNext = new ToolItem(toolBarKeyTree, SWT.NONE);
		tltmFindNext.setImage(IconProvider.getImage("find_next"));
		tltmFindNext.setToolTipText(loc.getString("keyEditor:findNext:tooltip"));
		
		ToolItem toolItem_1 = new ToolItem(toolBarKeyTree, SWT.SEPARATOR);
		
		tltmJumpToKey = new ToolItem(toolBarKeyTree, SWT.NONE);
		tltmJumpToKey.setImage(IconProvider.getImage("jump_to"));
		tltmJumpToKey.setToolTipText(loc.getString("keyEditor:jumpToKey:tooltip"));							
		
		lblSeparator = new Label(leftComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblSeparator.setText("New Label");
		
		compositeFilter = new Composite(leftComposite, SWT.NONE);
		GridLayout gl_compositeFilter = new GridLayout(2, false);
		gl_compositeFilter.marginHeight = 2;
		gl_compositeFilter.marginRight = 1;
		gl_compositeFilter.marginWidth = 0;
		gl_compositeFilter.horizontalSpacing = 0;
		compositeFilter.setLayout(gl_compositeFilter);
		compositeFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		txtFilterKeys = new WatermarkText(compositeFilter, SWT.BORDER, loc.getString("keyEditor:filter"));		
		txtFilterKeys.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		toolBarFilter = new ToolBar(compositeFilter, SWT.FLAT | SWT.RIGHT);
		
		tltmResetFilter = new ToolItem(toolBarFilter, SWT.NONE);
		tltmResetFilter.setImage(IconProvider.getImage("reset_filter"));		
		tltmResetFilter.addListener(SWT.Selection, (e) -> {
			// reset the filter text
			txtFilterKeys.setText("");
		});
		
		ToolItem toolItem_2 = new ToolItem(toolBarFilter, SWT.SEPARATOR);
		
		tltmFilterDropDown = new ToolItem(toolBarFilter, SWT.CHECK);
		tltmFilterDropDown.setImage(IconProvider.getImage("filter"));
		DropDownSelectionListener dropDownListener = new DropDownSelectionListener(SWT.NONE, tltmFilterDropDown);
		mtmOnlyShowMissing = dropDownListener.addMenuItem(loc.getString("keyEditor:filter:onlyShowMissing"), SWT.CHECK);
		tltmFilterDropDown.addSelectionListener(dropDownListener);		

		treeComposite = new Composite(leftComposite, SWT.NONE);
		treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));		
		GridLayout gl_treeComposite = new GridLayout(1, false);
		gl_treeComposite.marginRight = 2;
		gl_treeComposite.marginLeft = 2;
		gl_treeComposite.marginTop = 2;
		gl_treeComposite.marginHeight = 0;
		gl_treeComposite.marginWidth = 0;
		treeComposite.setLayout(gl_treeComposite);

		treeViewer = new TreeViewer(treeComposite, SWT.FULL_SELECTION);
		treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));		

		rightComposite = new Composite(sashForm, SWT.NONE);
		rightComposite.setLayout(new GridLayout(1, false));

		grpSelectedKey = new Group(rightComposite, SWT.NONE);
		grpSelectedKey.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_grpSelectedKey = new GridLayout(2, false);
		grpSelectedKey.setLayout(gl_grpSelectedKey);
		grpSelectedKey.setText(loc.getString("keyEditor:properties"));

		lblKeyPath = new Label(grpSelectedKey, SWT.NONE);
		lblKeyPath.setText(loc.getString("keyEditor:properties:keyPath") + ":");

		composite_1 = new Composite(grpSelectedKey, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(3, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);

		lblPath = new Label(composite_1, SWT.NONE);
		lblPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblPath.setSize(223, 15);
		lblPath.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblPath.setText("path of the key");

		btnRenameKey = new Button(composite_1, SWT.NONE);
		btnRenameKey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnRenameKey.setToolTipText(loc.getString("keyEditor:renameKey:tooltip"));
		btnRenameKey.setImage(IconProvider.getImage("rename"));

		btnCopyPathToClipboard = new Button(composite_1, SWT.NONE);
		btnCopyPathToClipboard.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCopyPathToClipboard.setToolTipText(loc.getString("keyEditor:copyPathToClipboard:tooltip"));
		btnCopyPathToClipboard.setImage(IconProvider.getImage("copy"));

		lblResourceType = new Label(grpSelectedKey, SWT.NONE);
		lblResourceType.setText(loc.getString("keyEditor:properties:keyType") + ":");

		combo = new Combo(grpSelectedKey, SWT.NONE);
		combo.setEnabled(false);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.setText("Text");

		lblComment = new Label(grpSelectedKey, SWT.NONE);
		lblComment.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblComment.setText(loc.getString("keyEditor:properties:comment") + ":");

		textComment = new Text(grpSelectedKey, SWT.BORDER | SWT.MULTI);
		GridData gd_textComment = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textComment.widthHint = 174;
		gd_textComment.heightHint = 45;
		textComment.setLayoutData(gd_textComment);

		grpLocalizedValues = new Group(grpSelectedKey, SWT.NONE);
		grpLocalizedValues.setLayout(new GridLayout(1, false));
		grpLocalizedValues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpLocalizedValues.setText(loc.getString("keyEditor:properties:localizedValues"));

		btnAddLocalizedValues = new Button(grpLocalizedValues, SWT.NONE);
		btnAddLocalizedValues.setText(loc.getString("keyEditor:properties:addLocalizedValues"));

		scrolledComposite = new ScrolledComposite(grpLocalizedValues, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);

		localizedValuePresenter = new ItemsControl(scrolledComposite, SWT.None);
		scrolledComposite.setContent(localizedValuePresenter);
		scrolledComposite.setMinSize(new Point(10, 10));

		sashForm.setWeights(new int[] { 2, 3 });
	}
	
	void UpdateFilterToolItemSelection() {
		// indicate active filters on the filter dropdown button
		tltmFilterDropDown.setSelection(mtmOnlyShowMissing.getSelection());
	}

	protected void setupBindings(ILocalizationService loc) {
		bindingContext = new DataBindingContext();
		ComplexBind bind = new ComplexBind();
		commandManager = new CommandManager();

		// bind the hierarchical resource keys to the tree
		bind.tree(treeViewer, dataContext.value("values").observe(), new ResourceKeyObservableFactory(), new ResourceKeyTreeStructureAdvisor(), new ResourceKeyLabelProvider());

		// enable the tree to be filtered (with a slight input delay for the filter text)				
		BindableTreeFilter treeFilter = new BindableTreeFilter(treeViewer, 
				new BindableElementFilter<ResourceKeyViewModel, String>(WidgetProperties.text(SWT.Modify).observeDelayed(250, txtFilterKeys), (item, filterValue) -> item.getPath().toLowerCase().contains(filterValue.toLowerCase()), ""),
				new BindableElementFilter<ResourceKeyViewModel, Boolean>(WidgetProperties.selection().observe(mtmOnlyShowMissing), (item, filterValue) -> !filterValue || item.getHasMissingValues(), false));
		treeViewer.setFilters(treeFilter);	
		
		// update selection indicator when filter changes
		WidgetProperties.selection().observe(mtmOnlyShowMissing).addValueChangeListener((e) -> UpdateFilterToolItemSelection());
		
		tltmFilterDropDown.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// undo auto-toggle behavior
				// since the selection state should depend only depend on active filters
				UpdateFilterToolItemSelection();				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// --
			}});
		
		// only enable filter text when a collection has been loaded 
		bindingContext.bindValue(WidgetProperties.enabled().observe(tltmFilterDropDown), dataContext.observe(), null, UpdateValueStrategy.create(FuncConverter.create(Object.class, Boolean.class, (item) -> item != null)));
		bindingContext.bindValue(WidgetProperties.enabled().observe(txtFilterKeys), dataContext.observe(), null, UpdateValueStrategy.create(FuncConverter.create(Object.class, Boolean.class, (item) -> item != null)));		
		bindingContext.bindValue(WidgetProperties.enabled().observe(tltmResetFilter), WidgetProperties.text(SWT.Modify).observe(txtFilterKeys), null, UpdateValueStrategy.create(FuncConverter.create(String.class, Boolean.class, (text) -> !text.isEmpty())));
		
		// show the details for the selected tree node
		IViewerObservableValue selectedKey = ViewerProperties.singleSelection().observe(treeViewer);

		// bind the selection to the ViewModel
		bindingContext.bindValue(selectedKey, dataContext.value("selectedResourceKey").observe());

		// only show details when a key is selected
		bind.visibility(dataContext.value("selectedResourceKey").observe(), (x) -> x != null, grpSelectedKey, false);			

		// bind the details of the selected resource key
		IObservableValue detailPath = BeanProperties.value("path").observeDetail(selectedKey);
		bindingContext.bindValue(WidgetProperties.text().observe(lblPath), detailPath);

		// bind the comment of the key
		IObservableValue commentPath = BeanProperties.value("comment").observeDetail(selectedKey);
		bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(textComment), commentPath);

		// set the ItemTemplate for the ContentPresenter and bind the localized
		// values to it as item source
		localizedValuePresenter.setItemFactory(new ITemplatingControlFactory() {
			@Override
			public Control createControl(Composite parent, IDataContext itemDataContext) {
				// use the langBundle of this shell
				return new LocalizedValueView(parent, SWT.NONE, itemDataContext, loc);
			}

			@Override
			public Object getLayoutData(Layout parentLayout, Control itemControl, IDataContext itemDataContext) {
				return new GridData(SWT.FILL, SWT.TOP, true, false);
			};
		});

		localizedValuePresenter.setInput(BeanProperties.value("localizedValues").observeDetail(selectedKey));

		// if this node is an intermediate node, allow the user to add values by
		// a button
		bind.visibility(BeanProperties.value("isIntermediateNode").observeDetail(selectedKey), (x) -> x == null ? false : (boolean) x, btnAddLocalizedValues, true);
		bind.visibility(BeanProperties.value("isIntermediateNode").observeDetail(selectedKey), (x) -> x == null ? false : (!(boolean) x), scrolledComposite, true);

		// commands
		commandManager.bind(btnAddLocalizedValues, dataContext.value("selectedResourceKey").value("addMissingValuesCommand"));
		commandManager.bind(btnRenameKey, dataContext.value("selectedResourceKey").value("renameCommand"));
		commandManager.bind(btnCopyPathToClipboard, dataContext.value("selectedResourceKey").value("copyPathToClipboardCommand"));
		commandManager.bind(tltmKeyAdd, dataContext.value("addResourceKeyCommand"));
		commandManager.bind(tltmKeyRemove, dataContext.value("removeResourceKeyCommand"));
		commandManager.bind(tltmFind, dataContext.value("findCommand"));
		commandManager.bind(tltmFindNext, dataContext.value("findNextCommand"));
		commandManager.bind(tltmJumpToKey, dataContext.value("jumpToKeyCommand"));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}

// Classes for binding the ViewModels values list to a TreeViewer

class ResourceKeyObservableFactory implements IObservableFactory {

	@Override
	public IObservable createObservable(Object item) {
		if (item instanceof ResourceKeyViewModel)
			return ((ResourceKeyViewModel) item).getChildren();

		if (item instanceof IObservable)
			return (IObservable) item;

		return null;
	}

}

class ResourceKeyTreeStructureAdvisor extends TreeStructureAdvisor {

	@Override
	public Object getParent(Object element) {
		return ((ResourceKeyViewModel) element).getParent();
	}

	@Override
	public Boolean hasChildren(Object element) {
		return ((ResourceKeyViewModel) element).getHasChildren();
	}
}