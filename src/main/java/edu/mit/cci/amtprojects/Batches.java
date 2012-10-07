package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.KickballHitCreator;
import edu.mit.cci.amtprojects.kickball.KickballHitForm;
import edu.mit.cci.amtprojects.kickball.KickballPostTask;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.io.IClusterable;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Date;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 10:23 PM
 */
public class Batches extends WebPage {

    private static final long serialVersionUID = 1L;
    private static int itemsPerPage = 50;
    private static Logger log = Logger.getLogger(Batches.class);
    Experiment experiment;
    PagingNavigator pagingNavigator;




    public Batches(PageParameters parameters) {


        if (parameters.get("experiment").isEmpty()) {
            parameters.set("error","No experiment specified!");
           throw new RestartResponseException(HomePage.class,parameters);
        } else {
            long id = parameters.get("experiment").toLong();
            experiment = CayenneUtils.findExperiment(DbProvider.getContext(),id);
            if (experiment == null) {
                parameters.set("error","Experiment not valid");
                 throw new RestartResponseException(HomePage.class,parameters);
            }

        }


        add(new Label("experimentId",experiment.getExperimentId()+""));
        add(new Label("experimentName",experiment.getName()+""));
        add(new Label("experimentClass",experiment.getClassname()));


        final DataView<Batch> dataView = new DataView<Batch>("pageable", new BatchDataProvider(experiment)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Batch> item) {
                final Batch batch = item.getModelObject();

                item.add(new Label("AWSId",batch.getAwsId()));
                item.add(new Label("creation", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(batch.getCreated())));
                item.add(new Label("sandbox",String.valueOf(batch.getIsReal())));
                Link<Batch> l = new Link<Batch>("manage", item.getModel()) {

                    @Override
                    public void onClick() {
                        Batch b = getModelObject();
                        PageParameters params = new PageParameters();
                        params.add("experiment",experiment.getExperimentId());
                        params.add("batch",batch.getId());
                        setResponsePage(Hits.class, params);
                    }
                };
                l.add(new Label("name",batch.getName()));
                item.add(l);
                item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return (item.getIndex() % 2 == 1) ? "even" : "odd";
                    }
                }));


            }
        };

        add(dataView);
        dataView.setItemsPerPage(itemsPerPage);
        pagingNavigator = new PagingNavigator("navigator", dataView);
        add(pagingNavigator);

        add(new Form<BatchFormModel>("batchForm",new CompoundPropertyModel<BatchFormModel>(new BatchFormModel(experiment.getExperimentId()))) {
            {


                add(new TextField<String>("name"));
                add(new TextField<String>("awsId"));
                add(new TextField<String>("awsSecret"));
                add(new CheckBox("isReal"));
                add(new Button("createButton"));
                add(new KickballHitForm("kickballForm"));

            }

            public void onSubmit() {

               System.err.println("Outer submit ");

                try {
                    Class.forName(experiment.getClassname());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    return;
                }

                Batch b = getModelObject().create();
                try {
                    KickballHitCreator.getInstance().launch(urlFor(KickballPostTask.class,null).toString(),b);
                } catch (MalformedURLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

        });


    }



    private static class BatchFormModel implements IClusterable {

        private String awsId;

         private String awsSecret;
        private boolean isReal = false;
        private String name;
        private Long experimentId;

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

        public void reset() {
            this.name = null;
            this.awsId = null;
            this.awsSecret = null;
            this.isReal = false;

        }

        public Batch create() {
            Batch b = DbProvider.getContext().newObject(Batch.class);
            b.setName(name);
            b.setAwsId(awsId);
            b.setAwsSecret(awsSecret);
            b.setCreated(new Date());
            b.setName(name);
            b.setIsReal(isReal);
            b.setToExperiment(CayenneUtils.findExperiment(DbProvider.getContext(),experimentId));
            DbProvider.getContext().commitChanges();
            return b;
        }



    }


}
