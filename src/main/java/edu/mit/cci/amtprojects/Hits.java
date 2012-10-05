package edu.mit.cci.amtprojects;

import com.amazonaws.mturk.requester.HIT;
import edu.cci.amtprojects.DefaultEnabledHitProperties;
import edu.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.KickballPostTask;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 10:23 PM
 */
public class Hits extends WebPage {

    private static final long serialVersionUID = 1L;
    private static int itemsPerPage = 50;
    private static Logger log = Logger.getLogger(Hits.class);
    private Batch batch;

    PagingNavigator pagingNavigator;




    public Hits(PageParameters parameters) {


        if (parameters.get("batch").isEmpty()) {
           parameters.set("error","Batch not valid");
            throw new RestartResponseException(HomePage.class,parameters);
        } else {
            long id = parameters.get("batch").toLong();
            batch = CayenneUtils.findBatch(DbProvider.getContext(), id);
            if (batch == null) {
                parameters.set("error","Batch not valid");
                throw new RestartResponseException(HomePage.class,parameters);
            }

        }

        add(new Label("experimentId",batch.getToExperiment().getExperimentId()+""));
        add(new Label("experimentName",batch.getToExperiment().getName()+""));
        add(new Label("batchName",batch.getName()));
        add(new Label("batchMode",batch.getIsReal()?"Real":"Sandbox"));
        add(new Label("awsId",batch.getAwsId()));

        final DataView<HIT> dataView = new DataView<HIT>("pageable", new HitDataProvider(batch)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<HIT> item) {
                HIT hit = item.getModelObject();
                item.add(new Label("hitId", String.valueOf(hit.getHITId())));
                item.add(new Label("creation", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(hit.getCreationTime().getTime())));
                item.add(new Label("status", hit.getHITStatus().getValue()));
                item.add(new Label("assignmentsRequested", String.valueOf(hit.getMaxAssignments())));
                item.add(new Label("assignmentsCompleted", String.valueOf(hit.getNumberOfAssignmentsCompleted())));
                item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return (item.getIndex() % 2 == 1) ? "even" : "odd";
                    }
                }));


            }
        };
        dataView.setOutputMarkupId(true);

        dataView.setItemsPerPage(itemsPerPage);
        pagingNavigator = new PagingNavigator("navigator", dataView);
        add(pagingNavigator);
        WebMarkupContainer container = new WebMarkupContainer("hitlist");
        container.add(dataView);
        container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(30)) {

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                log.info("Refresh");
                HitManager.get(batch).updateHits();
                super.onPostProcessTarget(target);    //To change body of overridden methods use File | Settings | File Templates.

            }
        });
        container.setOutputMarkupId(true);

        add(container);


        add(new AjaxLink("submitTask") {

            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {

                HttpServletRequest req = (HttpServletRequest)((WebRequest)RequestCycle.get().getRequest()).getContainerRequest();
                String relativePath = urlFor(KickballPostTask.class,null).toString();
                String url =  RequestUtils.toAbsolutePath(req.getRequestURL().toString(),relativePath);
                DefaultEnabledHitProperties props = new DefaultEnabledHitProperties();
                props.setTitle("Test hit");
                props.setDescription("Establish the reply sequence in a series of message forum posts");
                props.setMaxAssignments("1");
                props.setRewardAmount(".01");
                HitManager.get(batch).launch(url, 800, props);
                log.debug("Got url: "+url);
            }
        });




    }






}
