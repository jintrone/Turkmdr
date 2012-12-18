package edu.mit.cci.amtprojects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.amazonaws.mturk.requester.HIT;
import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.util.ClientConfig;

import edu.mit.cci.amtprojects.solver.AwsCredentials;
import edu.mit.cci.amtprojects.util.FilePropertiesConfig;

@AuthorizeInstantiation("ADMIN")
public class GlobalManagePage extends WebPage {
	private static final long serialVersionUID = 1L;

    private static int itemsPerPage = 50;
    private PagingNavigator pagingNavigator;
    private static Logger logger = Logger.getLogger(GlobalManagePage.class);
    private String selectedAwsId;
    private String selectedIsReal;
    
    public List<AwsCredentials> getAwsCredentials(){
    	String currentUser = ((MyAuthenticatedWebSession)getSession()).getUser().getUsername();
    	ObjectContext context = DataContext.createDataContext();
    	Expression qualifier = ExpressionFactory.likeExp(AwsCredentials.USER_PROPERTY, currentUser);
    	SelectQuery select = new SelectQuery(AwsCredentials.class, qualifier);
    	List awsCredentialsForUser = context.performQuery(select);
    	return awsCredentialsForUser;
    }
    
    public GlobalManagePage(final PageParameters parameters) {
    	
    	//AWS Credential selector and real/sandbox hits selector
    	List<AwsCredentials> awsCredentials = (List<AwsCredentials>)getAwsCredentials();
    	List<String> awsIds = new ArrayList<String>();
    	for (AwsCredentials cred : awsCredentials){
			awsIds.add(cred.getAwsId());
		}
    	selectedAwsId = awsIds.get(0);
    	
    	List<String> yesOrNo = Arrays.asList(new String[] { "yes", "no"});
    	selectedIsReal = "no";
    	
    	Form selectAwsCredentialsForm = new Form("selectAwsCredentialsForm");
    	add(selectAwsCredentialsForm);
    	selectAwsCredentialsForm.add(new DropDownChoice("selectAwsCredentialsDropdown", new PropertyModel(this, selectedAwsId), awsIds));
    	selectAwsCredentialsForm.add(new DropDownChoice("selectRealOrSandboxHitsDropdown", new PropertyModel(this, selectedIsReal), yesOrNo));
    	    	
    	String keyId = selectedAwsId;
		String secretId = "";
		for (AwsCredentials cred : awsCredentials){
			if (cred.getAwsId().equals(selectedAwsId)) {
				secretId = cred.getAwsSecret();
			}
		}
    	//String keyId = "AKIAIU4LXH47T5FEBPBQ";
		//String secretId = "P1AAMgFxgMkfxC0j0jCI1Pqkqbb4bwQAn4fz1uQR";
    
		//-------
        final HashSet<HIT> selectedValues = new HashSet<HIT>(); 
    	
    	final CheckGroup checkgroup = new CheckGroup("checkgroup");
    	
    	final Form form = new Form("form"){
			@Override
			public void onSubmit() {
				/*
				super.onSubmit(); 
                for (HIT h: selectedValues) { 
                    System.out.println(h); 
                    //TODO: delete rows here 
                    
                }
                */
			}
		};
		
        checkgroup.add(new CheckGroupSelector("checkboxSelectAll"));
        
        UserHitDataProvider userHitDataProvider = new UserHitDataProvider(selectedIsReal, keyId, secretId);
        
    	final DataView<HIT> dataView = new DataView<HIT>("pageable", userHitDataProvider) {
    		private static final long serialVersionUID = 1L;
    		
            @Override
            protected void populateItem(final Item<HIT> item) {
                HIT hit = item.getModelObject();
                
                //item.add(new CheckBox("checkbox", Model.of(Boolean.FALSE)));
                //item.add(new CheckBox("checkbox", CheckedModel(hit.getHITId()))); //why is item.getModel() null?
                item.add(new CheckBox("checkbox", new SelectItemUsingCheckboxModel(hit,selectedValues))); 
                
                item.add(new Label("hitName", String.valueOf(hit.getTitle())));
                item.add(new Label("hitId", String.valueOf(hit.getHITId())));
                
                item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return (item.getIndex() % 2 == 1) ? "even" : "odd";
                    }
                }));
            }
        };
        
        dataView.setOutputMarkupId(true);
        dataView.setItemsPerPage(itemsPerPage);
        
        WebMarkupContainer container = new WebMarkupContainer("table");
        container.add(dataView);
        checkgroup.add(container);
        
        pagingNavigator = new PagingNavigator("navigator", dataView);
        add(pagingNavigator);
        
        form.add(checkgroup);
        add(form);
        
    }
    
    
    public RequesterService getRequester(boolean isReal){
    	ClientConfig config;
    	
        try {
            config = new FilePropertiesConfig(getClass().getResourceAsStream("/global.mturk.properties"));
        } catch (IOException e) {
            logger.error("Could not read global properties file: global.mturk.properties");
            config = new ClientConfig();
        }

        if (isReal) {
            config.setServiceURL(ClientConfig.PRODUCTION_SERVICE_URL);
        } else {
            config.setServiceURL(ClientConfig.SANDBOX_SERVICE_URL);
        }
        
        return new RequesterService(config);
    }
        
}
