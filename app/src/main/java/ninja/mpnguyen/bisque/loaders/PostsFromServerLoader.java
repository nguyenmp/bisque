package ninja.mpnguyen.bisque.loaders;

import android.content.Context;

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
        try {
            Post[] posts = FrontPage.get(FrontPage.Sort.Hottest);
            new PostsCashier().putIntoCache(posts);
            return posts;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
