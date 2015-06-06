package ninja.mpnguyen.bisque.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.bisque.nio.PostsFetcherTask;
import ninja.mpnguyen.bisque.nio.RefreshingListener;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.bisque.views.posts.PostsAdapter;
import ninja.mpnguyen.chowders.things.json.Post;

public class PostsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final Observer postsObserver = new PostsObserver(this);

    // TODO: I really don't like this global state... I should get rid of this
    private Post[] posts = null;

    public static PostsListFragment newInstance() {
        return new PostsListFragment();
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

        recyclerView.swapAdapter(new PostsAdapter(null, getActivity(), true), false);

        return result;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onRefresh();
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

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mainView.findViewById(R.id.swipe);
        RecyclerView recyclerView = (RecyclerView) mainView.findViewById(R.id.content_view);

        if (swipeRefreshLayout == null || recyclerView == null) return;

        new PostsFetcherTask(new PostsListener(), mainView.getContext()).execute();
    }

    public void updateMetadata() {
        View view = getView();
        if (view == null) return;

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.content_view);
        PostsFetchedListener listener = new PostsFetchedListener(getActivity(), swipeRefreshLayout, recyclerView);
        new MetafyTask(getActivity(), posts, listener).execute();
    }

    private class PostsListener implements FetcherTask.Listener<Post[]> {
        @Override
        public void onStart() {
            // Do nothing
        }

        @Override
        public void onSuccess(@NonNull Post[] result) {
            posts = result;
            updateMetadata();
        }

        @Override
        public void onError() {
            posts = null;
            updateMetadata();
        }
    }

    private static class PostsFetchedListener extends RefreshingListener<MetaDataedPost[]> {
        private final WeakReference<Activity> activityRef;
        private final WeakReference<RecyclerView> recyclerRef;

        private PostsFetchedListener(@Nullable Activity activity, @Nullable SwipeRefreshLayout refreshLayout, @Nullable RecyclerView content) {
            super(refreshLayout);
            this.recyclerRef = new WeakReference<>(content);
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull MetaDataedPost[] result) {
            super.onSuccess(result);
            RecyclerView recycler = recyclerRef.get();
            if (recycler == null) return;

            recycler.swapAdapter(new PostsAdapter(result, activityRef.get(), false), false);
        }

        @Override
        public void onError() {
            super.onError();
            RecyclerView recycler = recyclerRef.get();
            if (recycler == null) return;

            recycler.swapAdapter(new PostsAdapter(null, activityRef.get(), false), false);
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
                f.updateMetadata();
            }
        }
    }
}
