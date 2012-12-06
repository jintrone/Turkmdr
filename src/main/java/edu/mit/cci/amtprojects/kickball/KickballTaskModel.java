package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.GenericPropertiesModel;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.util.io.IClusterable;

/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 2:55 PM
 */
public class KickballTaskModel extends GenericPropertiesModel implements IClusterable {


    public static final String TRAINING_POST_FIRST = "trainingPostFirst";
    public static final String TRAINING_POST_LAST = "trainingPostLast";
    public static final String TRAINING_ITEMS_COUNT = "trainingItemsCount";
    public static final String NUMBER_OF_WORKERS = "numberOfWorkers";
    public static final String QUALIFIER_PROPORTION = "qualifierProportion";
    public static final String QUALIFIER_REWARD = "qualifierReward";

    public static final String THREAD_ID = "threadId";
    public static final String ASSIGNMENTS_PER_HIT = "assignmentsPerHit";
    public static final String TASK_BONUS = "taskBonus";
    public static final String TASK_REWARD = "taskReward";



    private Long batchId;
    private static Logger log = Logger.getLogger(KickballTaskModel.class);


    public KickballTaskModel(Batch b) throws JSONException {
         super(b.getParameters());
        this.batchId = b.getId();

    }

    public KickballTaskModel() {
    }


    public void saveToBatch(Batch b) throws JSONException {
        this.batchId = b.getId();
        b.setParameters(this.toJSONString());
        DbProvider.getContext().commitChanges();
    }

    public Long getTrainingPostFirst() {
       return getLong(TRAINING_POST_FIRST);
    }

    public void setTrainingPostFirst(long first) {
        setProperty(TRAINING_POST_FIRST,first);
    }

    public void setTrainingPostLast(long last) {
        setProperty(TRAINING_POST_LAST,last);
    }

    public Long getTrainingPostLast() {
        return getLong(TRAINING_POST_LAST);
    }

    public void setTrainingItemsCount(int count) {
        setProperty(TRAINING_ITEMS_COUNT,count);
    }

    public Integer getTrainingItemsCount() {
        return getInt(TRAINING_ITEMS_COUNT);
    }

    public void setNumberOfWorkers(int workers) {
        setProperty(NUMBER_OF_WORKERS,workers);
    }

    public Integer getNumberOfWorkers() {
        return getInt(NUMBER_OF_WORKERS);
    }

    public void setQualifierProportion(float proportion) {
        setProperty(QUALIFIER_PROPORTION,proportion);
    }

    public Float getQualifierProportion() {
        return getFloat(QUALIFIER_PROPORTION);
    }

    public void setQualifierReward(float reward) {
        setProperty(QUALIFIER_REWARD,reward);
    }

    public Float getQualifierReward() {
        return getFloat(QUALIFIER_REWARD);
    }

    public Long getThreadId() {
        return getLong(THREAD_ID);
    }

    public void setThreadId(Long threadid) {
        setProperty(THREAD_ID,threadid);
    }

    public Integer getAssignmentsPerHit() {
        return getInt(ASSIGNMENTS_PER_HIT);

    }

    public void setAssignmentsPerHit(int val) {
        setProperty(ASSIGNMENTS_PER_HIT,val);
    }

    public void setTaskBonus(float val) {
        setProperty(TASK_BONUS,val);
    }

    public Float getTaskBonus() {
        return getFloat(TASK_BONUS);
    }

    public void setTaskReward(float val) {
        setProperty(TASK_REWARD,val);
    }

    public Float getTaskReward() {
        return getFloat(TASK_REWARD);
    }




}