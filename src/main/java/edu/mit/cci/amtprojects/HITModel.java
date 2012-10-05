package edu.mit.cci.amtprojects;

import com.amazonaws.mturk.requester.HIT;
import org.apache.wicket.model.IModel;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 10:39 PM
 */
public class HITModel implements IModel<HIT> {
    private HIT hit;

    public HITModel(HIT hit) {
        this.hit = hit;
    }

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public HIT getObject() {
       return hit;
    }

    public void setObject(HIT hit) {
        this.hit = hit;
    }
}
