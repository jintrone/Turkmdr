package edu.mit.cci.amtprojects;

import com.amazonaws.mturk.requester.HIT;
import edu.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.IndexedIterator;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 5:07 AM
 */
public class HitDataProvider implements IDataProvider<HIT> {


    long batchid = -1;

    public HitDataProvider(Batch b) {
        batchid = b.getId();

    }

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Iterator<? extends HIT> iterator(long i, long count) {
        HitManager manager = HitManager.get(batch());
        return new IndexedIterator<HIT>(manager.getAllHits(),i,count);
    }

    public long size() {
        HitManager manager = HitManager.get(batch());
        return manager.getAllHits().size();
    }

    public IModel<HIT> model(HIT hit) {
        return new HITModel(hit);
    }

    private Batch batch() {
        return CayenneUtils.findBatch(DbProvider.getContext(),batchid);
    }




}
