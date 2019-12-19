package de.renber.yamlbundleeditor.importers.diff;

import de.renber.yamlbundleeditor.importers.IImportConfiguration;

public class DiffImportConfiguration implements IImportConfiguration {

    public String fileOne = "";
    public String fileTwo = "";

    public boolean keepKeysOnlyInFileOne = false;

    public boolean keepKeysOnlyInFileTwo = true;

    public boolean keepKeysWithSameNameDifferentValue = true;

}
