package edu.mit.cci.amtprojects;

import java.text.DateFormat;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Hits;
import edu.mit.cci.amtprojects.solver.SolverPluginFactory;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Hits;
import edu.mit.cci.amtprojects.solver.SolverPluginFactory;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.Utils;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 10:23 PM
 */
@AuthorizeInstantiation("ADMIN")
public class HitsPage extends WebPage {

    private static final long serialVersionUID = 1L;
    private static int itemsPerPage = 50;
    private static Logger log = Logger.getLogger(HitsPage.class);
    private long batchid;
    private  AjaxLink<?> haltProcessor;
    private AjaxLink<?> restartProcessor;
     private AjaxLink<?> expireHits;
     private AjaxLink<?> extendHits;
     private AjaxLink<?> relaunchHits;

    PagingNavigator pagingNavigator;




    public HitsPage(PageParameters parameters) {


        if (parameters.get("batch").isEmpty()) {
           parameters.set("error","Batch not valid");
            throw new RestartResponseException(HomePage.class,parameters);
        } else {
            batchid = parameters.get("batch").toLong();

            if (batch() == null) {
                parameters.set("error","Batch not valid");
                throw new RestartResponseException(HomePage.class,parameters);
            }

        }

        add(new Label("experimentId",batch().getToExperiment().getExperimentId()+""));
        add(new Label("experimentName",batch().getToExperiment().getName()+""));
        add(new Label("batchName",batch().getName()));
        add(new Label("batchMode",batch().getIsReal()?"Real":"Sandbox"));
        add(new Label("awsId",batch().getAwsId()));
        final Label statusLabel = new Label("status",new Model<String>() {

            @Override
            public String getObject() {
              return getBatchStatus().name();
            }
        });

        restartProcessor = new AjaxLink<Object>("restartProcessor") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                log.info("Received restart");
               BatchManager manager = new SolverPluginFactory().getBatchManager();
                manager.restartBatchProcessor(batch(), Utils.getUrlCreator(this));
                target.add(haltProcessor,statusLabel);
            }

            public boolean isEnabled() {
                return getBatchStatus() != BatchManager.Status.COMPLETE;
            }
        };

         haltProcessor = new AjaxLink<Object>("haltProcessor") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                log.info("Received halt");
                BatchManager manager = new SolverPluginFactory().getBatchManager();
                manager.haltBatchProcessor(batch());
                target.add(restartProcessor,statusLabel);
            }

             public boolean isEnabled() {
                return getBatchStatus() == BatchManager.Status.RUNNING;
            }
        };

         expireHits = new AjaxLink<Object>("expireHits") {
             @Override
             public void onClick(AjaxRequestTarget target) {
                BatchManager manager = new SolverPluginFactory().getBatchManager();
                manager.expireBatch(batch());

             }
         };

         extendHits = new AjaxLink<Object>("extendHits") {
             @Override
             public void onClick(AjaxRequestTarget target) {
                BatchManager manager = new SolverPluginFactory().getBatchManager();
                manager.extendBatch(batch());

             }
         };

        relaunchHits = new AjaxLink<Object>("relaunchHits") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                 BatchManager manager = new SolverPluginFactory().getBatchManager();
                manager.restartActiveHits(batch());
            }
        };





        add(restartProcessor.setOutputMarkupId(true), haltProcessor.setOutputMarkupId(true), expireHits,extendHits,relaunchHits);
        add(statusLabel.setOutputMarkupId(true));


        final DataView<Hits> dataView = new DataView<Hits>("pageable", new HitDataProvider(batch())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Hits> item) {
                Hits hit = item.getModelObject();
                item.add(new Label("hitId", String.valueOf(hit.getId())));
                item.add(new Label("creation", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(hit.getCreation())));
                item.add(new Label("status", hit.getStatus()));
                item.add(new Label("amtstatus",hit.getAmtStatus()));
                item.add(new Label("assignmentsRequested", String.valueOf(hit.getRequested())));
                item.add(new Label("assignmentsCompleted", String.valueOf(hit.getCompleted())));
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
        container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(15)) {

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                log.info("Refresh");
                HitManager.get(batch()).updateHits();
                target.add(haltProcessor, restartProcessor,statusLabel);
                super.onPostProcessTarget(target);    //To change body of overridden methods use File | Settings | File Templates.

            }
        });
        container.setOutputMarkupId(true);

        add(container);

    }


    public BatchManager.Status getBatchStatus() {

           return new SolverPluginFactory().getBatchManager().getStatus(CayenneUtils.findBatch(DbProvider.getContext(),batch().getId()));

    }

    public Batch batch() {
        return CayenneUtils.findBatch(DbProvider.getContext(),batchid);
    }






}
