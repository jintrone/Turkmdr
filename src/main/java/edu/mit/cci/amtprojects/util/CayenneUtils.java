package edu.mit.cci.amtprojects.util;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;
import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import edu.mit.cci.amtprojects.kickball.cayenne.User;
import edu.mit.cci.amtprojects.kickball.cayenne.Users;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.QueryLogger;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.velocity.test.IntrospectorTestCase2;
import org.apache.wicket.ajax.json.JSONObject;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public static List<TurkerLog> getTurkerLogForAssignment(DataContext context, String assignmentId, String type) {

        final String queryString = "select * from TurkerLog  where assignmentId like '"+assignmentId+"' and type like '"+type+"'";
        final SQLTemplate queryTemplate = new SQLTemplate(TurkerLog.class, queryString);
        return (List<TurkerLog>)context.performQuery(queryTemplate);

    }

    public static Users findUser(DataContext context, String username, String password) {
        SelectQuery q = new SelectQuery(Users.class,Expression.fromString("username = '"+username+"' and password = '"+password+"'"));
        List<Users> result = (List<Users>)context.performQuery(q);
        if (result.isEmpty()) return null;
        else return result.get(0);
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

    public static Long extractObjectId(DataObject obj) {
        return (Long)obj.getObjectId().getIdSnapshot().get("id");
    }


    public static void logEvent(DataContext context, Batch b, String type, String workerid, String hitid, String assignmentid,
                                String queryparams, Map<String,Object> data) {
        TurkerLog log = context.newObject(TurkerLog.class);
        log.setToBatch(b);
        log.setType(type);
        log.setWorkerId(workerid);
        log.setHit(hitid);
        log.setAssignmentId(assignmentid);
        log.setQueryParams(queryparams);
        if (data != null && !data.isEmpty()) log.setData(new JSONObject(data).toString());
        log.setDate(new Date());
        context.commitChanges();

    }

    public static TurkerLog findWorkerDemographics(DataContext context, String workerId) {
       final String queryString = "select * from TurkerLog  where workerId like '"+workerId+"' and type like 'DEMOGRAPHICS'";
        final SQLTemplate queryTemplate = new SQLTemplate(TurkerLog.class, queryString);
        List<TurkerLog> logs = (List<TurkerLog>)context.performQuery(queryTemplate);
        return logs.isEmpty()?null:logs.get(0);
    }

    public static Set<Batch> findWorkerBatches(String workerId, Experiment e) {
        SelectQuery query = new SelectQuery(TurkerLog.class);
        query.andQualifier(Expression.fromString("workerId='"+workerId+"'"));
        Expression t = Expression.fromString("toBatch.toExperiment = $exp");

        query.andQualifier(t.expWithParameters(Collections.singletonMap("exp",e)));
        query.andQualifier(Expression.fromString("type="+"'RESULTS'"));
        List<TurkerLog> logs = (List<TurkerLog>)DbProvider.getContext().performQuery(query);
        Set<Batch> batches = new HashSet<Batch>();
        for (TurkerLog l:logs) {
            batches.add(l.getToBatch());
        }
        return batches;
    }


}
