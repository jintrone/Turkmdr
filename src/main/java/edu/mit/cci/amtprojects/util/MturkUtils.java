package edu.mit.cci.amtprojects.util;

import com.amazonaws.mturk.addon.HITQuestionHelper;
import com.amazonaws.mturk.requester.HIT;
import edu.cci.amtprojects.DefaultEnabledHitProperties;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 4:22 PM
 */
public class MturkUtils {

    private static HITQuestionHelper helper = new HITQuestionHelper();

    public static String getExternalQuestion(String url,int frameHeight) {
        String question = String.format("<ExternalQuestion xmlns=\"http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2006-07-14/ExternalQuestion.xsd\">" +
                "<ExternalURL>%s</ExternalURL>" +
                "<FrameHeight>%d</FrameHeight>" +
                "</ExternalQuestion>", helper.urlencode(url),frameHeight);
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

    public static String addUrlParams(String url, String... params) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (int i = 0;i<params.length/2 * 2;i+=2) {
            pairs.add(new BasicNameValuePair(params[i],params[i+1]));
        }
        return url+ "?"+URLEncodedUtils.format(pairs,"utf-8");
    }
}
