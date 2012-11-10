package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import org.apache.wicket.ajax.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * User: jintrone
 * Date: 10/16/12
 * Time: 11:25 PM
 */
public interface HitCreator {

    public void launch(UrlCreator creator, Object model, Batch b) throws MalformedURLException, UnsupportedEncodingException, JSONException;
}
