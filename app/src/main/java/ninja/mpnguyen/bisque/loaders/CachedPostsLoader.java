package ninja.mpnguyen.bisque.loaders;

import android.content.Context;

import ninja.mpnguyen.bisque.services.Cashier;
import ninja.mpnguyen.bisque.services.PostsCashier;
import ninja.mpnguyen.chowders.things.json.Post;

public class CachedPostsLoader extends CachedLoader<Post[]> {
    private final String topic;

    public CachedPostsLoader(Context context, String topic) {
        super(context);
        this.topic = topic;
    }

    public Cashier<Post[]> getCachier() {
        return new PostsCashier(topic);
    }
}
