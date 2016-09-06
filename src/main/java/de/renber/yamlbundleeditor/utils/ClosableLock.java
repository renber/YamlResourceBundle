package de.renber.yamlbundleeditor.utils;

import java.util.concurrent.locks.Lock;

/**
 * A lock implementation which can be used with the try resources syntax
 */
public class ClosableLock implements AutoCloseable {

	Lock lock;
	
	/**
	 * Creates a new instance of ClosableLock which wraps the given lock.
	 * Aquires lock
	 */
	public ClosableLock(Lock lock) {
		this.lock = lock;
		lock.lock();
	}
	
	/**
	 * Unlocks the wrapped lock
	 */
	@Override
	public void close() {
		lock.unlock();		
	}

}
