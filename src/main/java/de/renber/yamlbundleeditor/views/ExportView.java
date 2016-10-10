package de.renber.yamlbundleeditor.views;

import java.beans.Beans;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.renber.databinding.ComplexBind;
import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.databinding.providers.ImageLabelProvider;
import de.renber.databinding.templating.ContentPresenter;
import de.renber.databinding.templating.ITemplatingControlFactory;
import de.renber.yamlbundleeditor.export.IExportConfiguration;
import de.renber.yamlbundleeditor.export.IExporter;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.impl.ResBundleLocalizationService;
import de.renber.yamlbundleeditor.utils.DesignTimeResourceBundle;

public class ExportView extends Shell {

	IDataContext dataContext;
	ILocalizationService loc;
	DataBindingContext bindingContext;
	CommandManager commandManager;	

	protected Object result;
	private Group grpAusgewhlterExporter;
	private Group grpVerfgbareExportformate;
	private Composite exportersListComposite;
	private ListViewer exportersListViewer;
	private Composite composite;
	private Button btnExport;
	private Button btnCancel;
	private ContentPresenter configurationPresenter;

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public ExportView(Shell parent, int style, ILocalizationService localizationService, IDataContext dataContext) {
		super(parent, style);		

		this.setSize(640, 480);		
		// center on parent shell
		this.setLocation(parent.getLocation().x + (parent.getSize().x - this.getSize().x) / 2, parent.getLocation().y + (parent.getSize().y - this.getSize().y) / 2);
		
		this.dataContext = dataContext;

		// Use the DesignTimeResourceBundle and an empty DataContext in
		// WindowBuilder
		if (Beans.isDesignTime()) {
			localizationService = new ResBundleLocalizationService(new DesignTimeResourceBundle());
			dataContext = new BeansDataContext(null);
		}

		loc = localizationService;

		createContents(loc);

		if (!Beans.isDesignTime()) {
			setupBindings();
		}			
	}

	/**
	 * Create contents of the shell.
	 */
	private void createContents(ILocalizationService loc) {		
		GridLayout gridLayout = new GridLayout(1, false);		
		gridLayout.numColumns = 2;
		setLayout(gridLayout);
		
		grpVerfgbareExportformate = new Group(this, SWT.NONE);
		grpVerfgbareExportformate.setText("Verf\u00FCgbare Exportformate");
		grpVerfgbareExportformate.setLayout(new GridLayout(1, false));
		GridData gd_grpVerfgbareExportformate = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_grpVerfgbareExportformate.widthHint = 150;
		grpVerfgbareExportformate.setLayoutData(gd_grpVerfgbareExportformate);
		
		exportersListComposite = new Composite(grpVerfgbareExportformate, SWT.None);
		exportersListComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		exportersListComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		exportersListViewer = new ListViewer(exportersListComposite);		
		

		grpAusgewhlterExporter = new Group(this, SWT.NONE);
		grpAusgewhlterExporter.setText("Auswahl");
		grpAusgewhlterExporter.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpAusgewhlterExporter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		configurationPresenter = new ContentPresenter(grpAusgewhlterExporter, SWT.NONE);

		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnExport = new Button(composite, SWT.NONE);
		btnExport.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnExport.setText("export");

		btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setText("cancel");
	}

	protected void setupBindings() {
		bindingContext = new DataBindingContext();

		ComplexBind bind = new ComplexBind();
		
		IObservableValue obsList = dataContext.value("availableExporters").observe();		
		bind.list(exportersListViewer, obsList, new ImageLabelProvider(null, BeanProperties.value("name")));					
		
		// show the details for the selected list item		
		IViewerObservableValue selectedExporter = ViewerProperties.singleSelection().observe(exportersListViewer);
		
		// bind the selection to the ViewModel
		bindingContext.bindValue(selectedExporter, dataContext.value("selectedExporter").observe());
		
		// show the configuration control for the selected exporter
		configurationPresenter.setItemFactory(new ITemplatingControlFactory() {
			@Override
			public Control createControl(Composite parent, IDataContext itemDataContext) {				
				return ((IExporter)itemDataContext.value("exporterInstance").getValue()).getConfigurationControl(parent, (BundleCollection)itemDataContext.value("collection").getValue(), (IExportConfiguration)itemDataContext.value("configuraton").getValue());				
			}

			@Override
			public Object getLayoutData(Layout parentLayout, Control itemControl, IDataContext itemDataContext) {
				return null;
			};
		});

		configurationPresenter.setInput(selectedExporter);
		
		// commands
		commandManager = new CommandManager();		
		
		commandManager.bind(btnExport, dataContext.value("selectedExporter").value("exportCommand"));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
