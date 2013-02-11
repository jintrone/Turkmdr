package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.GenericTask;
import edu.mit.cci.amtprojects.HomePage;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 2:53 PM
 */
public class SolverValidationTask extends GenericTask {


    public static enum Mode {
        CHOOSE, GENERATE, COMBINE, IMPROVE
    }

    private Mode mode = Mode.CHOOSE;


    CheckGroup<Solution> group;

    private SolverTaskModel model;

    private List<Solution> solutions;

    private Solution target;

    private Set<Solution> copyOf = new HashSet<Solution>();

    private static Logger log = Logger.getLogger(SolverValidationTask.class);

    private boolean blank = false;

    public boolean isBlank() {
        return blank;
    }

    public void setBlank(boolean blank) {
        this.blank = blank;
    }

    public boolean isNonsense() {
        return nonsense;
    }

    public void setNonsense(boolean nonsense) {
        this.nonsense = nonsense;
    }

    public Set<Solution> getCopyOf() {
        return copyOf;
    }

    private boolean nonsense = false;


    public String getPhase() {
        return mode.name();
    }


    public SolverValidationTask(PageParameters param) {
        super(param, true, true);
        try {
            model = new SolverTaskModel(batch());
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RestartResponseException(HomePage.class);
        }

        solutions = new ArrayList<Solution>(model.getCurrentStatus().getCurrentAnswers());

        group = new CheckGroup<Solution>("copies", copyOf);

        for (Iterator<Solution> sit = solutions.iterator(); sit.hasNext(); ) {

            Solution s = sit.next();
            if (s.getId() == param.get("answer").toLong()) {
                target = s;
                sit.remove();
            } else if (s.getRound() == model.getCurrentStatus().getCurrentRound()) {
                sit.remove();
            }
        }
        if (target == null) {
            throw new RestartResponseException(HomePage.class);
        }

        add(new Label("question", model.getQuestion().getText()));
        add(new Label("answer", target.getText()));


        Form<?> form = getForm();

        form.add(new HiddenField<String>("answerId", new Model<String>(target.getId() + "")));
        form.add(new AttributeModifier("action", batch().getIsReal() ? "https://www.mturk.com/mturk/externalSubmit" : "http://workersandbox.mturk.com/mturk/externalSubmit"));
        form.add(new AttributeModifier("method", "POST"));
        form.setOutputMarkupId(true);
        form.add(new CheckBox("cbBlank", new PropertyModel<Boolean>(this, "blank")).add(new AttributeModifier("name", "blank")));
        form.add(new CheckBox("cbNonsense", new PropertyModel<Boolean>(this, "nonsense")).add(new AttributeModifier("name", "nonsense")));
        form.add(new CheckBox("cbGood", new PropertyModel<Boolean>(this, "good")).add(new AttributeModifier("name", "good")));


        form.add(new HiddenField<String>("phase", new Model<String>(model.getCurrentStatus().getPhase().name())));
        form.add(new HiddenField<Integer>("round", new Model<Integer>(model.getCurrentStatus().getCurrentRound())));

        DataView<Solution> dataView = new DataView<Solution>("answers", new ListDataProvider<Solution>(solutions)) {
            @Override
            protected void populateItem(Item<Solution> solutionItem) {
                final Solution sol = solutionItem.getModelObject();
                Solution sdata = sol;
                if (sdata.getPersistenceState() == PersistenceState.HOLLOW) {
                    sdata = (Solution) DataObjectUtils.objectForPK(DbProvider.getContext(), sol.getObjectId());
                }


                solutionItem.add(new Check<Solution>("check", solutionItem.getModel(), group).add(new AttributeModifier("name", "copies")).add(new AttributeModifier("value", sol.getId() + "")));
                solutionItem.add(new MultiLineLabel("text", sdata.getText()));
            }


        };

        group.add(dataView);
        form.add(group);

        form.add(new Button("submit") {

            public boolean isVisible() {
                return !isPreview();
            }


        });


    }
}
