package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import org.apache.wicket.util.io.IClusterable;

import java.util.Collections;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 2:55 PM
 */
public class SolverTaskModel implements IClusterable {



    public SolverTaskModel() {}


    public void updateBatchParameters(Batch b) {

    }

    public void updateBatchStatus(Batch b) {

    }

    public SolverTaskModel readFromBatch(Batch b) {
        return this;
    }

    //

    public int getNumberGenerators() {
        return 0;
    }

    public int getNumberOfRankers() {
        return 0;
    }

    public int getNumberOfRounds() {
       return 0;
    }

    public int getSizeOfFront() {
        return 0;
    }

    public float getMaxRankingBonus() {
        return 0;
    }

    public float getMaxGeneratingBonus() {
        return 0;
    }

    public float getMaxCombiningBonus() {
        return 0;
    }

    public float getBaseReward() {
        return 0;
    }

    public List<String> getInitialAnswers() {
        return Collections.emptyList();
    }

    public String getQuestion() {
        return "";
    }



}
