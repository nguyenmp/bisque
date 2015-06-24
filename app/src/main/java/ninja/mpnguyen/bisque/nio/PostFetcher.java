package ninja.mpnguyen.bisque.nio;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import ninja.mpnguyen.chowders.nio.json.FrontPage;
import ninja.mpnguyen.chowders.things.json.Post;

public class PostFetcher implements Callable<Post[]> {
    private final WeakReference<Context> cRef;

    public PostFetcher(Context c) {
        this.cRef = new WeakReference<>(c);
    }

    @Override
    public Post[] call() throws Exception {
        Context c = cRef.get();
        if (c == null) return null;

        return FrontPage.get(FrontPage.Sort.Hottest);
    }
}
