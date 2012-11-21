package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;

/**
 * User: jintrone
 * Date: 11/11/12
 * Time: 10:52 PM
 */
public interface BatchManager {

    public void restartBatchProcessor(Batch b, UrlCreator creator);
    public void haltBatchProcessor(Batch b);

    public void extendBatch(Batch b);
    public void expireBatch(Batch b);

    public void restartActiveHits(Batch b);

    public Status getStatus(Batch batch);

    enum Status {
        RUNNING, HALTED, COMPLETE
    }
}
