package ninja.mpnguyen.bisque.services;

import android.support.annotation.Nullable;

import ninja.mpnguyen.chowders.things.json.Post;

public class PostsCashier extends Cashier<Post[]> {
    private final String topic;

    public PostsCashier() {
        this(null);
    }

    public PostsCashier(@Nullable String topic) {
        this.topic = topic;
    }

    @Override
    public String getKey() {
        return "Frontpage Posts with Topic: " + topic;
    }

    @Override
    public Class<Post[]> getType() {
        return Post[].class;
    }
}
