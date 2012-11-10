package edu.mit.cci.amtprojects.kickball.cayenne;


import edu.mit.cci.amtprojects.util.CayenneUtils;

public class Batch extends _Batch {

    public Long getId() {
        return CayenneUtils.extractObjectId(this);
    }



}
