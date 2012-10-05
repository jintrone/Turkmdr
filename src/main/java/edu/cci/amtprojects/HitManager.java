package edu.cci.amtprojects;

import com.amazonaws.mturk.requester.HIT;
import com.amazonaws.mturk.requester.QualificationRequirement;
import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.util.ClientConfig;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.util.FilePropertiesConfig;
import edu.mit.cci.amtprojects.util.MturkUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 3:27 PM
 */
public class HitManager {
     private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(HitManager.class);


    private RequesterService requesterService;
    List<HIT> hits = new ArrayList<HIT>();
    boolean isModified = false;

    private Long batch;

    private static Map<Batch,HitManager> managerMap = new HashMap<Batch, HitManager>();



    public HitManager(Batch batch) {
        setBatch(batch);


    }

    public void setBatch(Batch batch) {
        this.batch = batch.getId();
        ClientConfig config;
        try {
            config=new FilePropertiesConfig(getClass().getResourceAsStream("/global.mturk.properties"));
        } catch (IOException e) {
            log.error("Could not read global properties file: global.mturk.properties");
            config = new ClientConfig();
        }

        config.setAccessKeyId(batch.getAwsId());
        config.setSecretAccessKey(batch.getAwsSecret());

        if (batch.getIsReal()) {
            config.setServiceURL(ClientConfig.PRODUCTION_SERVICE_URL);

        } else {
            config.setServiceURL(ClientConfig.SANDBOX_SERVICE_URL);

        }

        requesterService = new RequesterService(config);
        updateHits();
    }


    public boolean isModified() {
        return isModified;
    }


    public void launch(String url, int height, DefaultEnabledHitProperties props) {

        requesterService.createHIT(
                null, // hitTypeId
                props.getTitle("No title"),
                props.getDescription("No description"),
                props.getKeywords(null), // keywords
                MturkUtils.getExternalQuestion(url, height),
                props.getRewardAmount(0),
                props.getAssignmentDuration(60 * 5),
                props.getAutoApprovalDelay(60 * 30),
                props.getLifetime(60 * 60 * 15),
                props.getMaxAssignments(1),
                getAnnotation(props.getAnnotation(null)), // requesterAnnotation
                props.getQualificationRequirements(new QualificationRequirement[0]), // qualificationRequirements
                new String[]{"Minimal", "HITDetail", "HITQuestion", "HITAssignmentSummary"}, // responseGroup
                null, // uniqueRequestToken
                null, // assignmentReviewPolicy
                null); // hitReviewPolicy
    }


    public List<HIT> getAllHits() {
        return hits;

    }

    protected String getAnnotation(String value) {

        if (!value.contains("batchId=")) {
            value = "batchId="+batch+";"+value;
        }
        return value;
    }


    public void updateHits() {

        List<HIT> hits = new ArrayList<HIT>(Arrays.asList(requesterService.searchAllHITs()));
        for (Iterator<HIT> hitsI = hits.iterator(); hitsI.hasNext(); ) {
            HIT h = hitsI.next();

            if (!Long.valueOf(MturkUtils.parseBatchId(h)).equals(batch)) {
                hitsI.remove();
            }
        }
        isModified = !(hits.size() == this.hits.size() && hits.containsAll(this.hits));
        if (isModified) {
            this.hits = hits;

        }

    }

    public static HitManager get(Batch batch) {
        HitManager manager = managerMap.get(batch);
        if (manager == null) {
            managerMap.put(batch,manager=new HitManager(batch));
        }
        return manager;
    }
}
