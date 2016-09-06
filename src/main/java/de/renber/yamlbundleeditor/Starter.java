package de.renber.yamlbundleeditor;

import java.util.ResourceBundle;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import de.renber.databinding.commands.CommandManager;
import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.databinding.context.beans.BeansValueDataContext;
import de.renber.resourcebundles.yaml.YamlResourceBundle;
import de.renber.yamlbundleeditor.redoundo.DefaultRedoUndoService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.impl.DefaultDialogService;
import de.renber.yamlbundleeditor.services.impl.ResBundleLocalizationService;
import de.renber.yamlbundleeditor.viewmodels.MainViewModel;
import de.renber.yamlbundleeditor.views.MainView;

public class Starter {

	public static Shell mainShell;
	
	public static void main(String[] args) {
		
		final Display display = Display.getDefault();			
		
		// swt main loop, terminates when the MainWindow closes			
		Realm.runWithDefault(DisplayRealm.getRealm(display), () -> runApp(display));					
		display.dispose();		
		
		// end command manager thread
		CommandManager.end();		
	}
	
	static void runApp(Display display) {			
		// load the localization bundle
		ResourceBundle langBundle = new YamlResourceBundle(Starter.class.getResourceAsStream("/de/renber/yamlbundleeditor/localization/lang_de.yaml"));
		
		Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();	    
		
	    ILocalizationService localizationService = new ResBundleLocalizationService(langBundle);	    
		mainShell = new MainView(display, langBundle, new BeansDataContext(new MainViewModel(new DefaultDialogService(localizationService), new DefaultRedoUndoService(localizationService), localizationService)));
		
		Rectangle rect = mainShell.getBounds();	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    mainShell.layout();
	    
	    mainShell.setLocation(x, y);
	    
		mainShell.open();		
		
		while (!mainShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}				
	}
	
}
