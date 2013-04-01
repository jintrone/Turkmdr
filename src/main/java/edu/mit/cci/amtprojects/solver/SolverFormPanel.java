package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.InnerFormCallback;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 2:51 PM
 */
public class SolverFormPanel extends Panel {

    public SolverFormPanel(String id, final InnerFormCallback callback) {
        super(id);
        Form<SolverTaskModel> form = new Form<SolverTaskModel>("solverHitForm",
                new CompoundPropertyModel<SolverTaskModel>(new SolverTaskModel())) {

            public void onSubmit() {
                callback.setData(getModelObject());
            }

        };

        form.add(new TextField<String>("groupName"));
        form.add(new TextField<Integer>("numberOfGenerators"));
        form.add(new TextField<Integer>("numberOfRankers"));
        form.add(new TextField<Integer>("numberOfRounds"));
        form.add(new TextField<Integer>("sizeOfFront"));
        form.add(new TextField<Double>("maxRankingBonus"));
        form.add(new TextField<Double>("maxGeneratingBonus"));
        form.add(new TextField<Double>("maxImprovingBonus"));
        form.add(new TextField<Double>("maxCombiningBonus"));
        form.add(new TextField<Double>("baseReward"));
        form.add(new TextField<String>("questionText"));
        form.add(new TextField<Double>("validationReward"));
        form.add(new TextField<Integer>("numberOfValidators"));

        form.add(new TextField<String>("rankDimension0"));
        form.add(new TextArea<String>("rankDimensionText0"));
        form.add(new TextField<String>("rankDimension1"));
        form.add(new TextArea<String>("rankDimensionText1"));
        form.add(new TextField<String>("rankDimension2"));
        form.add(new TextArea<String>("rankDimensionText2"));

        form.add(new TextField<String>("initialAnswer0"));
        form.add(new TextField<String>("initialAnswer1"));
        form.add(new TextField<String>("initialAnswer2"));
        form.add(new TextField<String>("initialAnswer3"));
        form.add(new TextField<String>("initialAnswer4"));
        form.add(new TextField<String>("initialAnswer5"));
        form.add(new TextField<String>("initialAnswer6"));
        form.add(new TextField<String>("initialAnswer7"));
        form.add(new TextField<String>("initialAnswer8"));
        form.add(new TextField<String>("initialAnswer9"));


        add(form);

    }
}
