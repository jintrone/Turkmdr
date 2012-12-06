package edu.mit.cci.amtprojects;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * GenericPropertiesModel Tester.
 *
 * @author <Authors name>
 * @since <pre>12/04/2012</pre>
 * @version 1.0
 */
public class GenericPropertiesModelTest extends TestCase {
    public GenericPropertiesModelTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testProps() throws Exception {
        GenericPropertiesModel model = new GenericPropertiesModel();
        model.setProperty("A",1);
        model.setProperty("B","test");
        model.setProperty("C",3.4f);
        model.setProperty("D", Arrays.asList("a","b","c"));

        GenericPropertiesModel model2 = new GenericPropertiesModel(model.toJSONString());
        assertEquals(model.getList("D",String.class),model2.getList("D",String.class));


    }


    public static Test suite() {
        return new TestSuite(GenericPropertiesModelTest.class);
    }
}
