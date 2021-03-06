package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.BatchManager;
import edu.mit.cci.amtprojects.UrlCreator;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Hits;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONException;

import java.util.ArrayList;

/**
 * User: jintrone
 * Date: 11/12/12
 * Time: 10:29 PM
 */
public class SolverBatchManager implements BatchManager {

    private static Logger logger = Logger.getLogger(SolverBatchManager.class);


    public void restartBatchProcessor(Batch b,UrlCreator creator) {
        MultiHitSolverHitCreator.configure(creator);
        MultiHitSolverProcessMonitor monitor = MultiHitSolverProcessMonitor.get(b);
        if (!monitor.isRunning()) {
            monitor.restart();
        }

    }

    public void haltBatchProcessor(Batch b) {
       MultiHitSolverProcessMonitor monitor = MultiHitSolverProcessMonitor.get(b);
        if (monitor.isRunning()) {
            monitor.halt();
        }
    }

    public void extendBatch(Batch b) {
        HitManager manager = HitManager.get(b);
        manager.extendBatch(b,600000);
    }

    public void expireBatch(Batch b) {
        HitManager manager = HitManager.get(b);
        manager.expireBatch();
    }

    public Status getStatus(Batch b) {
       MultiHitSolverProcessMonitor monitor = MultiHitSolverProcessMonitor.get(b);

        try {
            SolverTaskModel model = new SolverTaskModel(b);
            if (model.getCurrentStatus().getPhase() == SolverPluginFactory.Phase.COMPLETE) {
                return Status.COMPLETE;
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        }
        return monitor.isRunning()?Status.RUNNING:Status.HALTED;


    }



    public synchronized void restartActiveHits(Batch b) {

        MultiHitSolverProcessMonitor monitor = MultiHitSolverProcessMonitor.get(b);
        monitor.halt();
        HitManager manager = HitManager.get(b);
        manager.updateHits();
        manager.expireBatch();
        for (Hits h:new ArrayList<Hits>(b.getHits())) {
            if (h.getStatusEnum() == Hits.Status.OPEN) {
                manager.reLaunch(h.getId());
            }
        }
        monitor.restart();
    }
}
