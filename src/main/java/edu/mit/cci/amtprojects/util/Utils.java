package edu.mit.cci.amtprojects.util;

import edu.mit.cci.amtprojects.UrlCreator;
import edu.mit.cci.amtprojects.solver.SolutionRank;
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

    public static String extractJsonProperty(String value, String key) {
        try {
            JSONObject obj = new JSONObject(value);
            return obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;

    }

    public static String join(List initialAnswers, String s) {
        StringBuilder result = new StringBuilder();
        String sep = "";
        for (Object a:initialAnswers) {
            result.append(sep).append(a.toString());
            sep = s;
        }
        return result.toString();

    }

    public static<T> T last(List<T> list) {
        return list.get(list.size()-1);
    }
}
