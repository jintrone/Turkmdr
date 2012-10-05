package edu.mit.cci.amtprojects.util;



import org.apache.log4j.Logger;

import javax.naming.OperationNotSupportedException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 5:34 AM
 */
public class IndexedIterator<T> implements Iterator<T> {

    private int start;
    private int count;
    private int current;

    private List<T> data;
    private static Logger log = Logger.getLogger(IndexedIterator.class);

    public IndexedIterator(List<T> data, int start, int count) {
        this.start =start;
        this.count = count;
        this.current = start;
        this.data = data;

    }



    public boolean hasNext() {
        return data.size()>current && current - start < count;
    }

    public T next() {

        return data.get(current++);
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported on "+this.getClass());
    }
}
