package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.KickballProcessMonitor;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import edu.mit.cci.amtprojects.solver.Solution;
import edu.mit.cci.amtprojects.solver.SolutionRank;
import edu.mit.cci.amtprojects.solver.SolverHitCreator;
import edu.mit.cci.amtprojects.solver.SolverProcessMonitor;
import edu.mit.cci.amtprojects.solver.SolverTaskModel;
import edu.mit.cci.amtprojects.solver.SolverTaskStatus;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.MturkUtils;
import jsc.datastructures.MatchedData;
import jsc.descriptive.MeanVar;
import jsc.relatedsamples.FriedmanTest;
import org.apache.cayenne.DataObjectUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 2:55 PM
 */
public class BatchProcessMonitor extends KickballProcessMonitor {


    private BatchProcessMonitor(Batch b) {super();
        // if (!running) restart();
    }


    @Override public void update() throws UnsupportedEncodingException, JSONException {
        logger.info("Checking status");
        Batch b = CayenneUtils.findBatch(DbProvider.getContext(),batchId);
        SolverTaskModel model = new SolverTaskModel(b);
        if (model.getCurrentStatus().getPhase() == SolverProcessMonitor.Phase.COMPLETE) {

            t.cancel();
        }

        int currentRound = model.getCurrentStatus().getCurrentRound();
        HitManager manager = HitManager.get(b);
        manager.updateHits();



    }





}
