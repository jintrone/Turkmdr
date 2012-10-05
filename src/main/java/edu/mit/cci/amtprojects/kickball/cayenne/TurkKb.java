package edu.mit.cci.amtprojects.kickball.cayenne;



public class TurkKb extends _TurkKb {

    private static TurkKb instance;

    private TurkKb() {}

    public static TurkKb getInstance() {
        if(instance == null) {
            instance = new TurkKb();
        }

        return instance;
    }
}
