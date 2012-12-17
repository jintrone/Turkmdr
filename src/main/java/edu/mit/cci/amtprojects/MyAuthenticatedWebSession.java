package edu.mit.cci.amtprojects;

import edu.mit.cci.amtprojects.kickball.cayenne.User;
import edu.mit.cci.amtprojects.kickball.cayenne.Users;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

/**
 * User: jintrone
 * Date: 11/14/12
 * Time: 11:04 AM
 */
public class MyAuthenticatedWebSession extends AuthenticatedWebSession
{



    Users user;


    /**
     * Construct.
     *
     * @param request
     *            The current request object
     */
    public MyAuthenticatedWebSession(Request request)
    {
        super(request);
    }

    /**
     * @see org.apache.wicket.authroles.authentication.AuthenticatedWebSession#authenticate(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean authenticate(final String username, final String password)
    {
        Users u  = CayenneUtils.findUser(DbProvider.getContext(),username,password);
        user = u;
        return u!=null;
    }

    public Users getUser() {
        return user;
    }

    /**
     * @see org.apache.wicket.authroles.authentication.AuthenticatedWebSession#getRoles()
     */
    @Override
    public Roles getRoles()
    {
        if (isSignedIn())
        {
            // If the user is signed in, they have these roles
            return new Roles(Roles.ADMIN);
        }
        return null;
    }
}
