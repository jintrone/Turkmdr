package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.BatchManager;
import edu.mit.cci.amtprojects.HitCreator;
import edu.mit.cci.amtprojects.InnerFormCallback;
import edu.mit.cci.amtprojects.PluginFactory;
import org.apache.wicket.markup.html.panel.Panel;

import java.text.Normalizer;

/**
 * User: jintrone
 * Date: 10/16/12
 * Time: 11:12 PM
 */
public class KickballPluginFactory implements PluginFactory {


    public Panel getFormPanel(String id,InnerFormCallback callback) {
        return new KickballHitFormPanel(id,callback);
    }

    public HitCreator getHitCreator() {
        return KickballHitCreator.getInstance();
    }

    public BatchManager getBatchManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
