package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.GenericTask;
import edu.mit.cci.amtprojects.HomePage;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 2:53 PM
 */
public class SolverRankingTask extends GenericTask {

    private static Logger logger = Logger.getLogger(SolverRankingTask.class);

    public SolverRankingTask(PageParameters param) {
        super(param,true,true);
        add(new Label("question", getModel().getQuestion().getText()));
        add(new Label("answerCount",""+getModel().getCurrentStatus().getCurrentAnswers().size()));

        DataView<Solution> dataView = new DataView<Solution>("answers", new ListDataProvider<Solution>(getModel().getCurrentStatus().getCurrentAnswers())) {
            @Override
            protected void populateItem(Item<Solution> solutionItem) {
                Solution sol = solutionItem.getModelObject();
                if (sol.getPersistenceState() == PersistenceState.HOLLOW) {
                    sol = (Solution) DataObjectUtils.objectForPK(DbProvider.getContext(), sol.getObjectId());
                }
                TextField<Integer> field = new TextField<Integer>("rank");
                field.add(new AttributeModifier("name","Solution."+sol.getId()));

                solutionItem.add(field);
                solutionItem.add(new MultiLineLabel("text", sol.getText()));
                logger.info("Adding label with "+sol.getText());


            }


        };
        getForm().add(dataView);
        getForm().add(new HiddenField<String>("round",new Model<String>(getModel().getCurrentStatus().getCurrentRound()+"")));
        getForm().add(new HiddenField<String>("phase",new Model<String>(getModel().getCurrentStatus().getPhase().name()+"")));
        getForm().add(new Button("submit") {
          public boolean isVisible() {
                    return !isPreview();
                }
        });
    }

}
