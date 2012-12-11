package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.BatchProcessMonitor;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.solver.SolverProcessMonitor;
import edu.mit.cci.amtprojects.solver.SolverTaskModel;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: jintrone
 * Date: 12/5/12
 * Time: 9:32 AM
 */
public class KickballProcessMonitor extends BatchProcessMonitor {

    private static Logger logger = Logger.getLogger(KickballProcessMonitor.class);

    public KickballProcessMonitor(Batch b) {
        super(b);
    }

    @Override
    public void update() throws UnsupportedEncodingException, JSONException {
         logger.info("Checking status");
        Batch b = CayenneUtils.findBatch(DbProvider.getContext(), batchId);
        SolverTaskModel model = new SolverTaskModel(b);
        if (model.getCurrentStatus().getPhase() == SolverProcessMonitor.Phase.COMPLETE) {

            t.cancel();
        }

        int currentRound = model.getCurrentStatus().getCurrentRound();
        HitManager manager = HitManager.get(b);
        manager.updateHits();
    }
}
