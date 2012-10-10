package edu.mit.cci.amtprojects.kickball;

import edu.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.IndexedIterator;
import edu.mit.cci.amtprojects.util.MturkUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 10/9/12
 * Time: 3:50 PM
 */
public class PostReplyDataProvider implements IDataProvider<Set<PostReplyDataProvider.PostReplyResult>>{

    long batchid;
    long threadid;
    List<Set<PostReplyResult>> results;
    private static Logger log = Logger.getLogger(PostDataProvider.class);

    public PostReplyDataProvider(Batch b) throws JSONException {
        this.batchid = b.getId();
        JSONObject obj = new JSONObject(b.getParameters());
        threadid = Long.parseLong(""+obj.get("threadid"));
        log.info("Got thread id of "+threadid);
    }


    public void detach() {
        results = null;
    }

    public Iterator<? extends Set<PostReplyResult>> iterator(long l, long l1) {
        if (results == null) {
            populate();
        }
        return new IndexedIterator<Set<PostReplyResult>>(results,l,l1);
    }



    private void populate() {
        HitManager manager = HitManager.get(batch());
        Map<Integer,Set<PostReplyResult>> resultmap = new HashMap<Integer, Set<PostReplyResult>>();
        for (TurkerLog log:manager.getFilteredLogs("RESULTS")) {
            String answer = "";
            try {
                JSONObject obj = new JSONObject(log.getData());
                answer = ""+obj.get("answer");
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return;
            }
            Integer postid = Integer.parseInt(MturkUtils.extractAnswer("post", answer));
            Integer respondsto = Integer.parseInt(MturkUtils.extractAnswer("respondstoid", answer));
            Set<PostReplyResult> results = resultmap.get(postid);
            if (results == null) {
                resultmap.put(postid,results = new HashSet<PostReplyResult>());
            }
            results.add(new PostReplyResult(postid,log.getAssignmentId(),respondsto));
        }


        SelectQuery query = new SelectQuery(Post.class, Expression.fromString("threadid = "+threadid));
        query.addOrdering("created", SortOrder.ASCENDING);

        List<Post> posts = DbProvider.getContext().performQuery(query);
        results = new ArrayList<Set<PostReplyResult>>(posts.size());
        for (Post p:posts) {
            if (resultmap.containsKey(p.getPostid())) {
                results.add(resultmap.get(p.getPostid()));

            } else {
                results.add(Collections.singleton(new PostReplyResult(p.getPostid(),null,-1)));
            }
        }


    }

    public long size() {

        long size = CayenneUtils.count(DbProvider.getContext(),"Post", Post.class,"threadid = "+threadid);
        log.info("Got "+size+" posts");
        return size;

    }

    public IModel<Set<PostReplyResult>> model(Set<PostReplyResult> postReplyResults) {
        return new PostReplyResultModel(postReplyResults);
    }

    private Batch batch() {
        return CayenneUtils.findBatch(DbProvider.getContext(),batchid);
    }



    public static class PostReplyResult {
        public int postId;
        public int repondsToId;
        public String assignmentId;

        public PostReplyResult(int postid,String assignmentId, int respondstoid) {
            this.postId = postid;
            this.assignmentId = assignmentId;
            this.repondsToId = respondstoid;
        }
    }

    public static class PostReplyResultModel  implements IModel<Set<PostReplyResult>> {

        private Set<PostReplyResult> data;

        public PostReplyResultModel(Set<PostReplyResult> result) {
            setObject(result);
        }

        public void detach() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public Set<PostReplyResult> getObject() {
            return data;
        }

        public void setObject(Set<PostReplyResult> postReplyResult) {
            this.data = postReplyResult;

        }
    }


}
