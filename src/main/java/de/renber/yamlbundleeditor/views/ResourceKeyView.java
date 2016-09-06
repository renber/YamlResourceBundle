package de.renber.yamlbundleeditor.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;

import java.beans.Beans;
import java.util.ResourceBundle;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.value.IObservableValue;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wb.swt.SWTResourceManager;

import de.renber.databinding.ComplexBind;
import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.commands.ICommand;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.providers.ImageLabelProvider;
import de.renber.databinding.providers.PropertyColumnLabelProvider;
import de.renber.databinding.templating.ContentPresenter;
import de.renber.databinding.templating.ITemplatingCompositeFactory;
import de.renber.yamlbundleeditor.services.IconProvider;
import de.renber.yamlbundleeditor.utils.DesignTimeResourceBundle;
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

	IDataContext dataContext;
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
	private ContentPresenter localizedValuePresenter;
	private SashForm sashForm;
	private ScrolledComposite scrolledComposite;
	private Button btnAddLocalizedValues;
	private Composite rightComposite;
	private Composite leftComposite;
	private ToolBar toolBar;
	private ToolItem tltmKeyAdd;
	private Composite composite_1;
	private Button btnCopyPathToClipboard;
	private ToolItem tltmKeyRemove;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ResourceKeyView(Composite parent, int style, IDataContext dataContext, ResourceBundle langBundle) {
		super(parent, style);

		this.dataContext = dataContext;

		if (Beans.isDesignTime())
			langBundle = new DesignTimeResourceBundle();

		createContents(langBundle);

		if (!Beans.isDesignTime())
			setupBindings(langBundle);
	}

	protected void createContents(ResourceBundle langBundle) {
		setLayout(new GridLayout(1, false));

		sashForm = new SashForm(this, SWT.BORDER | SWT.SMOOTH);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		leftComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_leftComposite = new GridLayout(1, false);
		gl_leftComposite.verticalSpacing = 0;
		leftComposite.setLayout(gl_leftComposite);

		toolBar = new ToolBar(leftComposite, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		treeComposite = new Composite(leftComposite, SWT.NONE);
		treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeComposite.setSize(156, 371);
		treeComposite.setLayout(new GridLayout(1, false));		

		tltmKeyAdd = new ToolItem(toolBar, SWT.NONE);
		tltmKeyAdd.setImage(IconProvider.getImage("key_add"));
		
		tltmKeyRemove = new ToolItem(toolBar, SWT.NONE);
		tltmKeyRemove.setImage(IconProvider.getImage("key_delete"));		

		treeViewer = new TreeViewer(treeComposite, SWT.FULL_SELECTION);		
		treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));		
		
		rightComposite = new Composite(sashForm, SWT.NONE);
		rightComposite.setLayout(new GridLayout(1, false));

		grpSelectedKey = new Group(rightComposite, SWT.NONE);
		grpSelectedKey.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_grpSelectedKey = new GridLayout(2, false);
		grpSelectedKey.setLayout(gl_grpSelectedKey);
		grpSelectedKey.setText(langBundle.getString("keyEditor:properties"));

		lblKeyPath = new Label(grpSelectedKey, SWT.NONE);
		lblKeyPath.setText(langBundle.getString("keyEditor:properties:keyPath") + ":");

		composite_1 = new Composite(grpSelectedKey, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(2, false);
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

		btnCopyPathToClipboard = new Button(composite_1, SWT.NONE);
		btnCopyPathToClipboard.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCopyPathToClipboard.setImage(IconProvider.getImage("copy"));

		lblResourceType = new Label(grpSelectedKey, SWT.NONE);
		lblResourceType.setText(langBundle.getString("keyEditor:properties:keyType") + ":");

		combo = new Combo(grpSelectedKey, SWT.NONE);
		combo.setEnabled(false);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.setText("Text");

		lblComment = new Label(grpSelectedKey, SWT.NONE);
		lblComment.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblComment.setText(langBundle.getString("keyEditor:properties:comment") + ":");

		textComment = new Text(grpSelectedKey, SWT.BORDER | SWT.MULTI);
		GridData gd_textComment = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textComment.widthHint = 174;
		gd_textComment.heightHint = 45;
		textComment.setLayoutData(gd_textComment);

		grpLocalizedValues = new Group(grpSelectedKey, SWT.NONE);
		grpLocalizedValues.setLayout(new GridLayout(1, false));
		grpLocalizedValues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpLocalizedValues.setText(langBundle.getString("keyEditor:properties:localizedValues"));

		btnAddLocalizedValues = new Button(grpLocalizedValues, SWT.NONE);
		btnAddLocalizedValues.setText(langBundle.getString("keyEditor:properties:addLocalizedValues"));

		scrolledComposite = new ScrolledComposite(grpLocalizedValues, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);

		localizedValuePresenter = new ContentPresenter(scrolledComposite, SWT.None);
		scrolledComposite.setContent(localizedValuePresenter);
		scrolledComposite.setMinSize(new Point(10, 10));

		sashForm.setWeights(new int[] { 2, 3 });
	}

	protected void setupBindings(ResourceBundle langBundle) {
		bindingContext = new DataBindingContext();
		ComplexBind bind = new ComplexBind();
		commandManager = new CommandManager();

		// bind the hierarchical resource keys to the tree
		bind.tree(treeViewer, dataContext.value("values").observe(), new ResourceKeyObservableFactory(), new ResourceKeyTreeStructureAdvisor(), new ResourceKeyLabelProvider());

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
		localizedValuePresenter.setItemFactory(new ITemplatingCompositeFactory() {
			@Override
			public Composite createComposite(Composite parent, IDataContext itemDataContext) {
				// use the langBundle of this shell
				return new LocalizedValueView(parent, SWT.NONE, itemDataContext, langBundle);
			}

			@Override
			public Object getLayoutData(Composite itemComposite, IDataContext itemDataContext) {
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
		commandManager.bind(tltmKeyAdd, dataContext.value("addResourceKeyCommand"));
		commandManager.bind(tltmKeyRemove, dataContext.value("removeResourceKeyCommand"));		
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
			return  ((ResourceKeyViewModel) item).getChildren();
		
		if (item instanceof IObservable)
			return (IObservable)item;		
		
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