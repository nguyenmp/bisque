package ninja.mpnguyen.bisque.fragments.story;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

import ninja.mpnguyen.bisque.databases.CommentHelper;
import ninja.mpnguyen.bisque.things.CommentMetadata;
import ninja.mpnguyen.bisque.things.CommentMetadataWrapper;
import ninja.mpnguyen.bisque.views.comments.StoryAdapter;

class HideCommentListener implements StoryAdapter.HideCommentListener {
    private final WeakReference<StoryListFragment> fRef;

    public HideCommentListener(StoryListFragment f) {
        this.fRef = new WeakReference<>(f);
    }

    @Override
    public void setCommentHidden(CommentMetadataWrapper commentWrapper, boolean hidden) {
        StoryListFragment f = fRef.get();
        if (f == null) return;

        Context context = f.getActivity();
        if (context == null) return;

        CommentMetadata metadata = commentWrapper.metadata;
        metadata.hide_children = hidden;
        try {
            CommentHelper.setMetadata(metadata, context);
            f.updateMetadata();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
