package de.renber.yamlbundleeditor.controls;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * Paints a border aroudn a widget
 * @author renber
 *
 */
public class BorderPainter implements PaintListener {

	Control control;
	Color borderColor;
	
	public BorderPainter(Control targetControl) {
		control = targetControl;
		borderColor = SWTResourceManager.getColor(0, 0, 0);
		
		control.addPaintListener(this);
	}
	
	@Override
	public void paintControl(PaintEvent e) {		
		e.gc.setForeground(borderColor);
		e.gc.drawRectangle(0, 0, control.getSize().x - 1, control.getSize().y - 1);		
	}

}
