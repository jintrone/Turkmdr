package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.DefaultEnabledHitProperties;
import edu.mit.cci.amtprojects.HitCreator;
import edu.mit.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.UrlCreator;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.util.MturkUtils;
import org.apache.wicket.ajax.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 3:57 PM
 */
public class MultiHitSolverHitCreator implements HitCreator {


    private static MultiHitSolverHitCreator instance;

    private static String rankerpath;
    private static String generatorpath;
    private static String validationpath;

    private MultiHitSolverHitCreator() {

    }

    public static MultiHitSolverHitCreator getInstance() {
        if (instance == null) {
            instance = new MultiHitSolverHitCreator();
        }
        return instance;
    }


    public void relaunch(UrlCreator creator, Batch b) {
        configure(creator);


    }

    public static void configure(UrlCreator creator) {
        if (creator != null) {
            if (rankerpath == null) {
                rankerpath = creator.getUrlFor(SolverRankingTask.class);

            }

            if (generatorpath == null) {
                generatorpath = creator.getUrlFor(SolverGenerationTask.class);
            }

            if (validationpath == null) {
                validationpath = creator.getUrlFor(SolverValidationTask.class);
            }


        }
    }


    public void launch(UrlCreator creator, Object m, Batch b) throws UnsupportedEncodingException, JSONException {

        SolverTaskModel model = (SolverTaskModel) m;


        if (model != null && model.getBatch() == null) {
            model.saveToBatch(b);
            if (creator != null) {
                configure(creator);
            }
//            if (creator != null) {
//                String props = Utils.updateJSONProperty(b.getParameters(), "SolverGenerationTaskURL", creator.getUrlFor(SolverGenerationTask.class));
//                props = Utils.updateJSONProperty(b.getParameters(), "SolverRankingTaskURL", creator.getUrlFor(SolverRankingTask.class));
//                b.setParameters(props);
//                DbProvider.getContext().commitChanges();
//            }
            MultiHitSolverProcessMonitor.get(b).restart();

        } else {
            model = new SolverTaskModel(b);
        }
        SolverTaskStatus status = model.getCurrentStatus();
        String groupText = "[" + model.getGroupName() + "]";
        if (status.getPhase() == SolverPluginFactory.Phase.INIT) {
            for (int i = 0; i < model.getRankDimensions().length; i++) {
                DefaultEnabledHitProperties props = new DefaultEnabledHitProperties();
                props.setTitle("Rank a set of answers to a question about climate change " + groupText);
                props.setDescription("Rank a set of answers to the question: " + model.getQuestionText() + "  Bonus of up to $." + String.format("%.2f", model.getMaxRankingBonus()));
                props.setKeywords("climate,experiment,rank,bonus");
                props.setMaxAssignments("" + model.getNumberOfRankers());
                props.setRewardAmount("" + model.getBaseReward());
                props.setAssignmentDuration("900");
                MturkUtils.addBatchAnnotation(props, b);
                props.setLifetime("600000");

                String launchurl = MturkUtils.addUrlParams(rankerpath, "batch", b.getId() + "",  "dimension", "" + i);
                HitManager.get(b).launch(launchurl, 1000, props);
            }


        } else if (status.getPhase() == SolverPluginFactory.Phase.GENERATE) {
            DefaultEnabledHitProperties props = new DefaultEnabledHitProperties();
            props.setTitle("Create or improve an answer to a question about climate change " + groupText);
            props.setDescription("Choose to create a new answer or improve existing answers to the question: " + model.getQuestionText() + "  Bonus of up to $." + String.format("%.2f", Math.max(model.getMaxGeneratingBonus(), model.getMaxCombiningBonus())));
            props.setKeywords("climate,experiment,bonus");
            props.setMaxAssignments("" + model.getNumberOfGenerators());
            props.setRewardAmount("" + model.getBaseReward());
            MturkUtils.addBatchAnnotation(props, b);
            props.setLifetime("600000");
            String launchurl = MturkUtils.addUrlParams(generatorpath, "batch", b.getId() + "");
            props.setAssignmentDuration("1800");
            HitManager.get(b).launch(launchurl, 1000, props);


        } else if (status.getPhase() == SolverPluginFactory.Phase.RANK) {
            for (int i = 0; i < model.getRankDimensions().length; i++) {
                DefaultEnabledHitProperties props = new DefaultEnabledHitProperties();
                props.setTitle("Rank a set of answers to a question about climate change [" + model.getGroupName() + "]");
                props.setDescription("Rank a set of answers to the question: " + model.getQuestionText() + "  Bonus of up to $." + String.format("%.2f", model.getMaxRankingBonus()));
                props.setKeywords("climate,experiment,rank,bonus");
                props.setMaxAssignments("" + model.getNumberOfRankers());
                props.setRewardAmount("" + model.getBaseReward());
                props.setLifetime("600000");
                MturkUtils.addBatchAnnotation(props, b);
                String launchurl = MturkUtils.addUrlParams(rankerpath, "batch", b.getId() + "", "dimension", "" + i);
                props.setAssignmentDuration("900");
                HitManager.get(b).launch(launchurl, 1000, props);
            }

        } else if (status.getPhase() == SolverPluginFactory.Phase.VALIDATION) {
            for (Solution s : status.getCurrentAnswers()) {
                if (s.getRound() == model.getCurrentStatus().getCurrentRound()) {
                    DefaultEnabledHitProperties props = new DefaultEnabledHitProperties();
                    props.setTitle("Check if an answer to a question is blank, spam, or a copy of another answer");
                    props.setDescription("Check if an answer to a question is blank, spam, or a copy of another answer. ");
                    props.setKeywords("validation,fast");
                    props.setMaxAssignments("" + model.getNumberOfValidators());
                    props.setRewardAmount("" + model.getValidationReward());
                    props.setLifetime("600000");
                    MturkUtils.addBatchAnnotation(props, b);
                    String launchurl = MturkUtils.addUrlParams(validationpath, "batch", b.getId() + "", "answer", s.getId() + "");
                    props.setAssignmentDuration("300");
                    HitManager.get(b).launch(launchurl, 1000, false, Collections.singletonList(s.getWorkerId()), props);
                }
            }
        }


    }


}
