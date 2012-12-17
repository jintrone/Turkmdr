package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.GenericTask;
import edu.mit.cci.amtprojects.HomePage;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.internal.Enclosure;
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
import java.util.ArrayList;
import java.util.List;

/**
 * User: jintrone
 * Date: 8/31/12
 * Time: 3:16 PM
 */
public class KickballPostTask extends GenericTask {


    public int itemsPerPage = 100;

    private static Logger log = Logger.getLogger(KickballPostTask.class);

    PagingNavigator pagingNavigator;

    int thread = -1;
    int focus = -1;
    int selection = -1;
    int refocus = -1;
    boolean qualifier = false;
    int tries = 0;
    int fails = 0;
    int requires = 0;
    List<Integer> posts;



    public int getSelection() {

        return selection;
    }

    public KickballPostTask(final PageParameters param) {
        super(param, false, true);

        StringValue focusid = param.get("focus");
        StringValue isQualifier = param.get("qualifier");
        KickballTaskModel model = null;

        try {
            model = new KickballTaskModel(batch());
        } catch (JSONException ex) {
            ex.printStackTrace();
            param.add("error", ex.getMessage());
            throw new RestartResponseException(HomePage.class, param);
        }
        requires = model.getTrainingItemsCount();


        if (!isQualifier.isEmpty()) qualifier = isQualifier.toBoolean(false);

        float bonus = model.getTaskBonus();

        if (isPreview()) {
            focusid = StringValue.valueOf(97456);

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


        configureItemsPerPage();




        //final int finalFocus = focus;

        add(new Label("bonusLabel", String.format("$%.2f", bonus)) {
            public boolean isVisible() {
                return !qualifier;
            }
        });




        Link focuslink = new Link("targetfocus") {
            public void onClick() {
                log.info("Setting focus");
                refocus = focus;
                logEvent("JUMP_TO_TARGET");

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
                logEvent("JUMP_TO_SELECTION", "selection", selection);

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
                return !isPreview() && selection > 0;

            }

        }.setDefaultFormProcessing(isReadyForSubmit()));

        submitcontainer.add(new Button("submitnochoice") {
            public boolean isEnabled() {
                return !isPreview() && selection == -1;
            }

        });

        submitcontainer.add(new AjaxButton("next") {

            public boolean isVisible() {
                return qualifier &&  isMoreTrainingAvailable() ;
            }

            public void onSubmit() {
                tries++;
                focus = posts.get(tries);
                configureItemsPerPage();
            }
        }.setDefaultFormProcessing(false));




        getForm().add(submitcontainer);


         final Component scorefield = new HiddenField<String>("score", new Model<String>() {
            @Override
            public String getObject() {
                return String.format("%.2f",1f - (float)fails/tries);
            }
        }).setOutputMarkupId(true);
        getForm().add(scorefield);

        final Component selectionfield = new HiddenField<String>("respondstoid", new Model<String>() {
            @Override
            public String getObject() {
                return selection + "";
            }
        }).setOutputMarkupId(true);
        getForm().add(selectionfield);


        getForm().add(new AttributeModifier("action", batch().getIsReal() ? "https://www.mturk.com/mturk/externalSubmit" : "http://workersandbox.mturk.com/mturk/externalSubmit"));
        getForm().add(new AttributeModifier("method", "POST"));
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

        getForm().add(group);

        final AjaxLink clearbutton = new AjaxLink("clearselection") {


            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                log.info("Got clear selection");
                selection = -1;

                ajaxRequestTarget.add(container);

                ajaxRequestTarget.add(group);
                ajaxRequestTarget.add(submitcontainer);
                ajaxRequestTarget.add(selectionfield);
                logEvent("CLEAR_SELECTION");

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
                logEvent("SELECTION_UPDATED","selection",selection+"");


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

    public boolean isReadyForSubmit() {
        return qualifier && tries > requires;
    }

    public boolean isMoreTrainingAvailable() {
      return (posts.size()  - tries) > 0;
    }

    private static List<Integer> getTrainingPosts(Long first, Long last) {
        PostModel pm = new PostModel(last.intValue());
        Integer threadid = pm.getObject().getThreadid();

        SelectQuery q = new SelectQuery(Post.class);
        q.andQualifier(Expression.fromString("threadid = "+threadid+" and postid >="+first+" and postid <="+last));
        q.addOrdering("created", SortOrder.DESCENDING);
        List<Post> tmp = DbProvider.getContext().performQuery(q);
        List<Integer> result = new ArrayList<Integer>();
        for (Post p:tmp) {
            result.add(p.getPostid());
        }
        return result;
    }


}
