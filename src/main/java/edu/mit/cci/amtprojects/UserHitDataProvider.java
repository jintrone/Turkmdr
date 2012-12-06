package edu.mit.cci.amtprojects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.amazonaws.mturk.requester.HIT;
import com.amazonaws.mturk.service.axis.RequesterService;

import edu.mit.cci.amtprojects.util.IndexedIterator;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 5:07 AM
 */
public class UserHitDataProvider implements IDataProvider<HIT> {


	RequesterService requesterService;
	
    public UserHitDataProvider(RequesterService r) {
    	requesterService = r;
    }

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Iterator<? extends HIT> iterator(long i, long count) {
        List<HIT> hits = new ArrayList<HIT>(Arrays.asList(requesterService.searchAllHITs()));
        return new IndexedIterator<HIT>(hits,i,count);
    }

    public long size() {
        return requesterService.searchAllHITs().length;
    }

	public IModel<HIT> model(HIT hit) {
		return new HITModel2(hit);
	}

}
