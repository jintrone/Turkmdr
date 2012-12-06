package edu.mit.cci.amtprojects;

import org.apache.wicket.model.IModel;

import com.amazonaws.mturk.requester.HIT;

/**
 * User: jintrone
 * Date: 9/26/12
 * Time: 10:39 PM
 */

//TODO: refactor HITModel to HitsModel, refactor this to HITModel 
public class HITModel2 implements IModel<HIT> {
    private HIT hit;

    public HITModel2(HIT hit) {
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
