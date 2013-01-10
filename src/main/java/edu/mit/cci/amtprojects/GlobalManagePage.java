package edu.mit.cci.amtprojects;

import com.amazonaws.mturk.requester.HIT;
import edu.mit.cci.amtprojects.kickball.cayenne.AwsCredentials;
import org.apache.cayenne.exp.Expression;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@AuthorizeInstantiation("ADMIN")
public class GlobalManagePage extends WebPage {
    private static final long serialVersionUID = 1L;

    private static int itemsPerPage = 50;
    private PagingNavigator pagingNavigator;
    private static Logger logger = Logger.getLogger(GlobalManagePage.class);
    private FormModel formModel;

    public List<AwsCredentials> getAwsCredentials() {
        return ((MyAuthenticatedWebSession) getSession()).getUser().getToAwsCredentials();
    }


    public GlobalManagePage(final PageParameters parameters) {

        //AWS Credential selector and real/sandbox hits selector
        List<AwsCredentials> awsCredentials = getAwsCredentials();
        List<String> awsIds = new ArrayList<String>();
        formModel = new FormModel();
        for (AwsCredentials cred : awsCredentials) {
            awsIds.add(cred.getAwsId());
        }
        formModel.setSelectAwsCredentialsDropdown(awsIds.get(0));
        formModel.setSelectRealOrSandboxHitsDropdown("sandbox");
        List<String> yesOrNo = Arrays.asList(new String[]{"sandbox", "real"});

        Form<FormModel> selectAwsCredentialsForm = new Form<FormModel>("selectAwsCredentialsForm", new CompoundPropertyModel<FormModel>(formModel)) {
            @Override
            public void onSubmit() {
                super.onSubmit();
                //TODO: why isn't this updating the form?
            }
        };
        add(selectAwsCredentialsForm);
        selectAwsCredentialsForm.add(new DropDownChoice("selectAwsCredentialsDropdown", awsIds));
        selectAwsCredentialsForm.add(new DropDownChoice("selectRealOrSandboxHitsDropdown", yesOrNo));


        //List of experiments
        final HashSet<HIT> selectedValues = new HashSet<HIT>();

        final CheckGroup<SelectItemUsingCheckboxModel> checkgroup = new CheckGroup("checkgroup", new ArrayList<SelectItemUsingCheckboxModel>());

        final Form form = new Form("form") {
            @Override
            public void onSubmit() {
                
                super.onSubmit();
                for (HIT h: selectedValues) { 
                    System.out.println(h); 
                    //TODO: delete rows here 
                    
                }

            }
        };

        checkgroup.add(new CheckGroupSelector("checkboxSelectAll"));

        UserHitDataProvider userHitDataProvider = new UserHitDataProvider(formModel);

        final DataView<HIT> dataView = new DataView<HIT>("pageable", userHitDataProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<HIT> item) {
                HIT hit = item.getModelObject();


                item.add(new CheckBox("checkbox", new SelectItemUsingCheckboxModel(hit, selectedValues)));
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

    public static class FormModel implements Serializable {

        public String getSelectAwsCredentialsDropdown() {
            return selectAwsCredentialsDropdown;
        }

        public void setSelectAwsCredentialsDropdown(String selectAwsCredentialsDropdown) {
            this.selectAwsCredentialsDropdown = selectAwsCredentialsDropdown;
        }

        public String getSelectRealOrSandboxHitsDropdown() {
            return selectRealOrSandboxHitsDropdown;
        }

        public void setSelectRealOrSandboxHitsDropdown(String selectRealOrSandboxHitsDropdown) {
            this.selectRealOrSandboxHitsDropdown = selectRealOrSandboxHitsDropdown;
        }

        public String selectAwsCredentialsDropdown = "Select one";
        public String selectRealOrSandboxHitsDropdown = "Select one";

        public String lookupAwsSecret() {
            SelectQuery query = new SelectQuery(AwsCredentials.class);
            query.andQualifier(Expression.fromString("awsId='" + selectAwsCredentialsDropdown + "'"));
            List<AwsCredentials> results = DbProvider.getContext().performQuery(query);
            if (results == null || results.isEmpty()) {
                return null;
            } else {
                return results.get(0).getAwsSecret();
            }
        }
        public boolean isReal() {
            return "real".equals(selectRealOrSandboxHitsDropdown);
        }


    }

}
