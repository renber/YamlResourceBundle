package de.renber.yamlbundleeditor.tests;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import de.renber.yamlbundleeditor.models.Bundle;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.serialization.YamlBundleReader;

public class YamlBundleReaderTest {

	@Test
	public void test_readMeta() {
		String yaml =   "meta:\n"
	                  + " author: TestBot\n"
					  + " languageCode: de\n"				  
					  + " language: German\n"
					  + " localizedLanguage: Deutsch\n";					  
					  		
		try (ByteArrayInputStream stream = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8))) {
			YamlBundleReader reader = new YamlBundleReader();
			BundleMetaInfo meta = reader.readMeta(stream);		
			
			assertNotNull(meta);
			assertFalse(meta.isCommentBundle);
			assertEquals("TestBot", meta.author);
			assertEquals("de", meta.languageCode);
			assertEquals("", meta.variantName);
			assertEquals("German", meta.name);
			assertEquals("Deutsch", meta.localizedName);
			assertNull(meta.flagImage); // no image declared
			
		}catch (IOException e) {
			fail("Unexpected IOException while creating ByteArrayInputStream");
		}				
	}
	
	@Test
	public void test_readMeta_CommentBundle() {
		String yaml =   "meta:\n"
					  + " type: comments\n"
	                  + " author: TestBot\n"
					  + " languageCode: de\n"				  
					  + " language: German\n"
					  + " localizedLanguage: Deutsch\n";					  
					  		
		try (ByteArrayInputStream stream = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8))) {
			YamlBundleReader reader = new YamlBundleReader();
			BundleMetaInfo meta = reader.readMeta(stream);		
			
			assertNotNull(meta);
			assertTrue(meta.isCommentBundle);
			assertEquals("TestBot", meta.author);
			assertEquals("de", meta.languageCode);
			assertEquals("", meta.variantName);
			assertEquals("German", meta.name);
			assertEquals("Deutsch", meta.localizedName);
			assertNull(meta.flagImage); // no image declared
			
		}catch (IOException e) {
			fail("Unexpected IOException while creating ByteArrayInputStream");
		}				
	}
	
	@Test
	public void test_readValues() {
		String yaml =   "meta:\n"
                      + " author: TestBot\n"
				      + " languageCode: en\n"				  
				      + " language: English\n"				      
					  + "values:\n"
					  + " entryOne: Hello World!\n"
					  + " entryWithChild:\n"
					  + "  __value: But it also has a title\n"
					  + "  childnode: Value of child";
		
		try (ByteArrayInputStream stream = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8))) {
			YamlBundleReader reader = new YamlBundleReader();
			Bundle bundle = reader.read(stream);		
			
			assertEquals("Hello World!", bundle.getValues().get("entryOne"));
			assertEquals("But it also has a title", bundle.getValues().get("entryWithChild\t__value"));
			assertEquals("Value of child", bundle.getValues().get("entryWithChild\tchildnode"));
			
			
		}catch (IOException e) {
			fail("Unexpected IOException while creating ByteArrayInputStream");
		}			
	}

}
