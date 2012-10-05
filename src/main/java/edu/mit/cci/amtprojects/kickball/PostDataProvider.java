package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.IndexedIterator;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 5:07 AM
 */
public class PostDataProvider implements IDataProvider<Post> {


    private int threadid;
    private int focusid;
    public PostDataProvider(int threadid,int focusid) {
       this.threadid = threadid;
        this.focusid = focusid;
   }

    public void detach() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Iterator<? extends Post> iterator(int i, int count) {
        SelectQuery query = new SelectQuery(Post.class);
        query.addOrdering("created", SortOrder.ASCENDING);
       if (threadid> -1) {
           query.andQualifier(Expression.fromString("threadid = "+threadid));

       }
        if (focusid>-1)   {
            query.andQualifier(Expression.fromString("postid <= " + focusid));
        }
        query.setPageSize(count);
        //query.setFetchingDataRows(true);
        List<Post> posts = DbProvider.getContext().performQuery(query);

        return new IndexedIterator<Post>(posts,i,count);
    }

    public int size() {
        StringBuilder where = new StringBuilder();

        if (threadid > -1) {
            where.append("threadid = ").append(threadid);
            if (focusid > -1) {
                where.append(" and ");
            }
        }
        if (focusid > -1) {
            where.append("postid <= ").append(focusid);
        }
        return CayenneUtils.count(DbProvider.getContext(),"Post",Post.class,where.toString() );
    }

    public IModel<Post> model(Post post) {
        return new PostModel(post);
    }


}
