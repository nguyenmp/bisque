package ninja.mpnguyen.bisque.fragments.story;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.views.comments.StoryAdapter;
import ninja.mpnguyen.chowders.things.json.Story;

class StoryFetchedListener extends RefreshingListener<Story> {
    private final WeakReference<RecyclerView> contentRef;
    private final Story cachedStory;

    StoryFetchedListener(@Nullable SwipeRefreshLayout refreshLayout, @Nullable RecyclerView content, Story cachedStory) {
        super(refreshLayout);
        this.contentRef = new WeakReference<>(content);
        this.cachedStory = cachedStory;
    }

    @Override
    public void onSuccess(@NonNull Story result) {
        super.onSuccess(result);

        RecyclerView content = contentRef.get();
        if (content == null) return;
        content.swapAdapter(new StoryAdapter(result, false), false);
    }

    @Override
    public void onError() {
        super.onError();

        RecyclerView content = contentRef.get();
        if (content == null) return;
        content.swapAdapter(new StoryAdapter(cachedStory, false), false);
    }
}
