package edu.mit.cci.amtprojects.kickball;

import edu.cci.amtprojects.DefaultEnabledHitProperties;
import edu.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import edu.mit.cci.amtprojects.util.MturkUtils;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.sling.commons.json.JSONObject;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * User: jintrone
 * Date: 10/4/12
 * Time: 11:17 PM
 */
public class KickballHitCreator {

    private static KickballHitCreator instance;

    private static Logger log = Logger.getLogger(KickballHitCreator.class);

    public static KickballHitCreator getInstance() {
        if (instance == null) {
            instance = new KickballHitCreator();
        }
        return instance;
    }

    private KickballHitModel model;

    public void setModel(KickballHitModel model) {
        this.model = model;
    }

    public void launch(String relativePath, Batch b) throws MalformedURLException, UnsupportedEncodingException {

        //build url
        HttpServletRequest req = (HttpServletRequest) ((WebRequest) RequestCycle.get().getRequest()).getContainerRequest();
        String url = RequestUtils.toAbsolutePath(req.getRequestURL().toString(), relativePath);

        //add parameters to batch
        MturkUtils.parameterizeBatch(b,"threadid",model.getAssignmentsPerHit(),"bonus",model.getBonus(),"reward",model.getReward());
        log.info("Set parameters "+b.getParameters());
        DbProvider.getContext().commitChanges();

        //create hit properties
        DefaultEnabledHitProperties props = new DefaultEnabledHitProperties();
        props.setTitle("Indicate the reply structure of a message forum");
        props.setDescription("Establish the reply sequence in a series of message forum posts. Each HIT requires you to identify which post another post is a response to");
        props.setMaxAssignments("" + model.getAssignmentsPerHit());
        props.setRewardAmount(""+model.getReward());
        MturkUtils.addBatchAnnotation(props, b);

        //launch hits
        SelectQuery query = new SelectQuery(Post.class);
        query.addOrdering("created", SortOrder.DESCENDING);
        query.andQualifier(Expression.fromString("threadid = " + model.getThreadId()));
        List<Post> posts = DbProvider.getContext().performQuery(query);
        log.info("Would launch: "+posts.size()+" hits");
        for (Post p:posts) {
            String launchurl = MturkUtils.addUrlParams(url,"focus",""+p.getPostid(),"batch",""+b.getId());
            log.info("Would launch at " + launchurl);
            HitManager.get(b).launch(launchurl, 800, props);
            break;
        }

    }

}
