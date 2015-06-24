package ninja.mpnguyen.bisque.fragments.story;

import ninja.mpnguyen.bisque.services.Cashier;
import ninja.mpnguyen.bisque.things.BisqueStory;

public class StoryCashier extends Cashier<BisqueStory> {
    private final String short_id;

    public StoryCashier(String short_id) {
        this.short_id = short_id;
    }

    @Override
    public String getKey() {
        return "Story with short_id=" + short_id;
    }

    @Override
    public Class<BisqueStory> getType() {
        return BisqueStory.class;
    }
}
