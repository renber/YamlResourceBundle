package de.renber.yamlbundleeditor.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Listener for ToolItems which opens a drop down menu when clicked
 * 
 * @author renber
 */
public class DropDownSelectionListener extends SelectionAdapter {
	private ToolItem dropdown;

	private Menu menu;
	
	int eventDetail;

	public DropDownSelectionListener(int eventMask, ToolItem dropdown) {
		this.eventDetail = eventMask;
		this.dropdown = dropdown;
		menu = new Menu(dropdown.getParent().getShell());
	}

	public MenuItem addMenuItem(String item, int style) {
		MenuItem menuItem = new MenuItem(menu, style);
		menuItem.setText(item);		
		return menuItem;
	}

	public void widgetSelected(SelectionEvent event) {		
		if ((event.detail & eventDetail) == eventDetail) {
			ToolItem item = (ToolItem) event.widget;
			Rectangle rect = item.getBounds();
			Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
			menu.setLocation(pt.x, pt.y + rect.height);
			menu.setVisible(true);
		}
	}
	
	/**
	 * Returns the Menu instance used as the dropdown menu
	 */
	public Menu getMenu() {
		return menu;
	}
}
