package de.renber.yamlbundleeditor.importers.diff;

import de.renber.databinding.commands.ICommand;
import de.renber.databinding.commands.RelayCommand;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;
import de.renber.yamlbundleeditor.viewmodels.WindowedViewModelBase;

import java.io.File;

public class DiffImportConfigurationViewModel extends WindowedViewModelBase {

    private IDialogService dialogService;

    DiffImportConfiguration configuration;

    boolean importConfirmed = false;

    private ICommand browseForFileOneCommand;
    private ICommand browseForFileTwoCommand;

    ICommand okCommand;
    ICommand cancelCommand;

    public String getFileOne() {
        return configuration.fileOne;
    }

    public void setFileOne(String newValue) {
        changeProperty(configuration,"fileOne", newValue);
    }

    public String getFileTwo() {
        return configuration.fileTwo;
    }

    public void setFileTwo(String newValue) {
        changeProperty(configuration,"fileTwo", newValue);
    }

    public boolean getKeepKeysOnlyInFileOne() { return configuration.keepKeysOnlyInFileOne; }

    public void setKeepKeysOnlyInFileOne(boolean newValue) {
        changeProperty(configuration,"keepKeysOnlyInFileOne", newValue);
    }

    public boolean getKeepKeysOnlyInFileTwo() { return configuration.keepKeysOnlyInFileTwo; }

    public void setKeepKeysOnlyInFileTwo(boolean newValue) {
        changeProperty(configuration,"keepKeysOnlyInFileTwo", newValue);
    }

    public boolean getKeepKeysWithSameNameDifferentValue() { return configuration.keepKeysWithSameNameDifferentValue; }

    public void setKeepKeysWithSameNameDifferentValue(boolean newValue) {
        changeProperty(configuration,"keepKeysWithSameNameDifferentValue", newValue);
    }

    /**
     * The user confirmed that the import should be carried out
     */
    public boolean isConfirmed() {
        return importConfirmed;
    }

    public DiffImportConfigurationViewModel(DiffImportConfiguration config, IDialogService dialogService) {
        if (config == null) throw new IllegalArgumentException("Parameter config must not be null");
        if (dialogService == null) throw new IllegalArgumentException("Parameter dialogService must not be null");

        this.configuration = config;
        this.dialogService = dialogService;

        initCommands();
    }

    private void initCommands() {
        browseForFileOneCommand = new RelayCommand(() -> {
            File f = dialogService.showOpenFileDialog("Select the first file", new FileExtFilter("Yaml language bundle", "*.yaml"));
            if (f != null)
                setFileOne(f.getAbsolutePath());
        });

        browseForFileTwoCommand = new RelayCommand(() -> {
            File f = dialogService.showOpenFileDialog("Select the second file", new FileExtFilter("Yaml language bundle", "*.yaml"));
            if (f != null)
                setFileTwo(f.getAbsolutePath());
        });

        okCommand = new RelayCommand( () -> {
            importConfirmed = true;
            requestViewClose();
        }, () -> !getFileOne().isEmpty() && !getFileTwo().isEmpty() );

        cancelCommand = new RelayCommand( () -> {
            importConfirmed = false;
            requestViewClose();
        });
    }

    public ICommand getBrowseForFileOneCommand() { return browseForFileOneCommand; }

    public ICommand getBrowseForFileTwoCommand() { return browseForFileTwoCommand; }

    public ICommand getOkCommand() {
        return okCommand;
    }

    public ICommand getCancelCommand() {
        return cancelCommand;
    }

}
