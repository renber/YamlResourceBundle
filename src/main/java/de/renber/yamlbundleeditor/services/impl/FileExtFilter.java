package de.renber.yamlbundleeditor.services.impl;

/**
 * Describes a file extension filter for file dialogs
 * @author renber
 *
 */
public class FileExtFilter {

	String name;
	String extensions;
	
	public FileExtFilter(String name, String...extensions) {
		this.name = name;
		this.extensions = String.join(";", extensions);
	}
	
	public String getName() {
		return name;
	}
	
	public String getExtensions() {
		return extensions;
	}
	
}
