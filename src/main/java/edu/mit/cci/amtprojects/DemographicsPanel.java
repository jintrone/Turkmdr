package edu.mit.cci.amtprojects;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.Arrays;

/**
* User: jintrone
* Date: 11/9/12
* Time: 10:09 AM
*/
public class DemographicsPanel extends Panel {

    private boolean requestDemographicInfo = false;

    public DemographicsPanel(String id, boolean requestDemographics, final TurkLogger tlogger) {
        super(id);
        this.requestDemographicInfo = requestDemographics;
        Form<GenericTask.DemographicsModel> form = new Form<GenericTask.DemographicsModel>("demographics", new CompoundPropertyModel<GenericTask.DemographicsModel>(new GenericTask.DemographicsModel())) {
            {

                add(new TextField<Integer>("age"));
                add(new DropDownChoice<String>("gender", Arrays.asList("Male", "Female")));
                add(new TextField<String>("language"));
                add(new TextField<String>("countryOfOrigin"));


            }

            public void onSubmit() {
                GenericTask.DemographicsModel model = getModelObject();
                tlogger.logEvent("DEMOGRAPHICS", "age", model.getAge(), "gender", model.getGender(), "language", model.getLanguage(), "country", model.getCountryOfOrigin());
                requestDemographicInfo = false;
            }


        };
        add(form);
    }

    public boolean isVisible() {
        return requestDemographicInfo;
    }

}
