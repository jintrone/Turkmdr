package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.solver.SolverApprovalAdmin;
import edu.mit.cci.amtprojects.solver.SolverValidationTask;
import org.apache.cayenne.access.DataContext;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;

import edu.mit.cci.amtprojects.kickball.KickballHitProcessor;
import edu.mit.cci.amtprojects.kickball.KickballPostTask;
import edu.mit.cci.amtprojects.solver.SolverGenerationTask;
import edu.mit.cci.amtprojects.solver.SolverRankingTask;


/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see edu.mit.cci.amtprojects.Start#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication
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
        getDebugSettings().setComponentUseCheck(false);

        getRootRequestMapperAsCompound().add(new MountedMapper("/manage/${experiment}", Batches.class));


        mountPage("/manage/${experiment}/${batch}", HitsPage.class);
	    mountPage("/manage/kickball/${batch}", KickballHitProcessor.class);

	     mountPage("/task/kickball",KickballPostTask.class);

        mountPage("/task/solver/generate", SolverGenerationTask.class);
        mountPage("/task/solver/rank", SolverRankingTask.class);
        mountPage("/task/solver/validate", SolverValidationTask.class);
        mountPage("/manage/solver/${batch}", SolverApprovalAdmin.class);

        mountPage("/signin",MySignInPage.class);
        
        mountPage("/manage/all", GlobalManagePage.class);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return MyAuthenticatedWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return MySignInPage.class;
    }
}
