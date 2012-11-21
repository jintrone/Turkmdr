package edu.mit.cci.amtprojects.kickball.cayenne;



public class Hits extends _Hits {

    public static enum Status {
        OPEN, HALTED, COMPLETE, RELAUNCHED, MISSING
    }

    public Status getStatusEnum() {
        if (getStatus() == null) return null;
        else return Status.valueOf(getStatus());
    }

}
