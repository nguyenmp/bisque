package ninja.mpnguyen.bisque.loaders;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import ninja.mpnguyen.bisque.services.PostsCashier;
import ninja.mpnguyen.chowders.nio.json.FrontPage;
import ninja.mpnguyen.chowders.things.json.Post;

public class PostsFromServerLoader extends GenericLoader<Post[]> {
    public PostsFromServerLoader(Context context) {
        super(context);
    }

    @Override
    public Post[] loadInBackground() {
        Post[] posts = null;
        try {
            posts = FrontPage.get(FrontPage.Sort.Hottest);
            return posts;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                // When returning from a killed state, this action of
                // putting it into the cache can throw a null pointer
                // this would cause us to return null in the above block
                // which is invalid because posts could have been non-null
                if (posts != null) new PostsCashier().putIntoCache(posts);
            } catch (Exception e) {
                Log.i(PostsFromServerLoader.class.getName(), "Failed to put " + posts + " into the cache.", e);
            }
        }
    }
}
