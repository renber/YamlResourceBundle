package de.renber.yamlbundleeditor.redoundo;

import java.util.ResourceBundle;

import org.eclipse.core.databinding.beans.BeanProperties;

import de.renber.yamlbundleeditor.services.ILocalizationService;

/**
 * Describes an undoable change of a ViewModel Property
 * @author renber
 *
 */
public class PropertyChangeAction implements IUndoableAction {

	/**
	 * The object the change occured in
	 */
	Object source;
	
	/**
	 * The name of the property
	 */
	String propertyName;
	
	/**
	 * The former value of the property
	 */
	Object oldValue;
	
	/**
	 * The new value of the property
	 */
	Object newValue;
	
	/**
	 * The time this action was created (used to decide if two actions should be merged)
	 */
	long time;
	
	public PropertyChangeAction(Object source, String propertyName, Object oldValue, Object newValue) {
		if (source == null)
			throw new IllegalArgumentException("Parameter source must not be null.");
		if (propertyName == null)
			throw new IllegalArgumentException("Parameter propertyName must not be null.");
		
		this.source = source;
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
		
		this.time = System.currentTimeMillis();
	}
	
	@Override
	public void undo() throws RedoUndoException {
		try {
		BeanProperties.value(propertyName).setValue(source, oldValue);
		} catch (Exception e) {
			throw new RedoUndoException("Undo failed", e);
		}
	}
	
	@Override
	public void redo() throws RedoUndoException {
		BeanProperties.value(propertyName).setValue(source, newValue);
	}

	@Override
	public boolean canMerge(IUndoableAction action) {
		if (action instanceof PropertyChangeAction) {
			PropertyChangeAction pca = (PropertyChangeAction)action;
			
			// actions can be merged, if they concern the same object and property
			// and if they are adjacent (oldValue of the new one is this action's newValue)
			// also the time gap between the two actions must not be greater than 5 seconds
			return (pca.time - time) <= 5000 && pca.source == source && pca.propertyName.equals(propertyName) && newValue.equals(pca.oldValue);
		}
		
		return false;
	}

	@Override
	public IUndoableAction merge(IUndoableAction action) {
		if (!canMerge(action))
			throw new IllegalArgumentException("Cannot merge this action with the given one.");
		
		PropertyChangeAction pca = (PropertyChangeAction)action;		
		return new PropertyChangeAction(source, propertyName, oldValue, pca.newValue);
	}

	@Override
	public String getActionDescription(ILocalizationService loc) {		
		String name = loc.getString("undoredo:properties:" + source.getClass().getSimpleName() + ":" + propertyName);		
		return loc.getString("undoredo:propertychanged", name);
	}
	
}
