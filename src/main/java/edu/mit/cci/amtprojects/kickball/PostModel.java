package edu.mit.cci.amtprojects.kickball;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Post;
import edu.mit.cci.amtprojects.kickball.cayenne.User;
import org.apache.cayenne.DataObjectUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.model.LoadableDetachableModel;


import java.util.Date;

/**
 * User: jintrone
 * Date: 9/1/12
 * Time: 5:13 AM
 */
public class PostModel  extends LoadableDetachableModel<Post> {


    private int id;
    private static Logger log = Logger.getLogger(PostModel.class);

    public PostModel(Post p) {
        this(p.getPostid());
    }

    public PostModel(int id) {
        this.id = id;
    }

    @Override
    protected Post load() {
        Post p = null;
        try {
            p = DataObjectUtils.objectForPK(DbProvider.getContext(),Post.class,id);

        } catch (Exception e) {
            log.warn("Encountered error retrieving post "+id,e);
        }
        if (p == null) return null;
        else return new HtmlizedPost(p);
    }

      /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Long.valueOf(id).hashCode();
    }

    /**
     * used for dataview with ReuseIfModelsEqualStrategy item reuse strategy
     *
     * @see org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj == null)
        {
            return false;
        }
        else if (obj instanceof PostModel)
        {
            PostModel other = (PostModel)obj;
            return other.id == id;
        }
        return false;
    }

    public static class HtmlizedPost extends Post {

        private Post post;

        @Override
        public String getContent() {
            return QuoteUtilities.sanitize(post.getContent());
        }

        @Override
        public Date getCreated() {
            return post.getCreated();
        }



        @Override
        public String getIpAddress() {
            return post.getIpAddress();
        }



        @Override
        public Integer getPostid() {
            return post.getPostid();
        }

        @Override
        public Thread getPostToThread() {
            return post.getPostToThread();
        }

        @Override
        public User getPostToUser() {
            return post.getPostToUser();
        }


        @Override
        public long getSnapshotVersion() {
            return post.getSnapshotVersion();
        }

        @Override
        public Integer getThreadid() {
            return post.getThreadid();
        }

        public HtmlizedPost(Post p) {
            this.post = p;
        }
    }
}
