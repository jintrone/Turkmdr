package edu.mit.cci.amtprojects.kickball;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * User: jintrone
 * Date: 10/4/12
 * Time: 11:17 PM
 */
public class KickballHitForm extends Form<KickballHitModel> {


    public KickballHitForm(String id) {
        super(id, new CompoundPropertyModel<KickballHitModel>(new KickballHitModel()));


        add(new TextField<Long>("threadId"));
        add(new TextField<Long>("assignmentsPerHit"));


    }

    public void onSubmit() {
        System.err.println("Kickball got a submit");
        KickballHitCreator.getInstance().setModel(getModelObject());
    }

}
