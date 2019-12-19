package de.renber.yamlbundleeditor.importers.diff;

import de.renber.databinding.ComplexBind;
import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.commands.ICommand;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.providers.ControlCellProvider;
import de.renber.databinding.providers.PropertyColumnLabelProvider;
import de.renber.databinding.templating.ITemplatingControlFactory;
import javafx.scene.layout.Pane;
import net.miginfocom.swt.MigLayout;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.awt.*;
import java.beans.Beans;

public class DiffImportConfigurationComposite extends Composite {

    DataBindingContext bindingContext;
    CommandManager commandManager;
    IDataContext dataContext;

    private Text txtFileOne;
    private Button btnBrowsForFileOne;

    private Text txtFileTwo;
    private Button btnBrowsForFileTwo;

    private Button cbKeepFileOne;
    private Button cbKeepFileTwo;
    private Button cbKeepSameName;


    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public DiffImportConfigurationComposite(Composite parent, int style, IDataContext dataContext) {
        super(parent, style);

        this.dataContext = dataContext;

        createContents();

        if (!Beans.isDesignTime()) {
            setupBindings();
        }
    }

    private void createContents() {
        setLayout(new MigLayout("", "[][fill][]"));

        Label lblFileOne = new Label(this, SWT.NONE);
        lblFileOne.setText("First file:");

        txtFileOne = new Text(this, SWT.BORDER);
        txtFileOne.setLayoutData("growx");

        btnBrowsForFileOne = new Button(this, SWT.NONE);
        btnBrowsForFileOne.setText("...");
        btnBrowsForFileOne.setLayoutData("wrap");

        Label lblFileTwo = new Label(this, SWT.NONE);
        lblFileTwo.setText("Second file:");

        txtFileTwo = new Text(this, SWT.BORDER);
        txtFileTwo.setLayoutData("growx");

        btnBrowsForFileTwo = new Button(this, SWT.NONE);
        btnBrowsForFileTwo.setText("...");
        btnBrowsForFileTwo.setLayoutData("wrap");

        cbKeepFileOne = new Button(this, SWT.CHECK);
        cbKeepFileOne.setText("Keep keys which are only present in file one");
        cbKeepFileOne.setLayoutData("spanx 2, wrap");

        cbKeepFileTwo = new Button(this, SWT.CHECK);
        cbKeepFileTwo.setText("Keep keys which are only present in file two");
        cbKeepFileTwo.setLayoutData("spanx 2, wrap");

        cbKeepSameName = new Button(this, SWT.CHECK);
        cbKeepSameName.setText("Keep keys with the same name but different values");
        cbKeepSameName.setLayoutData("spanx 2, wrap");
    }

    private void setupBindings() {
        bindingContext = new DataBindingContext();
        commandManager = new CommandManager();

        bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(txtFileOne), dataContext.value("fileOne").observe());
        bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(txtFileTwo), dataContext.value("fileTwo").observe());

        bindingContext.bindValue(WidgetProperties.selection().observe(cbKeepFileOne), dataContext.value("keepKeysOnlyInFileOne").observe());
        bindingContext.bindValue(WidgetProperties.selection().observe(cbKeepFileTwo), dataContext.value("keepKeysOnlyInFileTwo").observe());
        bindingContext.bindValue(WidgetProperties.selection().observe(cbKeepSameName), dataContext.value("keepKeysWithSameNameDifferentValue").observe());

        commandManager.bind(btnBrowsForFileOne, dataContext.value("browseForFileOneCommand"));
        commandManager.bind(btnBrowsForFileTwo, dataContext.value("browseForFileTwoCommand"));
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
