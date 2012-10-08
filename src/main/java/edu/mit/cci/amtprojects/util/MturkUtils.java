package edu.mit.cci.amtprojects.util;

import com.amazonaws.mturk.addon.HITQuestionHelper;
import com.amazonaws.mturk.requester.HIT;
import edu.cci.amtprojects.DefaultEnabledHitProperties;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 4:22 PM
 */
public class MturkUtils {

    private static HITQuestionHelper helper = new HITQuestionHelper();

    private static Logger log = Logger.getLogger(MturkUtils.class);


    public static String getExternalQuestion(String url,int frameHeight) {
        String question = String.format("<ExternalQuestion xmlns=\"http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2006-07-14/ExternalQuestion.xsd\">" +
                "<ExternalURL>%s</ExternalURL>" +
                "<FrameHeight>%d</FrameHeight>" +
                "</ExternalQuestion>", url,frameHeight);
        log.info("Attempt to launch external hit: "+question);
        return question;
    }

    public static int parseBatchId(HIT hit) {
        if (hit.getRequesterAnnotation() == null) {
            return -1;
        }
        Pattern p = Pattern.compile("batchId=(\\d+)");
        Matcher m = p.matcher(hit.getRequesterAnnotation());
        if (!m.find()) {
            return -1;
        } else {
            return Integer.parseInt(m.group(1));
        }
   }

   public static void addBatchAnnotation(DefaultEnabledHitProperties props, Batch b) {
        String batchannotation = "batchId="+b.getId();
       String annotation = props.getAnnotation();
       if (annotation == null || annotation.isEmpty()) {
          annotation = batchannotation;
       } else {
           annotation = batchannotation+";"+annotation;
       }
       props.setAnnotation(annotation);
   }

    public static String addUrlParams(String url, String... params) throws UnsupportedEncodingException {
        //List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        StringBuilder buffer = new StringBuilder();
        String sep = "";
        for (int i = 0;i<params.length/2 * 2;i+=2) {
            buffer.append(sep).append(params[i]).append("=").append(URLEncoder.encode(params[i+1],"utf-8"));
            sep="&amp;";

        }
        return url+ "?"+ buffer.toString();

    }

    public static void parameterizeBatch(Batch b, Object... params) {
        Map<String,Object> map = Utils.mapify(params);
        if (b.getParameters() == null || b.getParameters().isEmpty()) {
            JSONObject o = new JSONObject(map);
            b.setParameters(o.toString());
        } else {
            try {
                JSONObject o = new JSONObject(b.getParameters());
                for (Map.Entry<String,Object> m:map.entrySet()) {
                    o.put(m.getKey(),m.getValue());
                }
                b.setParameters(o.toString());
            } catch (JSONException e) {
                log.warn("Unable to process batch parameters: "+b.getParameters());
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

    }
}
