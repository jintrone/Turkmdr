package edu.mit.cci.amtprojects.kickball.cayenne;

import edu.mit.cci.amtprojects.kickball.cayenne.auto._KickballDataMap;

public class KickballDataMap extends _KickballDataMap {

    private static KickballDataMap instance;

    private KickballDataMap() {}

    public static KickballDataMap getInstance() {
        if(instance == null) {
            instance = new KickballDataMap();
        }

        return instance;
    }
}
