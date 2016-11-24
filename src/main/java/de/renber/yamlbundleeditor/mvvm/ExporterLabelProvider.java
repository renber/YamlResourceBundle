package de.renber.yamlbundleeditor.mvvm;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

public class ExporterLabelProvider extends OwnerDrawLabelProvider {

	IValueProperty imageProperty;
	IValueProperty textProperty;	
	
	public ExporterLabelProvider(IValueProperty imageProperty, IValueProperty textProperty) {
		this.imageProperty = imageProperty;
		this.textProperty = textProperty;		
	}
	
	@Override
	protected void measure(Event e, Object item) {
		Image img = (Image)imageProperty.getValue(item);
		
		int height = 20;
		if (img != null)
			height += img.getBounds().height;
		
		e.setBounds(new Rectangle(e.x, e.y, e.width, height));
	}

	@Override
	protected void paint(Event e, Object item) {		
		
		Rectangle cellBounds = ((TableItem) e.item).getBounds(e.index);
		
		int topOffset = cellBounds.y + 2;
		Image img = (Image)imageProperty.getValue(item);
		if (img != null) {
			e.gc.drawImage(img, cellBounds.x + (cellBounds.width - img.getBounds().width) / 2, topOffset);
			topOffset += img.getBounds().height + 2;
		}				
		
		String text = (String)textProperty.getValue(item);
		int textWidth = e.gc.stringExtent(text).x; 
		if (text != null) {
			e.gc.drawText(text, cellBounds.x + (cellBounds.width - textWidth) / 2, topOffset, true);			
		}		
	}

}
