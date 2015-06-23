package ninja.mpnguyen.bisque.fragments.story;

import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.bisque.things.BisqueStory;
import ninja.mpnguyen.chowders.nio.json.StoryFetcher;
import ninja.mpnguyen.chowders.things.json.Story;

class StoryFetcherTask extends FetcherTask<BisqueStory> {
    private final String short_id;

    StoryFetcherTask(Listener<BisqueStory> listener, String short_id) {
        super(listener);
        this.short_id = short_id;
    }

    @Override
    public BisqueStory doBlockingStuff() throws Exception {
        Story story = StoryFetcher.get(short_id);
        if (story == null) return null;
        else {
            BisqueStory bisqueStory = new BisqueStory();
            bisqueStory.post = story;
            bisqueStory.comments = story.comments;
            return bisqueStory;
        }
    }
}
