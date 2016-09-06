package de.renber.yamlbundleeditor.utils.providers;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;

import de.renber.yamlbundleeditor.viewmodels.datatypes.ResourceKeyViewModel;

public class ResourceKeyLabelProvider extends ColumnLabelProvider {
	
	public ResourceKeyLabelProvider() {
		
	}
	
	@Override
	public String getText(Object element) {
		
		if (element instanceof ResourceKeyViewModel) {
			return ((ResourceKeyViewModel)element).getName();
		}
		
		return element.toString();
	}	
	
	@Override
	public Color getForeground(Object element) {
		if (element instanceof ResourceKeyViewModel) {
			if (((ResourceKeyViewModel)element).getHasMissingValues())
				return SWTResourceManager.getColor(255, 0, 0);
		}
		
		return null;
	}
}
