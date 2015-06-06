package ninja.mpnguyen.bisque.nio;

import android.content.Context;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.chowders.nio.FrontPage;
import ninja.mpnguyen.chowders.things.Post;

public class PostsFetcherTask extends FetcherTask<Post[]> {
    private final WeakReference<Context> contextRef;

    public PostsFetcherTask(Listener<Post[]> listener, Context context) {
        super(listener);
        this.contextRef = new WeakReference<>(context);
    }

    @Override
    public Post[] doBlockingStuff() throws Exception {
        Context context = contextRef.get();
        if (context == null) return null;
        
        return FrontPage.get(FrontPage.Sort.Hottest);
    }
}
