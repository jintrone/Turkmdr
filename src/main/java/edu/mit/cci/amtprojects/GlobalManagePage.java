package edu.mit.cci.amtprojects;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.amazonaws.mturk.requester.HIT;
import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.util.ClientConfig;

import edu.mit.cci.amtprojects.util.FilePropertiesConfig;

@AuthorizeInstantiation("ADMIN")
public class GlobalManagePage extends WebPage {
	private static final long serialVersionUID = 1L;

    private static int itemsPerPage = 50;
    private PagingNavigator pagingNavigator;
    private static Logger logger = Logger.getLogger(GlobalManagePage.class);

    
    public GlobalManagePage(final PageParameters parameters) {
    	
    	final CheckGroup checkgroup = new CheckGroup("checkgroup");
    	
    	final Form<?> form = new Form<Void>("form"){
			@Override
			public void onSubmit() {

				//logger.error("data: " + checkgroup);
 
			}
		};
		
        checkgroup.add(new CheckGroupSelector("checkboxSelectAll"));
        
        UserHitDataProvider userHitDataProvider = new UserHitDataProvider(getRequester(false));
    	final DataView<HIT> dataView = new DataView<HIT>("pageable", userHitDataProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<HIT> item) {
                HIT hit = item.getModelObject();
                
                item.add(new CheckBox("checkbox", Model.of(Boolean.FALSE)));
                //item.add(new Check("checkbox", item.getModel())); //why is item.getModel() null?
                
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
    	
    	//TODO: pull these from file or form
        try {
            config = new FilePropertiesConfig(getClass().getResourceAsStream("/global.mturk.properties"));
        } catch (IOException e) {
            logger.error("Could not read global properties file: global.mturk.properties");
            config = new ClientConfig();
        }

        config.setAccessKeyId("AKIAIU4LXH47T5FEBPBQ"); 
        config.setSecretAccessKey("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"); 

        //TODO: two pages, for real or sandbox hits
        if (isReal) {
            config.setServiceURL(ClientConfig.PRODUCTION_SERVICE_URL);
        } else {
            config.setServiceURL(ClientConfig.SANDBOX_SERVICE_URL);
        }
        
        return new RequesterService(config);
    }
}
