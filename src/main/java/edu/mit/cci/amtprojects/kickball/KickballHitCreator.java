package edu.mit.cci.amtprojects.kickball;

import com.amazonaws.mturk.requester.QualificationType;
import edu.mit.cci.amtprojects.DefaultEnabledHitProperties;
import edu.mit.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.HitCreator;
import edu.mit.cci.amtprojects.UrlCreator;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import edu.mit.cci.amtprojects.util.MturkUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/4/12
 * Time: 11:17 PM
 */
public class KickballHitCreator implements HitCreator {

    private static KickballHitCreator instance;

    private static Logger log = Logger.getLogger(KickballHitCreator.class);

    public static KickballHitCreator getInstance() {
        if (instance == null) {
            instance = new KickballHitCreator();
        }
        return instance;
    }





    public void launch(UrlCreator creator, Object m, Batch b) throws MalformedURLException, UnsupportedEncodingException, JSONException {

        KickballTaskModel model = (KickballTaskModel)m;
        String url = creator.getUrlFor(KickballPostTask.class);

        if (model!=null && model.getBatch() == null) {
            model.saveToBatch(b);
        }

        //build url

        //create hit properties
        DefaultEnabledHitProperties props = new DefaultEnabledHitProperties();
        props.setTitle("Indicate the reply structure of a message forum");
        props.setDescription("Establish the reply sequence in a series of message forum posts. Each HIT requires you to identify which post another post is a response to");
        props.setMaxAssignments("" + model.getAssignmentsPerHit());
        props.setRewardAmount(""+model.getTaskReward());
        MturkUtils.addBatchAnnotation(props, b);

        //launch hits

        //not actually launching
//        SelectQuery query = new SelectQuery(Post.class);
//        query.addOrdering("created", SortOrder.DESCENDING);
//        query.andQualifier(Expression.fromString("threadid = " + model.getThreadId()));
//        List<Post> posts = DbProvider.getContext().performQuery(query);
//        log.info("Would launch: "+posts.size()+" hits");
//        for (Post p:posts) {
//            String launchurl = MturkUtils.addUrlParams(url,"focus",""+p.getPostid(),"batch",""+b.getId());
//            log.info("Would launch at " + launchurl);
//            HitManager.get(b).launch(launchurl, 800, props);
//
//        }


        HitManager manager = HitManager.get(b);
        QualificationType type= manager.findQualificationNamed(model.getQualificationName());
        if (type == null) {
            manager.createAssignableQualificationType(model.getQualificationName(),"forum,conversation,MIT","Indicates that you are good at understanding the reply structure of an online conversation");

        }

        DefaultEnabledHitProperties qprops = new DefaultEnabledHitProperties();
        props.setTitle("Earn qualification '"+model.getQualificationName()+"'");
        props.setDescription("Earn qualification '"+model.getQualificationName()+"'. HIT requires you to read through posts in a web forum to establish a reply sequence.  Qualification and $.20 bonus only granted for performing well enough.");

        props.setMaxAssignments("" + model.getNumberOfWorkersToQualify());
        props.setRewardAmount(""+model.getQualifierReward());
        String qualifierUrl = MturkUtils.addUrlParams(url,"qualifier","true");
        HitManager.get(b).launch(qualifierUrl,1000,props);


    }

}
