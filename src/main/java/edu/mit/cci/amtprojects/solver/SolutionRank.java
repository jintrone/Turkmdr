package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.solver.cayenne._SolutionRank;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.json.JSONStringer;


public class SolutionRank extends _SolutionRank {

    private static Logger log = Logger.getLogger(SolutionRank.class);

    public void setRank(float roundW, float var, float rank) throws JSONException {
        log.info("Setting rank of "+roundW+","+var+","+rank);
        setRank(new JSONStringer().object().key("W").value(roundW).key("sigma").value(var).key("rank").value(rank).endObject().toString());
    }

    public float getW() throws JSONException {
        if (getRank() == null || getRank().isEmpty()) {
            return 0f;
        } else {
            return (float) new JSONObject(getRank()).getDouble("W");
        }
    }

    public float getStdDev() throws JSONException {
        if (getRank() == null || getRank().isEmpty()) {
            return 0f;
        } else {
            return (float) new JSONObject(getRank()).getDouble("sigma");
        }

    }

    public float getRankValue()  {
        if (getRank() == null || getRank().isEmpty()) {
            return 0f;
        } else {
            try {
                return (float) new JSONObject(getRank()).getDouble("rank");
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return 0f;
            }
        }

    }

}
