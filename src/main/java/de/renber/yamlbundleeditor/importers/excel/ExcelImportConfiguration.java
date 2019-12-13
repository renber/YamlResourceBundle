package de.renber.yamlbundleeditor.importers.excel;

import de.renber.yamlbundleeditor.importers.IImportConfiguration;

class ExcelImportConfiguration implements IImportConfiguration {
	public String separator = ":";
	
	/**
	 * Display a warning to the user when the import contains keys which do not exist
	 * in the target collection
	 */
	public boolean warnForNonExistingKeys = false;
}