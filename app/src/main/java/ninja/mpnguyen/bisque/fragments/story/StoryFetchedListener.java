package ninja.mpnguyen.bisque.fragments.story;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.things.BisqueStory;
import ninja.mpnguyen.bisque.things.StoryMetadataWrapper;
import ninja.mpnguyen.bisque.views.comments.StoryAdapter;

class StoryFetchedListener extends RefreshingListener<BisqueStory> {
    private final WeakReference<StoryListFragment> fRef;
    private final WeakReference<RecyclerView> contentRef;
    private final StoryMetadataWrapper cachedStory;

    StoryFetchedListener(@Nullable StoryListFragment f, @Nullable SwipeRefreshLayout refreshLayout, @Nullable RecyclerView content, StoryMetadataWrapper cachedStory) {
        super(refreshLayout);
        this.fRef = new WeakReference<>(f);
        this.contentRef = new WeakReference<>(content);
        this.cachedStory = cachedStory;
    }

    @Override
    public void onSuccess(@NonNull BisqueStory result) {
        StoryListFragment f = fRef.get();
        f.updateMetadata(result);
    }

    @Override
    public void onError() {
        super.onError();

        RecyclerView content = contentRef.get();
        if (content == null) return;
        content.swapAdapter(new StoryAdapter(cachedStory, false, new HideCommentListener(fRef.get())), false);
    }
}
