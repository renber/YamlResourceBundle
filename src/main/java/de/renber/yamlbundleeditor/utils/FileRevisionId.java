package de.renber.yamlbundleeditor.utils;

/**
 * Identifies a specific revision of a file
 * @author berre
 *
 */
public interface FileRevisionId {

	public boolean hasChanged(FileRevisionId other);
	
}
