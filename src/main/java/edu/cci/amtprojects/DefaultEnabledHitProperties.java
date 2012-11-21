package edu.cci.amtprojects;

import com.amazonaws.mturk.addon.HITProperties;
import com.amazonaws.mturk.requester.QualificationRequirement;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 9:28 PM
 */

//TODO Fix super class!  Who stores instance data in a static enum?
public class DefaultEnabledHitProperties extends HITProperties{

    public DefaultEnabledHitProperties() {
        super(new Properties());
    }

    public DefaultEnabledHitProperties(String propertyFile) throws IOException {
        super(propertyFile);
    }

    public String getAnnotation(String def) {
        try {
            return super.getAnnotation();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return def;
        }
    }

    public long getAssignmentDuration(long def) {
        try {
            return super.getAssignmentDuration();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return def;
        }
    }

    public long getLifetime(long def) {
        try {
            return super.getLifetime();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return def;
        }
    }

    public long getAutoApprovalDelay(long def) {
        try {
            return super.getAutoApprovalDelay();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return def;
        }
    }

    public String getDescription(String def) {
        try {
            return super.getDescription();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return def;
        }
    }

    public String getKeywords(String def) {
        try {
            return super.getKeywords();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return def;
        }
    }

    public int getMaxAssignments(int maxassignments) {
        try {
            return super.getMaxAssignments();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return maxassignments;
        }
    }

    public double getRewardAmount(double reward) {
        try {
            return super.getRewardAmount();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return reward;
        }
    }

    public String getTitle(String def) {
        try {
            return super.getTitle();    //To change body of overridden methods use File | Settings | File Templates.
        } catch (Exception ex) {
            return def;
        }
    }

    public QualificationRequirement[] getQualificationRequirements(QualificationRequirement[] reqs) {
        try {
            return super.getQualificationRequirements();
        } catch (Exception ex) {
            return reqs;
        }
    }

    public DefaultEnabledHitProperties(Properties props) {
        super(props);
    }

    public String toJSONString() {

        Map<String,Object> props = new HashMap<String, Object>();
        for (HITField f:HITField.values()) {
            if (f.getFieldValue() == null) {
                continue;
            }
            props.put(f.getFieldName(),f.getFieldValue());
        }
        JSONObject obj = new JSONObject(props);
        return obj.toString();
    }

    public static DefaultEnabledHitProperties readFromJSON(String s) throws JSONException {
        DefaultEnabledHitProperties props = new DefaultEnabledHitProperties();
        JSONObject obj = new JSONObject(s);
        for (Iterator i = obj.keys();i.hasNext();) {
            String key = (String)i.next();
            HITField.valueOf(key).setFieldValue(obj.getString(key));
        }
        return props;
    }
}
