package ninja.mpnguyen.bisque.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.databases.MetafyTask;
import ninja.mpnguyen.bisque.databases.PostHelper;
import ninja.mpnguyen.bisque.loaders.PostsLoaderCallbacks;
import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.bisque.views.posts.PostsAdapter;
import ninja.mpnguyen.chowders.things.json.Post;

public class PostsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public interface PostClickListener {
        void onPostClicked(MetaDataedPost post);
    }

    private final Observer postsObserver = new PostsObserver(this);

    public void setListener(PostClickListener listener) {
        this.listener = listener;
    }

    public PostClickListener listener = null;

    // TODO: I really don't like this global state... I should get rid of this
    public volatile Post[] posts = null;

    public static PostsListFragment newInstance(PostClickListener listener) {
        PostsListFragment f = new PostsListFragment();
        f.setListener(listener);
        return f;
    }

    public static class PostsLoaderListener extends PostsLoaderCallbacks {
        private final WeakReference<PostsListFragment> fRef;

        public PostsLoaderListener(Context context, String topic, PostsListFragment f) {
            super(context, topic);
            this.fRef = new WeakReference<>(f);
        }

        @Override
        public void onRefreshing() {

        }

        @Override
        public void onResult(Post[] data, boolean fromServer) {
            PostsListFragment f = fRef.get();
            if (f == null) return;

            if (fromServer || data != null) {
                f.posts = data;
                f.updateMetadata(fromServer);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View result = inflater.inflate(R.layout.fragment_list_posts, container, false);

        PostHelper.observable.addObserver(postsObserver);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) result.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = (RecyclerView) result.findViewById(R.id.content_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.swapAdapter(new PostsAdapter(null, true, listener), false);

        return result;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoaderManager loaderManager = getLoaderManager();
        PostsLoaderListener listener = new PostsLoaderListener(view.getContext(), null, this);
        loaderManager.initLoader(PostsLoaderListener.DISK_CACHED, null, listener);
        loaderManager.initLoader(PostsLoaderListener.SERVER, null, listener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        PostHelper.observable.deleteObserver(postsObserver);
    }

    @Override
    public void onRefresh() {
        View mainView = getView();
        if (mainView == null) return;

        LoaderManager loaderManager = getLoaderManager();
        PostsLoaderListener listener = new PostsLoaderListener(mainView.getContext(), null, this);
        loaderManager.restartLoader(PostsLoaderListener.DISK_CACHED, null, listener);
        loaderManager.restartLoader(PostsLoaderListener.SERVER, null, listener);
    }

    public void updateMetadata(boolean andResetRefresher) {
        View view = getView();
        if (view == null) return;

        SwipeRefreshLayout swipeRefreshLayout = andResetRefresher ? (SwipeRefreshLayout) view.findViewById(R.id.swipe) : null;
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.content_view);
        PostsFetchedListener listener = new PostsFetchedListener(this.listener, swipeRefreshLayout, recyclerView);
        new MetafyTask(getActivity(), posts, listener).execute();
    }

    private static class PostsFetchedListener extends RefreshingListener<MetaDataedPost[]> {
        private final PostClickListener listener;
        private final WeakReference<RecyclerView> recyclerRef;

        private PostsFetchedListener(PostClickListener listener, @Nullable SwipeRefreshLayout refreshLayout, @Nullable RecyclerView content) {
            super(refreshLayout);
            this.recyclerRef = new WeakReference<>(content);
            this.listener = listener;
        }

        @Override
        public void onSuccess(@NonNull MetaDataedPost[] result) {
            super.onSuccess(result);
            RecyclerView recycler = recyclerRef.get();
            if (recycler == null) return;

            recycler.swapAdapter(new PostsAdapter(result, false, listener), false);
        }

        @Override
        public void onError() {
            super.onError();
            RecyclerView recycler = recyclerRef.get();
            if (recycler == null) return;

            recycler.swapAdapter(new PostsAdapter(null, false, listener), false);
        }
    }

    /**
     * This provides a level of indirection so that the Fragment
     * isn't being strongly referenced by the global observable.
     *
     * In here, the fragment is weakly referenced by a static
     * postsObserver which simply passes along the update.
     * Thus, we don't stupidly leak memory.
     */
    private static class PostsObserver implements Observer {
        private final WeakReference<PostsListFragment> fRef;

        private PostsObserver(PostsListFragment f) {
            this.fRef = new WeakReference<>(f);
        }

        @Override
        public void update(Observable observable, Object data) {
            PostsListFragment f = fRef.get();
            if (f == null) {
                observable.deleteObserver(this);
            } else {
                f.updateMetadata(false);
            }
        }
    }
}
