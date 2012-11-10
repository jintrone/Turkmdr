package edu.mit.cci.amtprojects;

import java.io.Serializable;

/**
 * User: jintrone
 * Date: 10/16/12
 * Time: 11:28 PM
 */
public interface InnerFormCallback {

    public void setData(Object o);



public static class Basic implements  InnerFormCallback, Serializable{

    Object[] data;

    public Basic(Object[] data) {
      this.data = data;
    }

    public void setData(Object o) {
        data[0] = o;
    }
}

}
