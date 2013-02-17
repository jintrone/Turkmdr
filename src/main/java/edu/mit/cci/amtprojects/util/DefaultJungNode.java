package edu.mit.cci.amtprojects.util;

import org.apache.commons.collections15.Factory;

import java.util.HashMap;
import java.util.Map;

/**
* User: jintrone
* Date: 4/20/11
* Time: 2:25 PM
*/
public class DefaultJungNode {

    private static int idgen = 0;

    private final Integer id;

    private Map<String,Object> attributes = new HashMap<String,Object>();


    private String label;

    public DefaultJungNode() {
        this.id = idgen++;
    }

     public DefaultJungNode(String label) {
        this();
         setLabel(label);

    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }

    public static Factory<DefaultJungNode> getFactory() {
        return new Factory<DefaultJungNode>() {

            public DefaultJungNode create() {
                return new DefaultJungNode();
            }
        };
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

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public String toString() {
        return "Node("+getId()+"):"+getLabel();
    }

    public int hashCode() {
        return id.hashCode()*7+13;
    }

    public boolean equals(Object o) {
        return o instanceof DefaultJungNode && ((DefaultJungNode)o).getId() == getId();
    }



}
