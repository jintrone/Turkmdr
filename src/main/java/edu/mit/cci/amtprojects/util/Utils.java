package edu.mit.cci.amtprojects.util;

import edu.mit.cci.amtprojects.UrlCreator;
import edu.mit.cci.amtprojects.solver.SolutionRank;
import org.apache.log4j.Logger;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.wicket.Component;

import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 10/5/12
 * Time: 9:49 AM
 */
public class Utils {
    private static Logger log = Logger.getLogger(Utils.class);

    public static UrlCreator getUrlCreator(final Component comp) {
        return new UrlCreator() {
            public String getUrlFor(Class c) {
                String relPath = comp.urlFor(c, null).toString();
                HttpServletRequest req = (HttpServletRequest) ((WebRequest) RequestCycle.get().getRequest()).getContainerRequest();
                return RequestUtils.toAbsolutePath(req.getRequestURL().toString(), relPath);
            }
        };
    }

    public static Map<String, Object> mapify(Object... s) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < s.length / 2 * 2; i += 2) {
            result.put(s[i].toString(), s[i + 1]);

        }
        return result;
    }

    public static String getJsonString(String value, String key) {
       return getJsonString(value,key,null);

    }

    public static String getJsonString(String value, String key, String def) {
        try {
            JSONObject obj = new JSONObject(value);
            return obj.getString(key);
        } catch (JSONException e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            log.warn(e.getMessage());
        }
        return def;

    }

    public static String[] getJsonArray(String value, String key, String[] def) {
            try {
                JSONObject obj = new JSONObject(value);
                JSONArray array =  obj.getJSONArray(key);
                String[] result = new String[array.length()];
                for (int i=0;i<array.length();i++) {
                    result[i] = array.getString(i);
                }
                return result;
            } catch (JSONException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                log.warn(e.getMessage());
            }
            return def;

        }

    public static String join(List initialAnswers, String s) {
        StringBuilder result = new StringBuilder();
        String sep = "";
        for (Object a:initialAnswers) {
            if (a == null || a.toString().isEmpty()) continue;
            result.append(sep).append(a.toString());
            sep = s;
        }
        return result.toString();

    }

    public static<T> T last(List<T> list) {
        return list.get(list.size()-1);
    }

    public static String updateJSONProperty(String data, String property, Object s) {
        try {
            JSONObject obj = new JSONObject(data);
            obj.put(property,s);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            log.error("Could not update property "+property+" in data "+data);
        }
        return data;
    }

    public static Integer getJsonInt(String data, String property) {
        try {
            JSONObject obj = new JSONObject(data);
            if (obj.has(property)) {
                return obj.getInt(property);
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;


    }
}
