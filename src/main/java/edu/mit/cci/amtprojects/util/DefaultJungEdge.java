package edu.mit.cci.amtprojects.util;

import org.apache.commons.collections15.Factory;

import java.util.HashMap;
import java.util.Map;

/**
* User: jintrone
* Date: 4/20/11
* Time: 2:26 PM
*/
public class DefaultJungEdge {

    private static int idgen = 0;

    private final int id;

    private float weight = 0;

    private Map<String,Object> attributes = new HashMap<String,Object>();


    public DefaultJungEdge() {
        this.id = idgen++;
    }

     public DefaultJungEdge(float weight) {
        this.id = idgen++;
         this.setWeight(weight);
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public int getId() {
        return id;
    }


    public void setAttribute(String key, Object val) {
        attributes.put(key,val);
    }

    public Map<String,Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String key) {
       return attributes.get(key);
    }

    public static Factory<DefaultJungEdge> getFactory() {
        return new Factory<DefaultJungEdge>() {

            public DefaultJungEdge create() {
                return new DefaultJungEdge();
            }
        };
    }

    public String toString() {
        return "Edge("+getId()+"):"+getWeight();
    }

}
