package ninja.mpnguyen.bisque.fragments.story;

import java.util.concurrent.Callable;

import ninja.mpnguyen.bisque.nio.DualFetcher;
import ninja.mpnguyen.bisque.services.Cashier;
import ninja.mpnguyen.bisque.things.BisqueStory;
import ninja.mpnguyen.chowders.things.json.Story;

class CachedFetcher extends DualFetcher<BisqueStory> {
    private final String short_id;

    CachedFetcher(String short_id, DualListener<BisqueStory> listener) {
        super(listener);
        this.short_id = short_id;
    }

    @Override
    public Callable<BisqueStory> getFetcher() {
        return new StoryFetcher(short_id);
    }

    @Override
    public Cashier<BisqueStory> getCashier() {
        return new StoryCashier(short_id);
    }

    private static class StoryFetcher implements Callable<BisqueStory> {
        private final String short_id;

        private StoryFetcher(String short_id) {
            this.short_id = short_id;
        }

        @Override
        public BisqueStory call() throws Exception {
            Story story = ninja.mpnguyen.chowders.nio.json.StoryFetcher.get(short_id);
            if (story == null) return null;
            else {
                BisqueStory bisqueStory = new BisqueStory();
                bisqueStory.post = story;
                bisqueStory.comments = story.comments;
                return bisqueStory;
            }
        }
    }
}
