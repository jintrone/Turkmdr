package edu.mit.cci.amtprojects.util;

import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 10/5/12
 * Time: 9:49 AM
 */
public class Utils {

    public static Map<String,Object> mapify(Object... s) {
        Map<String,Object> result = new HashMap<String,Object>();
        for (int i=0;i<s.length/2 * 2;i+=2) {
            result.put(s[i].toString(),s[i+1]);

        }
        return result;
    }
}
