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
        Form<KickballTaskModel> form = new Form<KickballTaskModel>("kickballHitForm",
                new CompoundPropertyModel<KickballTaskModel>(new KickballTaskModel())) {

            public void onSubmit() {
               callback.setData(getModelObject());
            }

        };

        form.add(new TextField<Long>("trainingPostFirst"));
        form.add(new TextField<Long>("trainingPostLast"));
        form.add(new TextField<Long>("trainingItems"));
        form.add(new TextField<Long>("numberOfWorkersToQualify"));
        form.add(new TextField<Float>("qualifierProportion"));
        form.add(new TextField<Float>("qualifierReward"));

        form.add(new TextField<Long>("threadId"));
        form.add(new TextField<Long>("assignmentsPerHit"));
        form.add(new TextField<Float>("taskBonus"));
        form.add(new TextField<Float>("taskReward"));
        add(form);

    }


}
