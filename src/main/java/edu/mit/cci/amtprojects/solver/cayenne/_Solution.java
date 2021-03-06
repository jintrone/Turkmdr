package edu.mit.cci.amtprojects.solver.cayenne;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import edu.mit.cci.amtprojects.solver.Question;
import edu.mit.cci.amtprojects.solver.Solution;
import edu.mit.cci.amtprojects.solver.SolutionRank;

/**
 * Class _Solution was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Solution extends CayenneDataObject {

    public static final String ASSIGNMENT_ID_PROPERTY = "assignmentId";
    public static final String CREATION_PROPERTY = "creation";
    public static final String ID_PROPERTY = "id";
    public static final String META_PROPERTY = "meta";
    public static final String ROUND_PROPERTY = "round";
    public static final String TEXT_PROPERTY = "text";
    public static final String VALID_PROPERTY = "valid";
    public static final String WORKER_ID_PROPERTY = "workerId";
    public static final String TO_CHILDREN_PROPERTY = "toChildren";
    public static final String TO_PARENTS_PROPERTY = "toParents";
    public static final String TO_QUESTION_PROPERTY = "toQuestion";
    public static final String TO_RANKS_PROPERTY = "toRanks";

    public static final String ID_PK_COLUMN = "id";

    public void setAssignmentId(String assignmentId) {
        writeProperty("assignmentId", assignmentId);
    }
    public String getAssignmentId() {
        return (String)readProperty("assignmentId");
    }

    public void setCreation(Date creation) {
        writeProperty("creation", creation);
    }
    public Date getCreation() {
        return (Date)readProperty("creation");
    }

    public void setId(Long id) {
        writeProperty("id", id);
    }
    public Long getId() {
        return (Long)readProperty("id");
    }

    public void setMeta(String meta) {
        writeProperty("meta", meta);
    }
    public String getMeta() {
        return (String)readProperty("meta");
    }

    public void setRound(Integer round) {
        writeProperty("round", round);
    }
    public Integer getRound() {
        return (Integer)readProperty("round");
    }

    public void setText(String text) {
        writeProperty("text", text);
    }
    public String getText() {
        return (String)readProperty("text");
    }

    public void setValid(String valid) {
        writeProperty("valid", valid);
    }
    public String getValid() {
        return (String)readProperty("valid");
    }

    public void setWorkerId(String workerId) {
        writeProperty("workerId", workerId);
    }
    public String getWorkerId() {
        return (String)readProperty("workerId");
    }

    public void addToToChildren(Solution obj) {
        addToManyTarget("toChildren", obj, true);
    }
    public void removeFromToChildren(Solution obj) {
        removeToManyTarget("toChildren", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Solution> getToChildren() {
        return (List<Solution>)readProperty("toChildren");
    }


    public void addToToParents(Solution obj) {
        addToManyTarget("toParents", obj, true);
    }
    public void removeFromToParents(Solution obj) {
        removeToManyTarget("toParents", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Solution> getToParents() {
        return (List<Solution>)readProperty("toParents");
    }


    public void setToQuestion(Question toQuestion) {
        setToOneTarget("toQuestion", toQuestion, true);
    }

    public Question getToQuestion() {
        return (Question)readProperty("toQuestion");
    }


    public void addToToRanks(SolutionRank obj) {
        addToManyTarget("toRanks", obj, true);
    }
    public void removeFromToRanks(SolutionRank obj) {
        removeToManyTarget("toRanks", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<SolutionRank> getToRanks() {
        return (List<SolutionRank>)readProperty("toRanks");
    }


}
