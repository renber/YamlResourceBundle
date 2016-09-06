package de.renber.resourcebundles.yaml.ext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Reads the !image tag from a yaml file
 * 
 * @author renber
 */
public class ImageConstructor extends Constructor {

	public ImageConstructor() {
		this.yamlConstructors.put(new Tag("!image"), new ConstructImage());		
	}

	private class ConstructImage extends AbstractConstruct {
		public Object construct(Node node) {						
			String val = (String) constructScalar((ScalarNode) node);
			// remove line breaks (if any)
			val = val.replace("\n", "");
									
			// node content is base64 encoded image data
			try (ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(val.getBytes(Charset.forName("UTF-8"))))) {				
				ImageLoader imgLoader = new ImageLoader();
				ImageData data = imgLoader.load(bis)[0];							
				return new Image(Display.getDefault(), data);				
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

}
