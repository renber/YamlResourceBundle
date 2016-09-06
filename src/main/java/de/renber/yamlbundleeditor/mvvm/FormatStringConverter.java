package de.renber.yamlbundleeditor.mvvm;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Converter for String properties which applies a string format
 * @author renber
 *
 */
public class FormatStringConverter implements IConverter {

	String formatString;
	
	/**
	 * 
	 * @param formatString The format string to apply to the binding value, the binding value will be passed
	 * to String.format(formatString, [value])
	 */
	public FormatStringConverter(String formatString) {
		this.formatString = formatString;
	}
	
	public Object convert(Object value) {
		return String.format(formatString, value);
	}

	public Object getFromType() {
		return Object.class;
	}

	public Object getToType() {
		return String.class;
	}

}
