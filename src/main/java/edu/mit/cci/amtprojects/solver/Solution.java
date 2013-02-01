package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.solver.cayenne._Solution;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.json.JSONStringer;

import static edu.mit.cci.amtprojects.DbProvider.getContext;

public class Solution extends _Solution {


    public String toJSONString() throws JSONException {
        return new JSONStringer().object().key("solution").value(this.getObjectId()).toString();
    }

    public Solution fromJSONString(String str) throws JSONException {
        JSONObject obj = new JSONObject(str);
        if (obj.has("solution")) {
            return DataObjectUtils.objectForPK(getContext(), Solution.class,obj.getLong("solution"));
        }
        return null;
    }

    public SolutionRank getLastRank() {
        return getToRanks().get(getToRanks().size()-1);
    }

     public Long getId() {
        return CayenneUtils.extractObjectId(this);
    }

    public void setValid(Valid v) {
        this.setValid(v.name());
    }

    public Valid getValidEnum() {
        return this.getValid()==null?null:Valid.valueOf(getValid());
    }

    public static enum Valid {
        UNKNOWN, NEEDS_APPROVAL, VALID, INVALID
    }
}
