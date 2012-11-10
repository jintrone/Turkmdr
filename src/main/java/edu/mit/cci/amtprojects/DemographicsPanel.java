package edu.mit.cci.amtprojects;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.io.IClusterable;

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
        Form<DemographicsModel> form = new Form<DemographicsModel>("demographics", new CompoundPropertyModel<DemographicsModel>(new DemographicsModel())) {
            {

                add(new TextField<Integer>("age").setRequired(true));
                add(new DropDownChoice<String>("gender", Arrays.asList("Male", "Female")).setRequired(true));
                add(new TextField<String>("language").setRequired(true));
                add(new TextField<String>("countryOfOrigin").setRequired(true));
                add(new Button("submit"));


            }

            public void onSubmit() {
                DemographicsModel model = getModelObject();
                tlogger.logEvent("DEMOGRAPHICS", "age", model.getAge(), "gender", model.getGender(), "language", model.getLanguage(), "country", model.getCountryOfOrigin());
                requestDemographicInfo = false;
            }


        };
        add(form);
        add(new FeedbackPanel("feedback"));
    }

    public boolean isVisible() {
        return requestDemographicInfo;
    }

    public static class DemographicsModel implements IClusterable {

        public int age;
        public String gender;
        public String countryOfOrigin;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getCountryOfOrigin() {
            return countryOfOrigin;
        }

        public void setCountryOfOrigin(String countryOfOrigin) {
            this.countryOfOrigin = countryOfOrigin;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String language;


    }
}
