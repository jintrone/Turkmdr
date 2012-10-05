package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import org.apache.cayenne.DataObjectUtils;
import org.apache.wicket.model.IModel;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 11:09 PM
 */
public class BatchModel implements IModel<Batch> {

    long batchid = -1;

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public BatchModel(Batch b) {
        this.batchid = b.getId();
    }

    public Batch getObject() {
        return DataObjectUtils.objectForPK(DbProvider.getContext(),Batch.class,batchid);
    }

    public void setObject(Batch batch) {
       this.batchid = batch.getId();
    }
}
