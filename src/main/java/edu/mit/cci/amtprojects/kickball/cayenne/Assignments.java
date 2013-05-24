package edu.mit.cci.amtprojects.kickball.cayenne;


import com.amazonaws.mturk.requester.AssignmentStatus;

public class Assignments extends _Assignments {

    public static enum Status {
        PENDING, RESULTS, CANCELED, APPROVED, REJECTED;

        public static Status lookup(AssignmentStatus status) {
            if (status.equals(AssignmentStatus.Approved)) {
                return APPROVED;
            } else if (status.equals(AssignmentStatus.Rejected)) {
                return REJECTED;
            } else if (status.equals(AssignmentStatus.Submitted)) {
                return RESULTS;
            } else return null;
        }
    }

    public Status getStatusEnum() {
        return getStatus()==null?null:Status.valueOf(getStatus());
    }

}
