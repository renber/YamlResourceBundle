package de.renber.yamlbundleeditor.viewmodels.datatypes;

import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.models.LocalizedValue;
import de.renber.yamlbundleeditor.redoundo.Undoable;
import de.renber.yamlbundleeditor.services.IUndoSupport;

/**
 * ViewModel for a localized version of a single value
 * @author renber
 */
public class LocalizedValueViewModel extends DataViewModelBase<LocalizedValue> {

	ResourceKeyViewModel owningKey;

	public LocalizedValueViewModel(LocalizedValue model, ResourceKeyViewModel owningKey, IUndoSupport undoSupport) {
		super(model, undoSupport);
		
		this.owningKey = owningKey;
	}
	
	/**
	 * The code for the language for which this value has been localized (e.g. en, de, en-us, ...)
	 */
	public String getLanguageCode() {
		return model.languageCode;
	}
	
	public String getLanguageDescription() {
		if (model.languageCode == null || owningKey == null || owningKey.getOwningCollection() == null)
			return getLanguageCode();
		
		BundleMetaViewModel bundle = QuIterables.query(owningKey.getOwningCollection().getBundles()).firstOrDefault(x -> model.languageCode.equals(x.getLanguageCode()));
		return bundle.getFriendlyText();
	}
	
	@Undoable
	public Object getValue() {
		return model.value;
	}
	
	public void setValue(Object newValue) {	
		changeProperty(model, "value", newValue);
		firePropertyChanged("hasValue", null, getHasValue());
	}
	
	public boolean getHasValue() {
		return model.value != null && !"".equals(model.value);
	}
}
