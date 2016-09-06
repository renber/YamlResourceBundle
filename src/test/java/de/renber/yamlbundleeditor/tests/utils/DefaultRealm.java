package de.renber.yamlbundleeditor.tests.utils;

import org.eclipse.core.databinding.observable.Realm;

/**
 * Simple realm implementation that will set itself as default when constructed. Invoke
 * {@link #dispose()} to remove the realm from being the default. Does not support asyncExec(...).
 * @see http://wiki.eclipse.org/JFace_Data_Binding/Realm
 */
public class DefaultRealm extends Realm {
    private Realm previousRealm;

    public DefaultRealm() {
        previousRealm = super.setDefault(this);
    }

    /**
     * @return always returns true
     */
    public boolean isCurrent() {
        return true;
    }

    protected void syncExec(Runnable runnable) {
        runnable.run();
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void asyncExec(Runnable runnable) {
        throw new UnsupportedOperationException("asyncExec is unsupported");
    }

    /**
     * Removes the realm from being the current and sets the previous realm to the default.
     */
    public void dispose() {
        if (getDefault() == this) {
            setDefault(previousRealm);
        }
    }
}