package edu.mit.cci.amtprojects;

import com.amazonaws.mturk.requester.HIT;
import edu.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.HITModel;
import edu.mit.cci.amtprojects.TurkerLogModel;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.IndexedIterator;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

/**
 * User: jintrone
 * Date: 10/9/12
 * Time: 3:31 PM
 */
public class LogResultsDataProvider {

    long batchid = -1;

    public LogResultsDataProvider(Batch batch) {
      this.batchid = batch.getId();

    }

    public Iterator<? extends TurkerLog> iterator(long i, long count) {
        HitManager manager = HitManager.get(batch());
        return new IndexedIterator<TurkerLog>(manager.getFilteredLogs("RESULTS"),i,count);
    }

    public long size() {
        HitManager manager = HitManager.get(batch());
        return manager.getFilteredLogs("RESULTS").size();
    }

    public IModel<TurkerLog> model(TurkerLog log) {
        return new TurkerLogModel(log);
    }

    private Batch batch() {
        return CayenneUtils.findBatch(DbProvider.getContext(), batchid);
    }

}
