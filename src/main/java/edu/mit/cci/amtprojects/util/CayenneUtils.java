package edu.mit.cci.amtprojects.util;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.QueryLogger;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.log4j.Logger;
import org.apache.velocity.test.IntrospectorTestCase2;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 6:59 AM
 */
public class CayenneUtils {

    private static Logger log = Logger.getLogger(CayenneUtils.class);

    public static int count(DataContext context, String table, Class clz, String where) {

        final String queryString = "select count(*) 'rowCount' from "+table+(where!=null && !where.isEmpty()?" where "+where:"");
        log.info("COUNT: "+queryString);
        final SQLTemplate queryTemplate = new SQLTemplate(clz, queryString);
        queryTemplate.setFetchingDataRows(true);
        List results = context.performQuery(queryTemplate);
        int result = 0;
        if (results.size() == 1) {
            DataRow row = (DataRow) results.get(0);

            result = ((Long)row.get("rowCount")).intValue();
        }
        log.info("Found "+result+" items");
        return result;



    }

    public static String dbDateFormat(Date d) {
        return new Timestamp(d.getTime()).toString();
    }

    public static void main(String args[]) {
        System.err.println(dbDateFormat(new Date()));
    }

    public static Experiment findExperiment(DataContext context, long id) {
        Experiment e = DataObjectUtils.objectForPK(context,Experiment.class,id);
        return e;
    }

    public static Batch findBatch(DataContext context, long id) {
        return DataObjectUtils.objectForPK(context,Batch.class,id);

    }
}
