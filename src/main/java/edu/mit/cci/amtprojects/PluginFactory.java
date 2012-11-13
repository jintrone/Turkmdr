package edu.mit.cci.amtprojects;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: jintrone
 * Date: 10/16/12
 * Time: 11:12 PM
 */
public interface PluginFactory {

    public Panel getFormPanel(String id, InnerFormCallback callback);
    public HitCreator getHitCreator();

    BatchManager getBatchManager();
}
