package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DateFormat;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 10:23 PM
 */
@AuthorizeInstantiation("ADMIN")
public class Batches extends WebPage {

    private static final long serialVersionUID = 1L;
    private static int itemsPerPage = 50;
    private static Logger log = Logger.getLogger(Batches.class);
    Experiment experiment;
    PagingNavigator pagingNavigator;


    public Batches(PageParameters parameters) {


        if (parameters.get("experiment").isEmpty()) {
            parameters.set("error", "No experiment specified!");
            throw new RestartResponseException(HomePage.class, parameters);
        } else {
            long id = parameters.get("experiment").toLong();
            experiment = CayenneUtils.findExperiment(DbProvider.getContext(), id);
            if (experiment == null) {
                parameters.set("error", "Experiment not valid");
                throw new RestartResponseException(HomePage.class, parameters);
            }

        }


        add(new Label("experimentId", experiment.getExperimentId() + ""));
        add(new Label("experimentName", experiment.getName() + ""));
        add(new Label("experimentClass", experiment.getClassname()));

        final PluginFactory pluginFactory;

        try {
            Class c = Class.forName(experiment.getClassname());
            pluginFactory = (PluginFactory)c.newInstance();
        } catch (Exception e) {
            parameters.set("error", "Experiment does not specify a valid plugin factory");
            throw new RestartResponseException(HomePage.class, parameters);
        }



        final DataView<Batch> dataView = new DataView<Batch>("pageable", new BatchDataProvider(experiment)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Batch> item) {
                final Batch batch = item.getModelObject();
                pluginFactory.getBatchManager();
                item.add(new Label("AWSId", batch.getAwsId()));
                item.add(new Label("creation", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(batch.getCreated())));
                item.add(new Label("sandbox", String.valueOf(batch.getIsReal())));
                item.add(new Label("status",pluginFactory.getBatchManager().getStatus(batch).name()));
                Link<Batch> l = new Link<Batch>("manage", item.getModel()) {

                    @Override
                    public void onClick() {
                        Batch b = getModelObject();
                        PageParameters params = new PageParameters();
                        params.add("experiment", experiment.getExperimentId());
                        params.add("batch", batch.getId());
                        setResponsePage(HitsPage.class, params);
                    }
                };
                l.add(new Label("name", batch.getName()));
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
        final Object[] innerFormData = new Object[1];



        add(new Form<BatchFormModel>("batchForm", new CompoundPropertyModel<BatchFormModel>(new BatchFormModel(experiment.getExperimentId()))) {
            {

                log.debug("I am creating the form");
                add(new TextField<String>("name"));
                add(new TextField<String>("awsId"));
                add(new TextField<String>("awsSecret"));
                add(new TextField<String>("contactEmail"));
                  add(new TextField<String>("restartRate"));
                add(new CheckBox("autoApprove"));
                add(new CheckBox("isReal"));
                add(new Button("createButton") {

                });
                add(pluginFactory.getFormPanel("pluginPanel",new InnerFormCallback.Basic(innerFormData)));

            }

            public void onValidate() {
                log.debug("Running validation");
                super.onValidate();
            }

            public void onSubmit() {
                log.debug("I am submitting!");

                Batch b = getModelObject().create();
                try {
                    pluginFactory.getHitCreator().launch(Utils.getUrlCreator(this), innerFormData[0], b);
                } catch (MalformedURLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

        });


    }


}
