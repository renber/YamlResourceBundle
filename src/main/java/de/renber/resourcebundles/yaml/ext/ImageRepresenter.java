package de.renber.resourcebundles.yaml.ext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class ImageRepresenter extends Representer {

	public ImageRepresenter() {
		this.representers.put(Image.class, new ImageRepresent());
	}

	class ImageRepresent implements Represent {

		@Override
		public Node representData(Object data) {
			// node content is base64 encoded image data
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				ImageLoader imgLoader = new ImageLoader();
				imgLoader.data = new ImageData[] { ((Image) data).getImageData() };
				imgLoader.save(bos, SWT.IMAGE_PNG);

				String nodeValue = Base64.getEncoder().encodeToString(bos.toByteArray());
				// add a linebreak every 80th character
				nodeValue = nodeValue.replaceAll("(.{80})", "$1\n");
				
				return new ScalarNode(new Tag("!image"), nodeValue, null, null, Character.valueOf('|'));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
}
