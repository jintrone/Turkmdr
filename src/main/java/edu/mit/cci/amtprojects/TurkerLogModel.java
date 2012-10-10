package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import org.apache.wicket.model.IModel;

/**
 * User: jintrone
 * Date: 10/9/12
 * Time: 3:36 PM
 */
public class TurkerLogModel implements IModel<TurkerLog> {

    TurkerLog log;

    public TurkerLogModel(TurkerLog log) {
        this.log = log;
    }

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public TurkerLog getObject() {
        return log;
    }

    public void setObject(TurkerLog turkerLog) {
        this.log = turkerLog;
    }
}
