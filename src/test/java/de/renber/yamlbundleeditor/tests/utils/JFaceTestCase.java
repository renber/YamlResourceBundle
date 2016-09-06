package de.renber.yamlbundleeditor.tests.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import junit.framework.TestCase;

/**
 * Base class for test cases which test JFace databinding and therefore need a default Realm
 * @author renber
 *
 */
public class JFaceTestCase {

	private DefaultRealm realm;    
	
    /**
     * Creates a new default realm for every test.
     */
	@Before
    public void setUp() throws Exception {        
        realm = new DefaultRealm();
    }
    
    /**
     * Removes the default realm.
     */
	@After
	public void tearDown() throws Exception {        
        realm.dispose();
    }
	
}
