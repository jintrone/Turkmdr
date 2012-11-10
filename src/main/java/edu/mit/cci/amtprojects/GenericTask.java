package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import edu.mit.cci.amtprojects.solver.SolverTaskModel;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.io.IClusterable;


import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

/**
 * User: jintrone
 * Date: 10/17/12
 * Time: 11:41 PM
 */
public abstract class GenericTask extends WebPage implements TurkLogger {

    private static Logger log = Logger.getLogger(GenericTask.class);

    String assignmentId = "NONE";

    public SolverTaskModel getModel() {
        return model;
    }

    private SolverTaskModel model;

    public String getAssignmentId() {
        return assignmentId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public String getHitId() {
        return hitId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public PageParameters getParam() {
        return param;
    }

    public String getWorkerId() {
        return workerId;
    }

    public Form<?> getForm() {
        return form;
    }

    String workerId = "NONE";
    boolean isPreview = false;
    Long batchId;
    String ipAddress;
    private String hitId;
    private PageParameters param;
    private Form<?> form;
    boolean needsDemographicInfo = false;
    boolean requestDemographicInfo = false;
    boolean isSticky = false;
    String scriptText = "//nothing to see here";
    String scriptWrapper = "<script type='text/javascript'>%s</script>";
    String reject = "$(function() {$('body').empty().append('<h2>Please return this HIT</h2><p>You have done nothing wrong, but the requester for this HIT does not want you to complete it because you have already participated in a group of HITs that are building on each other, and the requester wants to ensure that the same people " +
            "work on the same group of HITs.<br><br>" +
            "You are part of %s. You can avoid this message by choosing other hits that have %s in the title.</p><p><small>NOTE: This messag" +
            "e is a temporary fix; we hope that Mechanical Turk itself will allow us to block specific workers from particular HITs, so that they do not show up under \"HITs Available To You\". Given this and other factors, Mechanical Turk requesters" +
            " generally do not care how many HITs you return.</small></p><p><b>Sorry for the inconvenience.</b></p>');});";

    public GenericTask(PageParameters param) {
        this(param, false, false);
    }

    public GenericTask(PageParameters param, boolean isSticky, boolean needsDemographicInfo) {
        this.param = param;

        batchId = param.get("batch").toLong();
        if (batch()==null) {
            param.set("error","No such batch");
            throw new RestartResponseException(HomePage.class,param);
        }
         try {
            model = new SolverTaskModel(batch());
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RestartResponseException(HomePage.class);
        }

        assignmentId = param.get("assignmentId").toString();
        if (this.assignmentId.equals("ASSIGNMENT_ID_NOT_AVAILABLE")) {
            isPreview = true;
        }
        if (!isPreview) {
            workerId = param.get("workerId").toString();
            hitId = param.get("hitId").toString();
            if (isSticky) {
                Set<Batch> batches = CayenneUtils.findWorkerBatches(workerId,batch().getToExperiment());
                batches.remove(batch());
                String groupname = model.getGroupName();
                for (Iterator<Batch> i = batches.iterator();i.hasNext();) {
                    Batch b = i.next();
                    String oldgroup = "";
                    try {
                        oldgroup = new SolverTaskModel(b).getGroupName();
                        if (!groupname.equals(oldgroup)) {
                            scriptText = String.format(reject,"["+oldgroup+"]","["+oldgroup+"]");
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }


            }

        }

        add(new Label("script",scriptText).setEscapeModelStrings(false));




        HttpServletRequest request = (HttpServletRequest) getRequestCycle().getRequest().getContainerRequest();
        ipAddress = request.getRemoteAddr();

        if (needsDemographicInfo && !isPreview()) {
            TurkerLog log = CayenneUtils.findWorkerDemographics(DbProvider.getContext(), workerId);
            requestDemographicInfo = log == null;

        }

        add(new DemographicsPanel("demographicsPanel",requestDemographicInfo,this));


        form = new Form<Void>("hitForm");
        form.add(new AttributeModifier("action", batch().getIsReal() ? "https://www.mturk.com/mturk/externalSubmit" : "http://workersandbox.mturk.com/mturk/externalSubmit"));
        form.add(new HiddenField<String>("assignmentId", new Model<String>(this.assignmentId + "")));
        add(form);
        logEvent("VIEW_PAGE");



    }


    public void logEvent(String type, Object... o) {
        Map<String, Object> params = Utils.mapify(o);
        params.put("clientip", ipAddress);
        CayenneUtils.logEvent(DbProvider.getContext(),
                CayenneUtils.findBatch(DbProvider.getContext(), batchId),
                type,
                isPreview ? "NONE" : workerId,
                isPreview ? "NONE" : hitId,
                isPreview ? "NONE" : assignmentId,
                param.toString(), params);
    }

    protected Batch batch() {
        return CayenneUtils.findBatch(DbProvider.getContext(), batchId);
    }


}



