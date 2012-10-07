package edu.mit.cci.amtprojects.kickball;


import org.apache.wicket.util.io.IClusterable;

/**
* User: jintrone
* Date: 10/4/12
* Time: 11:57 PM
*/
public class KickballHitModel implements IClusterable {

    private static final long serialVersionUID = 1L;


    public int getAssignmentsPerHit() {
        return assignmentsPerHit;
    }

    public void setAssignmentsPerHit(int assignmentsPerHit) {
        this.assignmentsPerHit = assignmentsPerHit;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    private long threadId;
    private int assignmentsPerHit;


}
