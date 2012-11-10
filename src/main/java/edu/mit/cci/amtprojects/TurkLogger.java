package edu.mit.cci.amtprojects;

/**
 * User: jintrone
 * Date: 11/9/12
 * Time: 10:05 AM
 */
public interface TurkLogger {

    public void logEvent(String type, Object... params);
}
