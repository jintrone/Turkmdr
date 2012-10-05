package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.IndexedIterator;
import org.apache.cayenne.query.SelectQuery;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/1/12
 * Time: 2:25 PM
 */
public class ExperimentDataProvider implements IDataProvider<Experiment> {


    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Iterator<? extends Experiment> iterator(int i, int i1) {
        return new IndexedIterator<Experiment>(((List<Experiment>)DbProvider.getContext().performQuery(new SelectQuery(Experiment.class))),i,i1);
    }

    public int size() {
        return CayenneUtils.count(DbProvider.getContext(),"Experiment",Experiment.class,"");
    }

    public IModel<Experiment> model(Experiment experiment) {
        return new ExperimentModel(experiment);
    }


    public static class ExperimentModel implements IModel<Experiment> {

        long id;

        public ExperimentModel(Experiment e) {
            this.id = e.getExperimentId();
        }

        public void detach() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public Experiment getObject() {
            return CayenneUtils.findExperiment(DbProvider.getContext(),id);
        }

        public void setObject(Experiment experiment) {
           this.id = experiment.getExperimentId();
        }
    }
}
