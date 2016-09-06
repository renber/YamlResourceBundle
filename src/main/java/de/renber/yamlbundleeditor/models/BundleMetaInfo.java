package de.renber.yamlbundleeditor.models;

import org.eclipse.swt.graphics.Image;

/**
 * Meta-information about a ResourceBundle
 * @author renber
 *
 */
public class BundleMetaInfo {
	
	/**
	 * If true, this bundle does not declare resource values itself but comments for other bundles
	 */
	public boolean isCommentBundle;	
	/**
	 * ISO code of the language (e.g. en for english, de for german)
	 */
	public String languageCode;		
	/**
	 * Variant of the language (e.g. us for USA, gb for great britain)
	 */
	public String variantName;	
	/**
	 * The English name for the language (e.g. Espanol for Spanish)
	 */
	public String name;
	/**
	 * The name of the language in the language itself (e.g. Deutsch for German)
	 */
	public String localizedName;
	/**
	 * Descriptor of the language author(s)
	 */
	public String author;	
	/**
	 * The country's flag this localization file represents 
	 */
	public Image flagImage;		
		
}
