package edu.mit.cci.amtprojects.solver;

import edu.mit.cci.amtprojects.solver.cayenne._AmtSolutions;

public class AmtSolutions extends _AmtSolutions {

    private static AmtSolutions instance;

    private AmtSolutions() {}

    public static AmtSolutions getInstance() {
        if(instance == null) {
            instance = new AmtSolutions();
        }

        return instance;
    }
}
