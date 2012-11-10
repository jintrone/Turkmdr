package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.BatchStatus;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SelectQuery;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.json.JSONStringer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/22/12
 * Time: 1:32 PM
 */
public class SolverTaskStatus implements Serializable {

    long batchid;
    int currentRound = -1;
    SolverProcessMonitor.Phase phase;
    List<Long> currentSolutions = new ArrayList<Long>();


    public SolverTaskStatus(Batch b) {
        this.batchid = b.getId();


    }


    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentAnswers(List<Solution> currentAnswers) {
        this.currentSolutions.clear();
        for (Solution s : currentAnswers) {
            this.currentSolutions.add(s.getId());
        }


    }

    public SolverProcessMonitor.Phase getPhase() {
        return phase;
    }


    public void setPhase(SolverProcessMonitor.Phase phase) {
        this.phase = phase;
    }



    public List<Solution> getCurrentAnswers() {

        SelectQuery query = new SelectQuery(Solution.class, Expression.fromString("id in (" + Utils.join(currentSolutions, ",")+")"));
        return Collections.unmodifiableList((List<Solution>) DbProvider.getContext().performQuery(query));

    }

    public void addToCurrentAnswers(Solution s) {
        if (!currentSolutions.contains(s.getId())) {
            currentSolutions.add(s.getId());
        }

    }


    public void update() throws JSONException {
        Batch b = CayenneUtils.findBatch(DbProvider.getContext(),batchid);
        BatchStatus status = DbProvider.getContext().newObject(BatchStatus.class);
        status.setCreation(new Date());
        status.setToBatch(b);
        status.setJsonStatus(toJSONString());
        DbProvider.getContext().commitChanges();


    }

    public String toJSONString() throws JSONException {
        return new JSONStringer().object().key("Round").value(currentRound)
                .key("Phase").value(phase.name())
                .key("Solutions").value(new JSONArray(currentSolutions)).endObject().toString();
    }

    public void setCurrentRound(int i) {
        this.currentRound = i;
    }

    public void read() throws JSONException {
        Batch b = CayenneUtils.findBatch(DbProvider.getContext(),batchid);
        BatchStatus status = Utils.last(b.getToStatus());
        JSONObject obj = new JSONObject(status.getJsonStatus());
        setCurrentRound(obj.getInt("Round"));
        setPhase(SolverProcessMonitor.Phase.valueOf(obj.getString("Phase")));
        JSONArray array = obj.getJSONArray("Solutions");
        currentSolutions = new ArrayList<Long>();
        for (int i=0;i<array.length();i++) {
          currentSolutions.add(array.getLong(i));
        }

    }

    public int getNumberOfAnswers() {
        return currentSolutions==null?0:currentSolutions.size();
    }
}
