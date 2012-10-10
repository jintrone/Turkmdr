package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;

/**
 * User: jintrone
 * Date: 8/31/12
 * Time: 3:16 PM
 */
public class KickballPostTask extends WebPage {


    public int itemsPerPage = 100;

    private static Logger log = Logger.getLogger(KickballPostTask.class);

    PagingNavigator pagingNavigator;

    int thread = -1;
    int focus = -1;
    int selection = -1;
    int refocus = -1;
    String assignmentId = "NONE";
    String workerId = "NONE";
    private float bonus = 0f;
    boolean isPreview = false;


    public int getSelection() {

        return selection;
    }

    public KickballPostTask(final PageParameters param) {


        StringValue focusid = param.get("focus");
        final StringValue batchid = param.get("batch");
        final StringValue assignmentId = param.get("assignmentId");
        StringValue workerId = param.get("workerId");

        HttpServletRequest request = (HttpServletRequest) getRequestCycle().getRequest().getContainerRequest();

        final String ipAddress = request.getRemoteAddr();


        Batch batch = CayenneUtils.findBatch(DbProvider.getContext(), batchid.toLong());
        try {
            this.bonus = (float) (new JSONObject(batch.getParameters()).getDouble("bonus"));
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            log.warn("No bonus found in batch; using default of " + bonus);
        }
        this.workerId = workerId.toString("NONE");
        this.assignmentId = assignmentId.toString("NONE");

        if (this.assignmentId.equals("ASSIGNMENT_ID_NOT_AVAILABLE")) {
            focusid = StringValue.valueOf(97456);
            isPreview = true;
        }

        if (!focusid.isNull() && !focusid.isEmpty()) {
            focus = focusid.toInt(-1);
            if (focus > -1) {
                PostModel pm = new PostModel(focus);
                Post p = pm.load();
                if (p != null) {
                    thread = p.getThreadid();
                } else {
                    focus = -1;
                }
            }
        }

        CayenneUtils.logEvent(DbProvider.getContext(),
                CayenneUtils.findBatch(DbProvider.getContext(), batchid.toLong()),
                "VIEW_PAGE",
                param.get("workerId").toString("NONE"),
                param.get("hitId").toString("NONE"),
                param.get("assignmentId").toString("NONE"),
                param.toString(), Utils.mapify("clientip", ipAddress));

        configureItemsPerPage();


        //final int finalFocus = focus;
        add(new Label("bonusLabel", String.format("$%.2f", bonus)));


        Link focuslink = new Link("targetfocus") {
            public void onClick() {
                log.info("Setting focus");
                refocus = focus;
                CayenneUtils.logEvent(DbProvider.getContext(),
                        CayenneUtils.findBatch(DbProvider.getContext(), batchid.toLong()),
                        "JUMP_TO_TARGET",
                        param.get("workerId").toString("NONE"),
                        param.get("hitId").toString("NONE"),
                        param.get("assignmentId").toString("NONE"),
                        param.toString(), Utils.mapify("clientip", ipAddress));
            }

            public boolean isEnabled() {
                return focus > -1;
            }


        };


        final WebMarkupContainer container = new WebMarkupContainer("selectionContainer");
        container.setOutputMarkupId(true);
        container.add(focuslink);
        //slabel.setOutputMarkupId(true);


        final Link selectedlink = new Link("selection") {
            public void onClick() {
                log.info("Setting focus");
                refocus = selection;
                CayenneUtils.logEvent(DbProvider.getContext(),
                        CayenneUtils.findBatch(DbProvider.getContext(), batchid.toLong()),
                        "JUMP_TO_SELECTION",
                        param.get("workerId").toString("NONE"),
                        param.get("hitId").toString("NONE"),
                        param.get("assignmentId").toString("NONE"),
                        param.toString(), Utils.mapify("clientip", ipAddress, "selection", selection));
            }

            public boolean isEnabled() {
                return selection > -1;
            }
        };
        container.add(selectedlink);


        //selectedlink.setOutputMarkupId(true);


        final RadioGroup<Post> group = new RadioGroup<Post>("radioGroup", new Model<Post>());

        final WebMarkupContainer submitcontainer = new WebMarkupContainer("submitContainer");
        submitcontainer.setOutputMarkupId(true);


        submitcontainer.add(new Button("submitchoice") {

            public boolean isEnabled() {
                return !isPreview && selection > 0;

            }


        });

        submitcontainer.add(new Button("submitnochoice") {
            public boolean isEnabled() {
                return !isPreview && selection == -1;
            }

        });

        final Form<?> form = new Form<Void>("form");
        form.add(submitcontainer);
        form.add(new HiddenField<String>("assignmentId", new Model<String>(this.assignmentId + "")));
        form.add(new HiddenField<String>("post", new Model<String>("" + focus)));

        final Component selectionfield = new HiddenField<String>("respondstoid", new Model<String>() {
            @Override
            public String getObject() {
                return selection + "";
            }
        }).setOutputMarkupId(true);
        form.add(selectionfield);


        form.add(new AttributeModifier("action", batch.getIsReal() ? "https://www.mturk.com/mturk/externalSubmit" : "http://workersandbox.mturk.com/mturk/externalSubmit"));
        form.add(new AttributeModifier("method", "POST"));
        //  add(new Label("assignmentId", this.assignmentId).setEscapeModelStrings(false));
//        add(new Label("workerId",this.workerId).setEscapeModelStrings(false));


        DataView<Post> dataView = new DataView<Post>("pageable", new PostDataProvider(thread, focus)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Post> item) {
                Post post = item.getModelObject();
                //item.add(new ActionPanel("actions", item.getModel()));
                Radio<Post> r = new Radio<Post>("radio", item.getModel(), group) {
                    public boolean isVisible() {
                        return item.getModelObject().getPostid() != focus;
                    }


                };
                if (item.getModelObject().getPostid() == selection) {
                    r.add(AttributeModifier.replace("checked", "true"));
                }
                item.add(r);



                item.add(new Label("date", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(post.getCreated())));
                item.add(new Label("username", post.getPostToUser().getUsername()));
                item.add(new Label("content", post.getContent()).setEscapeModelStrings(false));


                item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return (item.getIndex() % 2 == 1) ? "even" : "odd";
                    }
                }));

                if (post.getPostid() == focus) item.add(AttributeModifier.append("class", "targetfocus"));
                if (post.getPostid() == selection) item.add(AttributeModifier.append("class", "replyfocus"));

                if (post.getPostid() == refocus) item.add(AttributeModifier.append("class", "focus"));
            }
        };

        dataView.setItemsPerPage(itemsPerPage);
        //add(dataView);
        add(form);
        form.add(group);

        final AjaxLink clearbutton = new AjaxLink("clearselection") {


            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                log.info("Got clear selection");
                selection = -1;

                ajaxRequestTarget.add(container);

                ajaxRequestTarget.add(group);
                ajaxRequestTarget.add(submitcontainer);
                ajaxRequestTarget.add(selectionfield);
                CayenneUtils.logEvent(DbProvider.getContext(),
                        CayenneUtils.findBatch(DbProvider.getContext(), batchid.toLong()),
                        "CLEAR_SELECTION",
                        param.get("workerId").toString("NONE"),
                        param.get("hitId").toString("NONE"),
                        param.get("assignmentId").toString("NONE"),
                        param.toString(), Utils.mapify("clientip", ipAddress));

            }

            public boolean isEnabled() {
                return selection > -1;
            }
        };

        container.add(clearbutton);
        add(container);

        group.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {

                selection = group.getModelObject().getPostid();
                log.info("Got selection: " + selection);
                ajaxRequestTarget.add(container);
                ajaxRequestTarget.add(group);
                ajaxRequestTarget.add(submitcontainer);
                ajaxRequestTarget.add(selectionfield);
                CayenneUtils.logEvent(DbProvider.getContext(),
                        CayenneUtils.findBatch(DbProvider.getContext(), batchid.toLong()),
                        "SELECTION_UPDATED",
                        param.get("workerId").toString("NONE"),
                        param.get("hitId").toString("NONE"),
                        param.get("assignmentId").toString("NONE"),
                        param.toString(), Utils.mapify("selection", selection + "", "clientip", ipAddress));

            }
        });
        group.add(dataView);
        pagingNavigator = new PagingNavigator("navigator", dataView);
        if (focus > -1) {
            refocus = focus;

        }
        add(pagingNavigator);


    }




    private void configureItemsPerPage() {
        if (focus > -1 && calculatePageForFocus(focus) > 0 && calculateIndexForFocus(focus) == 0) {
            itemsPerPage++;


        }
    }

    @Override
    protected void onBeforeRender() {
        if (refocus > -1) {
            refocus();
            // refocus=-1;
        }
        super.onBeforeRender();


    }

    protected void onAfterRender() {
        refocus = -1;
        super.onAfterRender();
    }

    public void refocus() {
        if (refocus > -1) {
            pagingNavigator.getPageable().setCurrentPage(calculatePageForFocus(refocus));

        }
    }


    public int calculatePageForFocus(int focusid) {
        Post p = new PostModel(focusid).load();
        if (p != null) {
            int count = CayenneUtils.count(DbProvider.getContext(), "Post", Post.class, "postid != " + p.getPostid() +
                    " AND created <= '" + CayenneUtils.dbDateFormat(p.getCreated()) + "' AND threadid=" + p.getThreadid());

            return count / itemsPerPage;
        }
        return 0;
    }

    public int calculateIndexForFocus(int focusid) {
        Post p = new PostModel(focusid).load();
        if (p != null) {
            int count = CayenneUtils.count(DbProvider.getContext(), "Post", Post.class, "postid != " + p.getPostid() +
                    " AND created <= '" + CayenneUtils.dbDateFormat(p.getCreated()) + "' AND threadid=" + p.getThreadid());

            return count % itemsPerPage;
        }
        return 0;
    }


}
