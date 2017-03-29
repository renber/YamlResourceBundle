package de.renber.yamlbundleeditor.importers.excel;

/**
 * Class which holds information about a bundle to be imported from an excel file
 * @author renber
 *
 */
public class ImportBundleInfo {
	String languageCode;
	String name;
	int excelColumn;
	boolean includeInImport = false;
}
