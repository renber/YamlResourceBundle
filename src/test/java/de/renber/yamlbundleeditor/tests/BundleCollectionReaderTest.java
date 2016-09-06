package de.renber.yamlbundleeditor.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.serialization.BundleCollectionReader;
import de.renber.yamlbundleeditor.serialization.YamlBundleReader;

public class BundleCollectionReaderTest {

	@Test
	public void test_singleBundle() {
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
			
			BundleCollectionReader reader = new BundleCollectionReader();
			BundleCollection collection = reader.read(new YamlBundleReader(), stream);
			
			assertEquals(1, collection.getBundles().size());			
			assertEquals(2, collection.getValues().size());
			assertEquals("Hello World!", QuIterables.query(collection.getValues()).firstOrDefault(x -> "entryOne".equals(x.name)).getLocalizedValue("en"));
			
			assertEquals("But it also has a title", QuIterables.query(collection.getValues()).firstOrDefault(x -> "entryWithChild".equals(x.name)).getLocalizedValue("en"));
			assertEquals("Value of child", QuIterables.query(collection.getValues()).firstOrDefault(x -> "entryWithChild".equals(x.name)).getChildren().get(0).getLocalizedValue("en"));			
		} catch (IOException e) {
			fail("Unexpected IOException while creating ByteArrayInputStrea");
		}
	}
	
	@Test
	public void test_multiBundles() {
		String yaml_en =   "meta:\n"
                + " author: TestBot\n"
			      + " languageCode: en\n"				  
			      + " language: English\n"				      
				  + "values:\n"
				  + " entryOne: Hello World!\n"
				  + " entryWithChild:\n"
				  + "  __value: But it also has a title\n"
				  + "  childnode: Value of child";
		
		String yaml_de =   "meta:\n"
                + " author: TestBot\n"
			      + " languageCode: de\n"				  
			      + " language: German\n"				      
				  + "values:\n"
				  + " entryOne: Hallo Welt!\n"
				  + " entryWithChild:\n"
				  + "  __value: Aber er hat auch Text\n"
				  + "  childnode: Wert des Kinds";		
	
		try (ByteArrayInputStream stream_en = new ByteArrayInputStream(yaml_en.getBytes(StandardCharsets.UTF_8));
			 ByteArrayInputStream stream_de = new ByteArrayInputStream(yaml_de.getBytes(StandardCharsets.UTF_8))) {
			
			BundleCollectionReader reader = new BundleCollectionReader();
			BundleCollection collection = reader.read(new YamlBundleReader(), stream_en, stream_de);
			
			assertEquals(2, collection.getBundles().size());			
			assertEquals(2, collection.getValues().size());
			
			ResourceKey entryOne = QuIterables.query(collection.getValues()).firstOrDefault(x -> "entryOne".equals(x.name));			
			assertEquals("Hello World!", entryOne.getLocalizedValue("en"));
			assertEquals("Hallo Welt!", entryOne.getLocalizedValue("de"));
			
			ResourceKey entryWithChild = QuIterables.query(collection.getValues()).firstOrDefault(x -> "entryWithChild".equals(x.name));
			assertEquals("But it also has a title", entryWithChild.getLocalizedValue("en"));
			assertEquals("Aber er hat auch Text", entryWithChild.getLocalizedValue("de"));
			
			ResourceKey childNode = QuIterables.query(collection.getValues()).firstOrDefault(x -> "entryWithChild".equals(x.name)).getChildren().get(0);
			assertEquals("Value of child", childNode.getLocalizedValue("en"));
			assertEquals("Wert des Kinds", childNode.getLocalizedValue("de"));
		} catch (IOException e) {
			fail("Unexpected IOException while creating ByteArrayInputStrea");
		}
	}	
	
}
