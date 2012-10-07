package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.IndexedIterator;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/2/12
 * Time: 11:04 PM
 */
public class BatchDataProvider implements IDataProvider<Batch> {

    long experimentId = -1;

    public BatchDataProvider(Experiment e) {
      experimentId = e.getExperimentId();

    }

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Iterator<? extends Batch> iterator(long i, long i1) {
        Experiment e = CayenneUtils.findExperiment(DbProvider.getContext(),experimentId);
        List<Batch> result = e.getToBatch();
        return new IndexedIterator<Batch>(result,i,i1);
    }

    public long size() {
        return CayenneUtils.count(DbProvider.getContext(),"Batch",Batch.class,"experimentid = "+experimentId);
    }

    public IModel<Batch> model(Batch batch) {
        return new BatchModel(batch);
    }
}
