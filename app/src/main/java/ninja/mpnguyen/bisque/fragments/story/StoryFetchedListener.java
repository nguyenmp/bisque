package ninja.mpnguyen.bisque.fragments.story;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.things.BisqueStory;
import ninja.mpnguyen.bisque.things.StoryMetadataWrapper;

class StoryFetchedListener extends RefreshingListener<BisqueStory> {
    private final WeakReference<StoryListFragment> fRef;

    StoryFetchedListener(@Nullable StoryListFragment f, @Nullable SwipeRefreshLayout refreshLayout) {
        super(refreshLayout);
        this.fRef = new WeakReference<>(f);
    }

    @Override
    public void onSuccess(@NonNull BisqueStory result) {
        StoryListFragment f = fRef.get();
        if (f != null) f.updateMetadata(result);
    }

    @Override
    public void onError() {
        super.onError();

        StoryListFragment f = fRef.get();
        if (f != null) f.updateMetadata();
    }
}
