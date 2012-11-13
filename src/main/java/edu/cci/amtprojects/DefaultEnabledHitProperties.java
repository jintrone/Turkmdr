package edu.cci.amtprojects;

import com.amazonaws.mturk.addon.HITProperties;
import com.amazonaws.mturk.requester.QualificationRequirement;
import org.apache.wicket.ajax.json.JSONObject;

import java.io.IOException;
import java.util.Properties;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 9:28 PM
 */
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
        JSONObject obj = new JSONObject(this,new String[] {"annotation","assignmentDuration","Lifetime",
        "autoApprovalDelay","description","keywords","maxAssignments","rewardAmount","title"});
        return obj.toString();
    }
}
