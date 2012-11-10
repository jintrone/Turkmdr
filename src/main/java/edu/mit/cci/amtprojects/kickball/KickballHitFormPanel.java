package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.InnerFormCallback;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * User: jintrone
 * Date: 10/4/12
 * Time: 11:17 PM
 */
public class KickballHitFormPanel extends Panel {


    public KickballHitFormPanel(String id, final InnerFormCallback callback) {
        super(id);
        Form<KickballHitModel> form = new Form<KickballHitModel>("kickballHitForm",
                new CompoundPropertyModel<KickballHitModel>(new KickballHitModel())) {

            public void onSubmit() {
               callback.setData(getModelObject());
            }

        };


        form.add(new TextField<Long>("threadId"));
        form.add(new TextField<Long>("assignmentsPerHit"));
        form.add(new TextField<Float>("bonus"));
        form.add(new TextField<Float>("reward"));
        add(form);

    }


}
