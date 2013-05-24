package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.util.io.IClusterable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 2:55 PM
 */
public class SolverTaskModel implements IClusterable {


    //serializaed props

    public String groupName = "";
    public int numberOfGenerators = 0;
    public int numberOfRankers = 0;
    public int numberOfRounds = 0;
    public int sizeOfFront = 0;
    public float maxRankingBonus = 0;


    public float maxImprovingBonus = 0;
    public float maxGeneratingBonus = 0;
    public float maxCombiningBonus = 0;
    public float baseReward = 0;
    public float validationReward = 0;

    public int numberOfValidators = 1;


    public List<Long> initialAnswerIds = new ArrayList<Long>();
    public Long questionId;


    //other props
    //names to write out
    static String[] names = new String[]{"validationReward", "numberOfValidators", "groupName", "numberOfGenerators", "numberOfRankers", "numberOfRounds", "sizeOfFront",
            "maxRankingBonus", "maxImprovingBonus", "maxGeneratingBonus", "maxCombiningBonus", "baseReward", "initialAnswerIds", "rankDimensions","rankDimensionsText","questionId", "validationReward"};

    public String[] initialAnswerText = new String[10];
    public String[] rankDimensions = new String[3];
    public String[] rankDimensionsText = new String[3];

    String questionText;
    private Long batchId;
    private SolverTaskStatus currentStatus;
    private static Logger log = Logger.getLogger(SolverTaskModel.class);


    public SolverTaskModel(Batch b) throws JSONException {
        this.batchId = b.getId();
        readFromBatch(b);
    }

    public SolverTaskModel() {
    }


    public void saveToBatch(Batch b) throws JSONException {
        this.batchId = b.getId();
        Question q;
        if (questionId == null) {
            if (questionText == null || questionText.isEmpty())
                throw new RuntimeException("Must specify question text before saving solver task");
            q = DbProvider.getContext().newObject(Question.class);
            q.setText(questionText);
            q.setToBatch(b);
            DbProvider.getContext().commitChanges();
            questionId = q.getId();


        } else {
            q = DataObjectUtils.objectForPK(DbProvider.getContext(), Question.class, questionId);


        }
        if (initialAnswerIds.isEmpty()) {

            List<Solution> solutions = new ArrayList<Solution>();
            for (String txt : initialAnswerText) {
                if (txt != null && !txt.isEmpty()) {
                    Solution s = DbProvider.getContext().newObject(Solution.class);
                    s.setText(txt);
                    s.setAssignmentId("<none>");
                    s.setRound(-1);
                    s.setCreation(new Date());
                    s.setWorkerId("<none>");
                    s.setToQuestion(q);
                    solutions.add(s);

                }
            }

            List<String> rd = new ArrayList<String>();
            List<String> rdt = new ArrayList<String>();

            for (int i=0;i<rankDimensions.length;i++) {
                if (rankDimensions[i] == null) {
                    continue;
                } else {
                   rd.add(rankDimensions[i]);
                    rdt.add(rankDimensionsText[i]);
                }

            }
            rankDimensions = rd.toArray(new String[rd.size()]);
            rankDimensionsText = rdt.toArray(new String[rdt.size()]);


            DbProvider.getContext().commitChanges();
            for (Solution s : solutions) {
                initialAnswerIds.add(s.getId());
            }

        }
        String jsonstring = this.toJSONString();
        b.setParameters(jsonstring);
        DbProvider.getContext().commitChanges();
    }

    public void updateCurrentStatus() {
        try {
            getCurrentStatus().update();
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        currentStatus = null;

    }

    public SolverTaskStatus getCurrentStatus() {
        if (currentStatus == null) {
            if (batchId == null) {
                log.warn("Cannot retrieve status if no batchid is available");
            } else {
                Batch b = CayenneUtils.findBatch(DbProvider.getContext(), batchId);
                if (b == null) {
                    log.error("Invalid batch id: " + batchId);
                    return null;
                }
                currentStatus = new SolverTaskStatus(b);
                try {
                    if (b.getToStatus().isEmpty()) {
                        currentStatus.setCurrentAnswers(getInitialAnswers());
                        currentStatus.setPhase(SolverPluginFactory.Phase.INIT);
                        currentStatus.setCurrentRound(0);
                        currentStatus.update();
                    } else {
                        currentStatus.read();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    log.error("Error parsing BatchStatus");
                    throw new RuntimeException("Error parsing batch status");
                }

            }
        }
        return currentStatus;
    }


    private void readFromBatch(Batch b) throws JSONException {
        String parameters = b.getParameters();
        setGroupName(Utils.getJsonString(parameters, "groupName"));
        setNumberOfGenerators(Integer.parseInt(Utils.getJsonString(parameters, "numberOfGenerators")));
        setNumberOfRankers(Integer.parseInt(Utils.getJsonString(parameters, "numberOfRankers")));
        setNumberOfRounds(Integer.parseInt(Utils.getJsonString(parameters, "numberOfRounds")));
        setSizeOfFront(Integer.parseInt(Utils.getJsonString(parameters, "sizeOfFront")));
        setMaxCombiningBonus(Float.parseFloat(Utils.getJsonString(parameters, "maxCombiningBonus")));
        setMaxRankingBonus(Float.parseFloat(Utils.getJsonString(parameters, "maxRankingBonus")));
        setMaxGeneratingBonus(Float.parseFloat(Utils.getJsonString(parameters, "maxGeneratingBonus")));
        setMaxImprovingBonus(Float.parseFloat(Utils.getJsonString(parameters, "maxImprovingBonus", "0")));
        setBaseReward(Float.parseFloat(Utils.getJsonString(parameters, "baseReward")));
        questionId = Long.parseLong(Utils.getJsonString(parameters, "questionId"));
        String answerArray = Utils.getJsonString(parameters, "initialAnswerIds");
        rankDimensions = Utils.getJsonArray(parameters,"rankDimensions", new String[0]);
        rankDimensionsText = Utils.getJsonArray(parameters,"rankDimensionsText", new String[0]);

        setNumberOfValidators(Integer.parseInt(Utils.getJsonString(parameters, "numberOfValidators", "0")));
        setValidationReward(Float.parseFloat(Utils.getJsonString(parameters, "validationReward", "0")));

        JSONArray array = new JSONArray(answerArray);
        initialAnswerIds = new ArrayList<Long>();
        for (int i = 0; i < array.length(); i++) {
            initialAnswerIds.add(array.getLong(i));
        }



    }

    private void setNumberOfValidators(int numberOfValidators) {
        this.numberOfValidators = numberOfValidators;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setQuestionText(String q) {
        this.questionText = q;
    }

    public String getQuestionText() {
        return questionText;
    }


    private String toJSONString() throws JSONException {
        String result = new JSONObject(this, names).toString();
        return result;
    }


    public float getBaseReward() {
        return baseReward;
    }

    public void setBaseReward(float baseReward) {
        this.baseReward = baseReward;
    }

    public String[] getRankDimensions() {
        return rankDimensions;
    }

    public String[] getRankDimensionsText() {
        return rankDimensionsText;
    }


    public List<Solution> getInitialAnswers() {
        String exp = "id in (" + Utils.join(initialAnswerIds, ",") + ")";
        log.debug("Expression string " + exp);

        SelectQuery query = new SelectQuery(Solution.class, Expression.fromString(exp));
        List<Solution> result = DbProvider.getContext().performQuery(query);
        return result;
    }


    public void setInitialAnswers(List<Solution> currentAnswers) {
        this.initialAnswerIds.clear();
        for (Solution s : currentAnswers) {
            this.initialAnswerIds.add(Long.valueOf(s.getObjectId().toString()));
        }


    }

    public float getMaxImprovingBonus() {
        return maxImprovingBonus;
    }

    public void setMaxImprovingBonus(float maxImprovingBonus) {
        this.maxImprovingBonus = maxImprovingBonus;
    }

    public float getMaxCombiningBonus() {
        return maxCombiningBonus;
    }

    public void setMaxCombiningBonus(float maxCombiningBonus) {
        this.maxCombiningBonus = maxCombiningBonus;
    }

    public float getMaxGeneratingBonus() {
        return maxGeneratingBonus;
    }

    public void setMaxGeneratingBonus(float maxGeneratingBonus) {
        this.maxGeneratingBonus = maxGeneratingBonus;
    }

    public float getMaxRankingBonus() {
        return maxRankingBonus;
    }

    public void setMaxRankingBonus(float maxRankingBonus) {
        this.maxRankingBonus = maxRankingBonus;
    }

    public int getNumberOfGenerators() {
        return numberOfGenerators;
    }

    public void setNumberOfGenerators(int numberOfGenerators) {
        this.numberOfGenerators = numberOfGenerators;
    }

    public int getNumberOfRankers() {
        return numberOfRankers;
    }

    public void setNumberOfRankers(int numberOfRankers) {
        this.numberOfRankers = numberOfRankers;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }


    public int getSizeOfFront() {
        return sizeOfFront;
    }

    public void setSizeOfFront(int sizeOfFront) {
        this.sizeOfFront = sizeOfFront;
    }


    //why you ask?  because building a dynamically size list in a wicket form
    //is not at all straightforward

    public void setInitialAnswer0(String answer) {
        initialAnswerText[0] = answer;
    }

    public void setInitialAnswer1(String answer) {
        initialAnswerText[1] = answer;
    }

    public void setInitialAnswer2(String answer) {
        initialAnswerText[2] = answer;
    }

    public void setInitialAnswer3(String answer) {
        initialAnswerText[3] = answer;
    }

    public void setInitialAnswer4(String answer) {
        initialAnswerText[4] = answer;
    }

    public void setInitialAnswer5(String answer) {
        initialAnswerText[5] = answer;
    }

    public void setInitialAnswer6(String answer) {
        initialAnswerText[6] = answer;
    }

    public void setInitialAnswer7(String answer) {
        initialAnswerText[7] = answer;
    }

    public void setInitialAnswer8(String answer) {
        initialAnswerText[8] = answer;
    }

    public void setInitialAnswer9(String answer) {
        initialAnswerText[9] = answer;
    }

    public String getInitialAnswer0() {
        return initialAnswerText[0];
    }

    public String getInitialAnswer1() {
        return initialAnswerText[1];
    }

    public String getInitialAnswer2() {
        return initialAnswerText[2];
    }

    public String getInitialAnswer3() {
        return initialAnswerText[3];
    }

    public String getInitialAnswer4() {
        return initialAnswerText[4];
    }

    public String getInitialAnswer5() {
        return initialAnswerText[5];
    }

    public String getInitialAnswer6() {
        return initialAnswerText[6];
    }

    public String getInitialAnswer7() {
        return initialAnswerText[7];
    }

    public String getInitialAnswer8() {
        return initialAnswerText[8];
    }

    public String getInitialAnswer9() {
        return initialAnswerText[9];
    }

    public void setRankDimension0(String txt) {
        rankDimensions[0] = txt;
    }

    public void setRankDimensionText0(String txt) {
        rankDimensionsText[0] = txt;
    }

    public void setRankDimension1(String txt) {
        rankDimensions[1] = txt;
    }

    public void setRankDimensionText1(String txt) {
        rankDimensionsText[1] = txt;
    }


    public void setRankDimension2(String txt) {
        rankDimensions[2] = txt;
    }

    public void setRankDimensionText2(String txt) {
        rankDimensionsText[2] = txt;
    }

    public String getRankDimension0() {
        return rankDimensions.length == 0?null:rankDimensions[0];
    }

    public String getRankDimensionText0() {
        return rankDimensions.length == 0?null:rankDimensionsText[0];
    }

    public String getRankDimension1() {
        return rankDimensions.length <2 ?null:rankDimensions[1];
    }

    public String getRankDimensionText1() {
        return rankDimensions.length <2 ?null:rankDimensionsText[1];
    }


    public String getRankDimension2() {
        return rankDimensions.length  <3?null:rankDimensions[2];
    }

    public String getRankDimensionText2() {
        return rankDimensions.length <3?null:rankDimensionsText[2];
    }


    public Question getQuestion() {
        return questionId != null ? DataObjectUtils.objectForPK(DbProvider.getContext(), Question.class, questionId) : null;
    }


    public Batch getBatch() {
        if (batchId == null) return null;
        else return CayenneUtils.findBatch(DbProvider.getContext(), batchId);
    }


    public int getNumberOfValidators() {
        return numberOfValidators;
    }

    public float getValidationReward() {
        return validationReward;
    }

    public void setValidationReward(float f) {
        this.validationReward = f;
    }
}
