package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.KickballHitProcessor;
import edu.mit.cci.amtprojects.kickball.KickballPostTask;
import org.apache.cayenne.access.DataContext;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.protocol.http.WebApplication;


/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see edu.mit.cci.amtprojects.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{    	

    DataContext context;

    /**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

        getRootRequestMapperAsCompound().add(new MountedMapper("/manage/${experiment}", Batches.class));
		mountPage("/manage/${experiment}/${batch}", Hits.class);
	    mountPage("/task/kickball",KickballPostTask.class);
        mountPage("/manage/kickball/${batch}", KickballHitProcessor.class);
    }
}
