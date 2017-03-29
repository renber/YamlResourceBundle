package de.renber.yamlbundleeditor.exporters.excel;

import java.util.ArrayList;
import java.util.List;

import de.renber.yamlbundleeditor.exporters.IExportConfiguration;

public class ExcelExportConfiguration implements IExportConfiguration {
		
	/**
	 * The separator to use (parent[separator:child]) when creating key paths 
	 */
	public String levelSeparator = ":";

	/**
	 * Should cells with missing values be highlighted
	 */
	public boolean highlightMissingValues = true;
	
	private List<String> languagesToExport = new ArrayList<String>();
	
	/**
	 * List of language codes which shall be included in the export
	 */
	public List<String> getLanguagesToExport() {
		return languagesToExport;
	}

}
