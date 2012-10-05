package edu.mit.cci.amtprojects;

import org.apache.cayenne.access.DataContext;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 5:15 AM
 */
public class DbProvider {

    private static DataContext context;


    public static DataContext getContext() {
        if (context == null) {
            context = DataContext.createDataContext("KickballData");
        }
        return context;
    }

}
