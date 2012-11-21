package edu.mit.cci.amtprojects.kickball.cayenne;


import edu.mit.cci.amtprojects.DbProvider;
import junit.framework.TestSuite;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * BatchStatus Tester.
 *
 * @author <Authors name>
 * @since <pre>11/20/2012</pre>
 * @version 1.0
 */
public class BatchStatusTest {






    public void testBatchStatus() {
        List<Batch> batches = DbProvider.getContext().performQuery(new SelectQuery(Batch.class));
        Batch b = batches.get(0);
        BatchStatus bs = DbProvider.getContext().newObject(BatchStatus.class);
        bs.setToBatch(b);
        bs.setJsonStatus("foo");
        bs.setCreation(new Date());
        DbProvider.getContext().commitChanges();


    }
}
