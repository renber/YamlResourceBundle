package de.renber.yamlbundleeditor.viewmodels;

import java.lang.reflect.Method;

import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.yamlbundleeditor.redoundo.PropertyChangeAction;
import de.renber.yamlbundleeditor.redoundo.Undoable;
import de.renber.yamlbundleeditor.services.IUndoSupport;

public class UndoableViewModelBase extends ViewModelBase {

private IUndoSupport undoSupport;
    
    /**
     * Creates an instance of ViewModelBase without undo support
     */
    public UndoableViewModelBase() {
    	this(null);
    }
    
    /**
     * @param undoSupport Instance of an IUndoSupport class which should be used to record changes (if any) 
     */
    public UndoableViewModelBase(IUndoSupport undoSupport) {
    	this.undoSupport = undoSupport;
    	
    	if (undoSupport != null) {
    		addPropertyChangeListener((evt) -> {
    			// record the property change    			
    			String getterName = "get" + String.valueOf(evt.getPropertyName().charAt(0)).toUpperCase() + evt.getPropertyName().substring(1);    			    		
    			
    			try {   
    				Method getter = evt.getSource().getClass().getMethod(getterName);
    				// check if the getter has bee marked for undo support
    				
    				if (getter.isAnnotationPresent(Undoable.class)) {    				
						undoSupport.record(new PropertyChangeAction(this, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
					}				
				} catch (NoSuchMethodException e) {
					// --
				} catch (SecurityException e) {
					// --
				}
    		});
    	}
    }
    
    public IUndoSupport getUndoSupport() {
    	return undoSupport;
    }
	
}
