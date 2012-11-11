package edu.mit.cci.amtprojects.solver;

import jsc.datastructures.MatchedData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.JUnit4;

/**
 * User: jintrone
 * Date: 11/10/12
 * Time: 9:15 PM
 */
public class FriedmanTest {

    @Test
    public void testKendallsW() {
        double[][] d = new double[][] {
                {.33,.66,1},
                {1,.66,.33}
        };
        float w = (float) new jsc.relatedsamples.FriedmanTest(new MatchedData(d)).getW();
        System.err.println(w);
        Assert.assertTrue(w<=1.0 && w>= 0.0);
    }
}
