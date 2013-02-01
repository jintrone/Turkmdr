package edu.mit.cci.amtprojects;

import java.text.DateFormat;


import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.eclipse.jetty.server.Authentication.User;

import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;
import edu.mit.cci.amtprojects.util.Mailer;

import javax.mail.MessagingException;

@AuthorizeInstantiation("ADMIN")
public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

    private static int itemsPerPage = 50;
    private PagingNavigator pagingNavigator;
    private static Logger logger = Logger.getLogger(HomePage.class);


    private class ExperimentInputForm extends Form<ExperimentFormModel> {

        public ExperimentInputForm(String id) {
            super(id,new CompoundPropertyModel<ExperimentFormModel>(new ExperimentFormModel()));

        }
    }

    public HomePage(final PageParameters parameters) {

          add(new Label("username",((MyAuthenticatedWebSession)getSession()).getUser().getUsername()));

          final DataView<Experiment> dataView = new DataView<Experiment>("pageable", new ExperimentDataProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Experiment> item) {
                Experiment exp = item.getModelObject();

                item.add(new Label("creation", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(exp.getCreated())));
                item.add(new Label("name", String.valueOf(exp.getName())));
                item.add(new Label("classname", String.valueOf(exp.getClassname())));
                item.add(new Label("batches", String.valueOf(exp.getToBatch().size())));
                Link l = new Link<Experiment>("manage", item.getModel()) {

                    @Override
                    public void onClick() {
                        Experiment e = getModelObject();
                        PageParameters params = new PageParameters();
                        params.add("experiment",e.getExperimentId());
                        setResponsePage(Batches.class, params);
                    }
                };
                l.add(new Label("experimentId",String.valueOf(exp.getExperimentId())));
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
        add(new Form<ExperimentFormModel>("experimentForm",new CompoundPropertyModel<ExperimentFormModel>(new ExperimentFormModel())) {
            {
                add(new TextField<String>("classname").setLabel(new Model<String>("Class name")));
                add(new TextField<String>("name").setLabel(new Model<String>("Experiment name")));
                add(new Button("createButton"));

            }

            public void onSubmit() {

               if (getModelObject().create()==null) {
                   logger.warn("Invalid experiment class");
               };
            }

        });
        
        //links to the global hit seleciton/deletion interface
        
        add(new Link("manageHits") {

            @Override
            public void onClick() {
                PageParameters params = new PageParameters();
                setResponsePage(GlobalManagePage.class, params);
            }
        });


        
        //TODO: form to add new credentials for this user
        
        






    }
}
