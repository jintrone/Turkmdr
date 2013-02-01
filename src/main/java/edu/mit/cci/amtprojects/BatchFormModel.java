package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.wicket.util.io.IClusterable;

import java.util.Date;

/**
* User: jintrone
* Date: 1/11/13
* Time: 10:18 AM
*/
class BatchFormModel implements IClusterable {

    private String awsId;

    private String awsSecret;
    private boolean isReal = false;
    private String name;
    private Long experimentId;
    private Long restartRate;

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    private String contactEmail;

    public boolean isAutoApprove() {
        return autoApprove;
    }

    public void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    private boolean autoApprove;

    public BatchFormModel(Long experimentId) {
        this.experimentId = experimentId;
    }

    public String getAwsId() {
        return awsId;
    }

    public void setAwsId(String awsId) {
        this.awsId = awsId;
    }

    public String getAwsSecret() {
        return awsSecret;
    }

    public void setAwsSecret(String awsSecret) {
        this.awsSecret = awsSecret;
    }

    public boolean isReal() {
        return isReal;
    }

    public void setReal(boolean real) {
        isReal = real;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRestartRate(long delay) {
        this.restartRate = delay;
    }

    public Long getRestartRate() {
        return restartRate;
    }


    public void reset() {
        this.name = null;
        this.awsId = null;
        this.awsSecret = null;
        this.isReal = false;
        this.restartRate = null;
        this.autoApprove = false;
        this.contactEmail = null;

    }

    public Batch create() {
        Batch b = DbProvider.getContext().newObject(Batch.class);
        b.setName(name);
        b.setAwsId(awsId);
        b.setAwsSecret(awsSecret);
        b.setCreated(new Date());
        b.setName(name);
        b.setIsReal(isReal);
        b.setRestartRate(restartRate);
        b.setAutoApprove(autoApprove);
        b.setContactEmail(contactEmail);
        b.setToExperiment(CayenneUtils.findExperiment(DbProvider.getContext(), experimentId));

        DbProvider.getContext().commitChanges();
        return b;
    }


}
