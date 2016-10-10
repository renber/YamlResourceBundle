package de.renber.yamlbundleeditor.redoundo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a property of a ViewModel for
 * automatic undo/redo support
 * Has to be attached to the property getter
 * @author renber
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoUndoable {
	
}
