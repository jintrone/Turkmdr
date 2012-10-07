package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;

import org.apache.wicket.util.io.IClusterable;
import sun.security.x509.Extension;

import java.util.Date;

/**
 * User: jintrone
 * Date: 10/3/12
 * Time: 11:00 AM
 */
public class ExperimentFormModel implements IClusterable {


    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void reset() {
        this.name = null;
        this.classname = null;
    }

    private String name;
    private String classname;

    public String toString() {
      return "ExperimentData: "+classname+":"+name;
    }

    public Experiment create() {

        Experiment e = DbProvider.getContext().newObject(Experiment.class);
        e.setName(name);
        e.setClassname(classname);
        reset();
        e.setCreated(new Date());
        DbProvider.getContext().commitChanges();
        return e;

    }


}
