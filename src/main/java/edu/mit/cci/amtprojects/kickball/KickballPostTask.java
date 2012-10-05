package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;


import java.text.DateFormat;

/**
 * User: jintrone
 * Date: 8/31/12
 * Time: 3:16 PM
 */
public class KickballPostTask extends WebPage {


    public int itemsPerPage = 50;

    private static Logger log = Logger.getLogger(KickballPostTask.class);

    PagingNavigator pagingNavigator;

    int thread = -1;
    int focus = -1;
    int selection = -1;
    int refocus = -1;
    String assignmentId = "NONE";
    String workerId = "NONE";


    public int getSelection() {

        return selection;
    }

    public KickballPostTask(PageParameters param) {



        StringValue focusid = param.get("focus");
        StringValue batchid = param.get("batch");
        StringValue assignmentId = param.get("assignmentId");
        StringValue workerId = param.get("workerId");

        Batch batch = CayenneUtils.findBatch(DbProvider.getContext(),batchid.toLong());


        this.workerId = workerId.toString("NONE");
        this.assignmentId = assignmentId.toString("NONE");


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

        configureItemsPerPage();


        //final int finalFocus = focus;


        Link focuslink = new Link("focus") {
            public void onClick() {
                log.info("Setting focus");
                refocus = focus;
            }

            public boolean isVisible() {
                return focus > -1;
            }
        };
        focuslink.setBody(Model.<String>of(""+focus));

        final Link selectedlink = new Link("selection") {
            public void onClick() {
                log.info("Setting focus");
                refocus = selection;
            }

            public boolean isVisible() {
              return selection > -1;
            }
        };
        selectedlink.setBody(new PropertyModel<Integer>(this, "selection"));
        selectedlink.setOutputMarkupId(true);

        final Label slabel = new Label("selectionlabel", "none") {
            public boolean isVisible() {
                return selection < 0;
            }
        };
        slabel.setOutputMarkupId(true);
        add(slabel);
        add(new Label("focuslabel",focus>-1?focus+"":"none") {
            public boolean isVisible() {
               return focus < 0;
            }
        });
        add(focuslink);
        add(selectedlink);
        add(new AjaxLink("clearselection") {


            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                selection = -1;

            }
        });

        final RadioGroup<Post> group = new RadioGroup<Post>("group", new Model<Post>());

        Form<?> form = new Form<Void>("form") {

            private boolean selection = false;

            {
                this.add(new Button("submitchoice") {
                    public void onSubmit() {
                       selection = true;
                    }
                });

                this.add(new Button("submitnochoice") {
                    public void onSubmit() {

                    }
                });


            }
            @Override
            protected void onSubmit() {
                log.info(selection?("selection group1: " + group.getModelObject().getPostid()):"No selection");

            }
        };

        form.add(new HiddenField<String>("assignmentId",new Model<String>(this.assignmentId+"")));

        form.add(new AttributeModifier("action",batch.getIsReal()?"https://www.mturk.com/mturk/externalSubmit":"http://workersandbox.mturk.com/mturk/externalSubmit"));
        form.add(new AttributeModifier("method","POST"));
          add(new Label("assignmentId", this.assignmentId).setEscapeModelStrings(false));
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
                    r.add(AttributeModifier.replace("checked","true"));
                }
                item.add(r);


                item.add(new Label("postid", String.valueOf(post.getPostid())));
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
                if (post.getPostid() == selection) item.add(AttributeModifier.append("class","replyfocus"));

                if (post.getPostid() == refocus) item.add(AttributeModifier.append("class","focus"));
            }
        };

        dataView.setItemsPerPage(itemsPerPage);
        //add(dataView);
        add(form);
        form.add(group);
        group.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                selection = group.getModelObject().getPostid();
                ajaxRequestTarget.add(slabel);
                ajaxRequestTarget.add(selectedlink);

            }
        });
        group.add(dataView);
        pagingNavigator = new PagingNavigator("navigator", dataView);
        if (focus > -1) {
            refocus = focus;

        }
        add(pagingNavigator);
        add(new Label("thread", thread + ""));


    }

    private void configureItemsPerPage() {
        if (focus> -1 && calculatePageForFocus(focus) > 0 &&calculateIndexForFocus(focus) == 0) {
            itemsPerPage++;


        }
    }

    @Override
    protected void onBeforeRender() {
        if (refocus>-1) {
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
