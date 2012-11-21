package edu.mit.cci.amtprojects;

import com.amazonaws.mturk.requester.HIT;
import edu.mit.cci.amtprojects.kickball.cayenne.Hits;
import org.apache.wicket.model.IModel;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 10:39 PM
 */
public class HITModel implements IModel<edu.mit.cci.amtprojects.kickball.cayenne.Hits> {
    private Hits hit;

    public HITModel(Hits hit) {
        this.hit = hit;
    }

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Hits getObject() {
       return hit;
    }

    public void setObject(Hits hit) {
        this.hit = hit;
    }
}
