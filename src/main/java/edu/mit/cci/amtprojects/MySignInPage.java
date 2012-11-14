package edu.mit.cci.amtprojects;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * User: jintrone
 * Date: 11/14/12
 * Time: 10:58 AM
 */
public class MySignInPage extends WebPage {


    public MySignInPage() {
        this(null);
    }

    public MySignInPage(final PageParameters params) {
        add(new SignInPanel("signInPanel"));
    }


}
