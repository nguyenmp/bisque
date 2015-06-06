package ninja.mpnguyen.bisque.nio;

import ninja.mpnguyen.chowders.nio.json.StoryFetcher;
import ninja.mpnguyen.chowders.things.json.Story;

public class StoryFetcherTask extends FetcherTask<Story> {
    private final String short_id;

    public StoryFetcherTask(Listener<Story> listener, String short_id) {
        super(listener);
        this.short_id = short_id;
    }

    @Override
    public Story doBlockingStuff() throws Exception {
        return StoryFetcher.get(short_id);
    }
}
