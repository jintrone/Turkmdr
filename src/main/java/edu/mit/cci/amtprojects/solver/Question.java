package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.solver.cayenne._Question;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.cayenne.DataObjectUtils;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.json.JSONStringer;

import static edu.mit.cci.amtprojects.DbProvider.getContext;

public class Question extends _Question {

public String toJSONString() throws JSONException {
        return new JSONStringer().object().key("question").value(this.getObjectId()).toString();
    }

    public Question fromJSONString(String str) throws JSONException {
        JSONObject obj = new JSONObject(str);
        if (obj.has("question")) {
            return DataObjectUtils.objectForPK(getContext(), Question.class, obj.getLong("question"));
        }
        return null;
    }


    public Long getId() {
        return CayenneUtils.extractObjectId(this);
    }
}
