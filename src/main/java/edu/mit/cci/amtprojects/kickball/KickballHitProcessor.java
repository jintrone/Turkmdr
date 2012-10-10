package edu.mit.cci.amtprojects.kickball;

import com.amazonaws.mturk.requester.HIT;
import edu.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.HitDataProvider;
import edu.mit.cci.amtprojects.HomePage;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import org.apache.sling.commons.json.JSONException;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/9/12
 * Time: 1:37 PM
 */
public class KickballHitProcessor extends WebPage {

    private Batch batch;

    PagingNavigator pagingNavigator;

    public KickballHitProcessor(PageParameters parameters) {

        if (parameters.get("batch").isEmpty()) {
            parameters.set("error", "Batch not valid");
            throw new RestartResponseException(HomePage.class, parameters);
        } else {
            long id = parameters.get("batch").toLong();
            batch = CayenneUtils.findBatch(DbProvider.getContext(), id);
            if (batch == null) {
                parameters.set("error", "Batch not valid");
                throw new RestartResponseException(HomePage.class, parameters);
            }

        }


        HitManager manager = HitManager.get(batch);


        add(new Label("experimentId", batch.getToExperiment().getExperimentId() + ""));
        add(new Label("experimentName", batch.getToExperiment().getName() + ""));
        add(new Label("batchName", batch.getName()));
        add(new Label("batchMode", batch.getIsReal() ? "Real" : "Sandbox"));


        PostReplyDataProvider postresults = null;
        try {
            postresults = new PostReplyDataProvider(batch);
        } catch (JSONException e) {
            parameters.set("error", "Error parsing log result");
                throw new RestartResponseException(HomePage.class, parameters);
        }

        final DataView<Set<PostReplyDataProvider.PostReplyResult>> dataView = new DataView<Set<PostReplyDataProvider.PostReplyResult>>("resultslist", postresults) {
            private static final long serialVersionUID = 1L;

            private String[] labels = {"assn1","assn2","assn3"};
            private String[] classes = {"noanswer","complete","partial","none"};

            @Override
            protected void populateItem(final Item<Set<PostReplyDataProvider.PostReplyResult>> item) {
                List<PostReplyDataProvider.PostReplyResult> result = new ArrayList<PostReplyDataProvider.PostReplyResult>(item.getModelObject());
                String postid = String.valueOf(result.get(0).postId);

                item.add(new Label("postId", postid));


                Set<String> values = new HashSet<String>();
                for (int i = 0;i<labels.length;i++) {

                    String val = result.size() <= i || result.get(i).assignmentId == null?"---":(result.get(i).repondsToId+"");
                    if (!("---".equals(val))) values.add(val);
                    item.add(new Label(labels[i],val));
                }
                final int agreement = values.size();

                item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return classes[agreement];
                    }
                }));


            }
        };

        add(dataView);


    }
}
