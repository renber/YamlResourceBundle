package de.renber.yamlbundleeditor.controls;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class WatermarkText extends Text implements PaintListener {

	Color watermarkForeground;
	String watermarkText;
	
	public WatermarkText(Composite parent, int style, String watermarkText) {
		super(parent, style);
		
		watermarkForeground = SWTResourceManager.getColor(75, 75, 75);
		this.watermarkText = watermarkText;
		
		this.addPaintListener(this);
	}

	@Override
	public void paintControl(PaintEvent event) {		
		// if no text has been entered show a watermark text, but only if we do not have focus
		if (getDisplay().getFocusControl() != this && getText() == null || getText().isEmpty()) {
			GC g = event.gc;
			g.setForeground(watermarkForeground);
			Rectangle rect = this.getClientArea();			
			Point strSize = g.textExtent(watermarkText);
			
			g.drawString(watermarkText, rect.x + 5, rect.y + (rect.height - strSize.y) / 2);	
		}		
	}
	
	public String getWatermarkText() {
		return watermarkText;
	}
	
	public void setWatermarkText(String newValue) {
		watermarkText = newValue;
		this.redraw();
	}
	
	public Color getWatermarkForeground() {
		return watermarkForeground;
	}
	
	public void setWatermarkForeground(Color newValue) {
		if (watermarkForeground != newValue)
			watermarkForeground.dispose();
		
		watermarkForeground = new Color(getDisplay(), newValue.getRGB());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
