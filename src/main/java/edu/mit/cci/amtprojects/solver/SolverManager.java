package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * User: jintrone
 * Date: 11/6/12
 * Time: 11:11 AM
 */
public class SolverManager extends WebPage {


    public String message = "No batch started";

    public SolverManager(PageParameters p) {

        if (p.getNamedKeys().contains("batch")) {
            Long l = p.get("batch").toLong();
            Batch b = CayenneUtils.findBatch(DbProvider.getContext(),l);
            SolverProcessMonitor.get(b.getToExperiment());
            setMessage("Should have restarted "+b.getToExperiment().getExperimentId());
        }

        add(new Label("message",new PropertyModel<String>(this,"message")));



    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }
}
