package ninja.mpnguyen.bisque.fragments.story;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.things.StoryMetadataWrapper;

class StoryMetafiedListener extends RefreshingListener<StoryMetadataWrapper> {
    private final WeakReference<StoryListFragment> fRef;
    private final StoryMetadataWrapper cachedStory;

    StoryMetafiedListener(StoryListFragment f, @Nullable SwipeRefreshLayout refreshLayout, StoryMetadataWrapper cachedStory) {
        super(refreshLayout);
        this.fRef = new WeakReference<>(f);
        this.cachedStory = cachedStory;
    }

    @Override
    public void onSuccess(@NonNull StoryMetadataWrapper result) {
        super.onSuccess(result);
        StoryListFragment f = fRef.get();
        if (f != null) f.updateMetadata(result);
    }

    @Override
    public void onError() {
        super.onError();
        StoryListFragment f = fRef.get();
        if (f != null) f.updateMetadata(cachedStory);
    }
}
