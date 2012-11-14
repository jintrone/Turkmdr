package edu.mit.cci.amtprojects.solver;

import edu.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.BatchManager;
import edu.mit.cci.amtprojects.UrlCreator;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import org.apache.wicket.ajax.json.JSONException;

/**
 * User: jintrone
 * Date: 11/12/12
 * Time: 10:29 PM
 */
public class SolverBatchManager implements BatchManager {

    public void restartBatch(Batch b,UrlCreator creator) {
        SolverHitCreator.configure(creator);
        HitManager manager = HitManager.get(b);
        manager.extendBatch(b,600000);
        SolverProcessMonitor monitor = SolverProcessMonitor.get(b.getToExperiment());
        if (!monitor.isRunning()) {
            monitor.restart();
        }

    }

    public Status getStatus(Batch b) {
       SolverProcessMonitor monitor = SolverProcessMonitor.get(b.getToExperiment());

        try {
            SolverTaskModel model = new SolverTaskModel(b);
            if (model.getCurrentStatus().getPhase() == SolverProcessMonitor.Phase.COMPLETE) {
                return Status.COMPLETE;
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        }
        return monitor.isRunning()?Status.RUNNING:Status.HALTED;


    }

    public void haltBatch(Batch b) {
        HitManager manager = HitManager.get(b);
        manager.expireBatch();
    }

    public void relaunchBatch(Batch b) {
        HitManager manager = HitManager.get(b);
        manager.expireBatch();
        manager.populateResults(false);



    }
}
