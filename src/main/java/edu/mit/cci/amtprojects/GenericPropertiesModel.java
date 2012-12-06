package edu.mit.cci.amtprojects;


import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 12/4/12
 * Time: 10:19 AM
 */
public class GenericPropertiesModel {

    private Map<String,Object> props = new HashMap<String,Object>();
    private List<String> propsToSerialize = null;

    public GenericPropertiesModel() {

    }

    public GenericPropertiesModel(String dessicated) throws JSONException {
       rehydrate(dessicated);
    }

    public Object get(String prop) {
        return props.get(prop);
    }


    public Set<String> getProperties() {
        return new HashSet<String>(props.keySet());
    }

    public void setProperty(String prop, Object val) {
        props.put(prop,val);

    }

    public String getString(String s) {
        return props.get(s)==null?null:props.get(s).toString();
    }

     public Long getLong(String s) {
        Object o =  props.get(s)==null?null:props.get(s);
         if (o instanceof Number) {
             return (((Number) o).longValue());
         } else {
           return Long.parseLong(o.toString());
         }
    }

     public Float getFloat(String s) {
        Object o =  props.get(s)==null?null:props.get(s);
         if (o instanceof Number) {
             return (((Number) o).floatValue());
         } else {
           return Float.parseFloat(o.toString());
         }
    }

     public Integer getInt(String s) {
        Object o =  props.get(s)==null?null:props.get(s);
         if (o instanceof Number) {
             return (((Number) o).intValue());
         } else {
           return Integer.parseInt(o.toString());
         }
     }

    public<T>  List<T> getList(String prop,Class<T> cls)  {
        return (List<T>)props.get(prop);
    }

    protected void setPropsToSerialize(List<String> s) {
        this.propsToSerialize = new ArrayList<String>(s);
    }

    public String toJSONString() {
        Map<String,Object> nmap = new HashMap<String, Object>(props);
        if (propsToSerialize!=null) nmap.keySet().retainAll(propsToSerialize);
        return new JSONObject(nmap).toString();
    }

    public void rehydrate(String s) throws JSONException {
        props.clear();
        JSONObject obj = new JSONObject(s);
        for (Iterator<String> it=obj.keys();it.hasNext();) {
            String key = it.next();
            Object o = obj.get(key);
            if (o instanceof JSONArray) {
                List l = new ArrayList();
                for (int i=0;i<((JSONArray)o).length();i++) {
                    l.add(((JSONArray)o).get(i));
                }
                o = l;
            }
            props.put(key,o);

        }
    }






}
