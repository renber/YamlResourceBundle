package de.renber.yamlbundleeditor.viewmodels;

import org.eclipse.swt.widgets.Shell;

import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.yamlbundleeditor.services.IUndoSupport;

/**
 * Base class for ViewModels which are bound to Windows
 * @author renber
 *
 */
public class WindowedViewModelBase extends UndoableViewModelBase {

	/**
	 * Instantiates a WindowedViewModelBase without UndoSupport
	 */
	public WindowedViewModelBase() {
		this(null);
	}
	
	public WindowedViewModelBase(IUndoSupport undoSupport) {
		super(undoSupport); 
	}

	protected ViewCallback viewCallback;
	
	public void setViewCallback(ViewCallback viewCallback) {
		this.viewCallback = viewCallback;
	}
	
	public void requestViewClose() {
		if (viewCallback != null)		
			viewCallback.requestClose();
	}
	
}
