package de.renber.yamlbundleeditor.viewmodels;

import org.eclipse.swt.widgets.Shell;

public interface ViewCallback {
	
	/**
	 * Requests that the view which is displaying the ViewModel (if any) be closed
	 */
	public void requestClose();
	
}
