package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Hits;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.IndexedIterator;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 5:07 AM
 */
public class HitDataProvider implements IDataProvider<Hits> {


    long batchid = -1;

    public HitDataProvider(Batch b) {
        batchid = b.getId();

    }

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Iterator<? extends Hits> iterator(long i, long count) {
        List<edu.mit.cci.amtprojects.kickball.cayenne.Hits> hits = batch().getHits();
        return new IndexedIterator<Hits>(hits,i,count);
    }

    public long size() {
        return batch().getHits().size();
    }

    public IModel<edu.mit.cci.amtprojects.kickball.cayenne.Hits> model(edu.mit.cci.amtprojects.kickball.cayenne.Hits hit) {
        return new HITModel(hit);
    }

    private Batch batch() {
        return CayenneUtils.findBatch(DbProvider.getContext(),batchid);
    }




}
