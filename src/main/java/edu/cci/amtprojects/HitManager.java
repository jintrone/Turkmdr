package edu.cci.amtprojects;

import com.amazonaws.mturk.requester.Assignment;
import com.amazonaws.mturk.requester.AssignmentStatus;
import com.amazonaws.mturk.requester.GetAssignmentResult;
import com.amazonaws.mturk.requester.HIT;
import com.amazonaws.mturk.requester.HITReviewStatus;
import com.amazonaws.mturk.requester.HITStatus;
import com.amazonaws.mturk.requester.QualificationRequirement;
import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.util.ClientConfig;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.FilePropertiesConfig;
import edu.mit.cci.amtprojects.util.MturkUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    private static Map<Batch, HitManager> managerMap = new HashMap<Batch, HitManager>();


    public HitManager(Batch batch) {
        setBatch(batch);


    }

    public void setBatch(Batch batch) {
        this.batch = batch.getId();
        ClientConfig config;
        try {
            config = new FilePropertiesConfig(getClass().getResourceAsStream("/global.mturk.properties"));
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

    public List<TurkerLog> getFilteredLogs(String... type) {
        populateResults();
        Batch b = CayenneUtils.findBatch(DbProvider.getContext(), batch);
        List<TurkerLog> logs = new ArrayList<TurkerLog>(b.getToLogs());
        List<String> accept = Arrays.asList(type);
        if (accept == null || accept.isEmpty()) return logs;
        for (Iterator<TurkerLog> i = logs.iterator(); i.hasNext(); ) {
            if (!accept.contains(i.next().getType())) {
                i.remove();
            }
        }
        return logs;

    }

    public void populateResults() {
        updateHits();
        for (HIT h : getAllHits()) {
            //only process completed hits
            //TODO do a better job with this, so that clients can choose completed hits or not
            if (h.getHITStatus() != HITStatus.Reviewable) continue;
            Assignment[] assignments = requesterService.getAllAssignmentsForHIT(h.getHITId());
            for (Assignment a : assignments) {
                List<TurkerLog> log = CayenneUtils.getTurkerLogForAssignment(DbProvider.getContext(), a.getAssignmentId(), "RESULTS");
                if (log == null || log.isEmpty()) {
                    TurkerLog nlog = DbProvider.getContext().newObject(TurkerLog.class);
                    nlog.setAssignmentId(a.getAssignmentId());
                    nlog.setHit(a.getHITId());
                    nlog.setDate(a.getSubmitTime().getTime());
                    nlog.setToBatch(CayenneUtils.findBatch(DbProvider.getContext(), batch));
                    nlog.setType("RESULTS");
                    nlog.setWorkerId(a.getWorkerId());
                    Map<String, String> result = new HashMap<String, String>();
                    result.put("answer", a.getAnswer());
                    JSONObject obj = new JSONObject(result);
                    nlog.setData(obj.toString());

                }
                if (a.getAssignmentStatus().equals(AssignmentStatus.Approved)) {
                    log = CayenneUtils.getTurkerLogForAssignment(DbProvider.getContext(), a.getAssignmentId(), "APPROVED");
                    if (log == null || log.isEmpty()) {
                        TurkerLog nlog = DbProvider.getContext().newObject(TurkerLog.class);
                        nlog.setAssignmentId(a.getAssignmentId());
                        nlog.setHit(a.getHITId());
                        nlog.setDate(a.getSubmitTime().getTime());
                        nlog.setToBatch(CayenneUtils.findBatch(DbProvider.getContext(), batch));
                        nlog.setType("APPROVED");
                        nlog.setWorkerId(a.getWorkerId());
                        Map<String, String> result = new HashMap<String, String>();
                        result.put("answer", a.getAnswer());
                        result.put("response", a.getRequesterFeedback());
                        JSONObject obj = new JSONObject(result);
                        nlog.setData(obj.toString());

                    }
                } else if (a.getAssignmentStatus().equals(AssignmentStatus.Rejected)) {
                    log = CayenneUtils.getTurkerLogForAssignment(DbProvider.getContext(), a.getAssignmentId(), "REJECTED");
                    if (log == null || log.isEmpty()) {
                        TurkerLog nlog = DbProvider.getContext().newObject(TurkerLog.class);
                        nlog.setAssignmentId(a.getAssignmentId());
                        nlog.setHit(a.getHITId());
                        nlog.setDate(a.getApprovalTime().getTime());
                        nlog.setToBatch(CayenneUtils.findBatch(DbProvider.getContext(), batch));
                        nlog.setType("REJECTED");
                        nlog.setWorkerId(a.getWorkerId());
                        Map<String, String> result = new HashMap<String, String>();
                        result.put("answer", a.getAnswer());
                        result.put("response", a.getRequesterFeedback());
                        JSONObject obj = new JSONObject(result);
                        nlog.setData(obj.toString());
                    }

                }

                DbProvider.getContext().commitChanges();


            }
        }
    }

    public void approveAssignments(String[] ids, String feedback) {
        for (String id : ids) {
            GetAssignmentResult result = requesterService.getAssignment(id);
            if (result.getAssignment() != null) {
                Assignment a = result.getAssignment();
                if (a.getAssignmentStatus().equals(AssignmentStatus.Submitted)) {
                    requesterService.approveAssignment(a.getAssignmentId(), feedback);
                    TurkerLog nlog = DbProvider.getContext().newObject(TurkerLog.class);
                    nlog.setAssignmentId(a.getAssignmentId());
                    nlog.setHit(a.getHITId());
                    nlog.setDate(new Date());
                    nlog.setToBatch(CayenneUtils.findBatch(DbProvider.getContext(), batch));
                    nlog.setType("APPROVED");
                    nlog.setWorkerId(a.getWorkerId());
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("response", feedback);
                    JSONObject obj = new JSONObject(data);
                    nlog.setData(obj.toString());
                    DbProvider.getContext().commitChanges();
                }
            }
        }
    }

    public void rejectAssignments(String[] ids, String feedback) {
        for (String id : ids) {
            GetAssignmentResult result = requesterService.getAssignment(id);
            if (result.getAssignment() != null) {
                Assignment a = result.getAssignment();
                if (a.getAssignmentStatus().equals(AssignmentStatus.Submitted)) {
                    requesterService.rejectAssignment(a.getAssignmentId(), feedback);
                    TurkerLog nlog = DbProvider.getContext().newObject(TurkerLog.class);
                    nlog.setAssignmentId(a.getAssignmentId());
                    nlog.setHit(a.getHITId());
                    nlog.setDate(new Date());
                    nlog.setToBatch(CayenneUtils.findBatch(DbProvider.getContext(), batch));
                    nlog.setType("REJECTED");
                    nlog.setWorkerId(a.getWorkerId());
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("response", feedback);
                    JSONObject obj = new JSONObject(data);
                    nlog.setData(obj.toString());
                    DbProvider.getContext().commitChanges();
                }
            }
        }
    }

    public void bonusAssignments(String[] ids, String feedback, double amount) {
        for (String id : ids) {
            GetAssignmentResult result = requesterService.getAssignment(id);
            if (result.getAssignment() != null) {
                Assignment a = result.getAssignment();
                requesterService.grantBonus(a.getWorkerId(), Double.parseDouble(String.format("%.2f",amount)), a.getAssignmentId(), feedback);
                TurkerLog nlog = DbProvider.getContext().newObject(TurkerLog.class);
                nlog.setAssignmentId(a.getAssignmentId());
                nlog.setHit(a.getHITId());
                nlog.setDate(new Date());
                nlog.setToBatch(CayenneUtils.findBatch(DbProvider.getContext(), batch));
                nlog.setType("BONUSED");
                nlog.setWorkerId(a.getWorkerId());
                Map<String, String> data = new HashMap<String, String>();
                data.put("response", feedback);
                data.put("amount", String.format("%.2f", amount));
                JSONObject obj = new JSONObject(data);
                nlog.setData(obj.toString());
                DbProvider.getContext().commitChanges();

            }
        }
    }

    protected String getAnnotation(String value) {

        if (!value.contains("batchId=")) {
            value = "batchId=" + batch + ";" + value;
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
            managerMap.put(batch, manager = new HitManager(batch));
        }
        return manager;
    }
}
