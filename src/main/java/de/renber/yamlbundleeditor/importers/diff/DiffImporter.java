package de.renber.yamlbundleeditor.importers.diff;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.databinding.templating.ITemplatingControlFactory;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.importers.IImportConfiguration;
import de.renber.yamlbundleeditor.importers.IImporter;
import de.renber.yamlbundleeditor.importers.ImportException;
import de.renber.yamlbundleeditor.importers.excel.ExcelImportConfigurationComposite;
import de.renber.yamlbundleeditor.importers.excel.ExcelImportConfigurationViewModel;
import de.renber.yamlbundleeditor.models.Bundle;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.serialization.BundleCollectionReader;
import de.renber.yamlbundleeditor.serialization.YamlBundleReader;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IconProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiffImporter implements IImporter {

    @Override
    public String getName() {
        return "Difference between to yaml files";
    }

    @Override
    public Image getImage() {
        return IconProvider.getImage("import/diff");
    }

    ILocalizationService loc;
    IDialogService dialogService;
    ICommand okCommand;
    ICommand cancelCommand;

    public DiffImporter(ILocalizationService localizationService, IDialogService dialogService) {
        this.loc = localizationService;
        this.dialogService = dialogService;
    }

    @Override
    public IImportConfiguration doImport(BundleCollection collection, IImportConfiguration configuration) throws ImportException {
        if (collection == null)
            throw new IllegalArgumentException("The parameter collection must not be null.");

        if (configuration == null || !(configuration instanceof DiffImportConfiguration))
            throw new IllegalArgumentException("The parameter configuration must not be null and of type DiffImportConfiguration.");

        DiffImportConfiguration config = (DiffImportConfiguration)configuration;

        final DiffImportConfigurationViewModel vm = new DiffImportConfigurationViewModel(config, dialogService);
        dialogService.showDialogFor("Diff import settings", new BeansDataContext(vm), new ITemplatingControlFactory() {
            @Override
            public Control create(Composite parent, IDataContext itemDataContext) {
                return new DiffImportConfigurationComposite(parent, SWT.None, itemDataContext);
            }

            @Override
            public Object getLayoutData(Layout parentLayout, Control itemControl, IDataContext itemDataContext) {
                return null;
            }
        });

        if (!vm.importConfirmed) {
            // cancelled
            return null;
        }

        Bundle bundleOne;
        Bundle bundleTwo;
        YamlBundleReader reader = new YamlBundleReader();

        try (FileInputStream streamOne = new FileInputStream(config.fileOne)) {
            bundleOne = reader.read(streamOne);
        } catch (IOException e) {
            dialogService.showMessageDialog("Error", "The specified file " + config.fileOne + " could not be opnened");
            return null;
        }

        try (FileInputStream streamTwo = new FileInputStream(config.fileTwo)) {
            bundleTwo = reader.read(streamTwo);
        } catch (IOException e) {
            dialogService.showMessageDialog("Error", "The specified file " + config.fileTwo + " could not be opnened");
            return null;
        }

        HashMap<String, String> resultValuesOld = new HashMap<>();
        HashMap<String, String> resultValuesNew = new HashMap<>();

        if (config.keepKeysOnlyInFileOne) {
            List<Map.Entry<String, String>> vals = QuIterables.query(bundleOne.getValues().entrySet()).where(x -> !bundleTwo.getValues().containsKey(x.getKey())).toList();
            for(Map.Entry<String, String> v: vals) {
                resultValuesOld.put(v.getKey(), v.getValue());
            }
        }

        if (config.keepKeysOnlyInFileTwo) {
            List<Map.Entry<String, String>> vals = QuIterables.query(bundleTwo.getValues().entrySet()).where(x -> !bundleOne.getValues().containsKey(x.getKey())).toList();
            for(Map.Entry<String, String> v: vals) {
                resultValuesNew.put(v.getKey(), v.getValue());
            }
        }

        if (config.keepKeysWithSameNameDifferentValue) {
            List<Map.Entry<String, String>> vals = QuIterables.query(bundleOne.getValues().entrySet()).where(x ->
                    bundleTwo.getValues().containsKey(x.getKey()) && !valuesMatch(x.getValue(), bundleTwo.getValues().get(x.getKey()))).toList();
            for(Map.Entry<String, String> v: vals) {
                resultValuesOld.put(v.getKey(), v.getValue());
                resultValuesNew.put(v.getKey(), bundleTwo.getValues().get(v.getKey()));
            }
        }

        BundleMetaInfo metaOld = bundleOne.getMeta();
        metaOld.languageCode = bundleOne.getMeta().languageCode + "_old";
        BundleMetaInfo metaNew = bundleTwo.getMeta();

        Bundle bundleOld = null;
        if (resultValuesOld.size() > 0) {
            bundleOld = new Bundle(metaOld, resultValuesOld);
        }
        Bundle bundleNew = new Bundle(metaNew, resultValuesNew);

        BundleCollectionReader creader = new BundleCollectionReader();
        BundleCollection combinedCollection;
        if (bundleOld != null) {
         combinedCollection = creader.read(bundleNew, bundleOld);
        } else {
            combinedCollection = creader.read(bundleNew);
        }

        // transfer to existing collection
        collection.getValues().addAll(combinedCollection.getValues());
        collection.getBundles().addAll(combinedCollection.getBundles());

        return config;
    }

    private boolean valuesMatch(String valueOne, String valueTwo) {
        if (valueOne == null) valueOne = "";
        if (valueTwo == null) valueTwo = "";

        if (valueOne.contains("Alle Zylindereigenschaften")) {
            valueOne = valueOne + "";
        }

        valueOne = valueOne.replace("\r", "");
        valueTwo = valueTwo.replace("\r", "");

        return valueOne.equals(valueTwo);
    }

    private String combineStrings(String strOne, String strTwo) {
        if (strOne == null) strOne = "";
        if (strTwo == null) strTwo = "";

        if (strOne.equals(strTwo)) {
            return strOne;
        } else {
            return strOne + " / " + strTwo;
        }
    }

    @Override
    public BundleCollection doImport(IImportConfiguration configuration) throws ImportException {
        BundleCollection bundle = new BundleCollection();
        IImportConfiguration config = doImport(bundle, configuration);
        return config == null ? null : bundle;
    }

    @Override
    public IImportConfiguration getDefaultConfiguration() {
        return new DiffImportConfiguration();
    }

    @Override
    public String serializeConfiguration(IImportConfiguration configuration) {
        return "";
    }

    @Override
    public IImportConfiguration deserializeConfiguration(String serializedString) {
        return new DiffImportConfiguration();
    }

}
