package ninja.mpnguyen.bisque.fragments.story;

import android.content.Context;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ninja.mpnguyen.bisque.databases.CommentHelper;
import ninja.mpnguyen.bisque.databases.PostHelper;
import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.bisque.things.BisqueStory;
import ninja.mpnguyen.bisque.things.CommentMetadataWrapper;
import ninja.mpnguyen.bisque.things.PostMetadataWrapper;
import ninja.mpnguyen.bisque.things.StoryMetadataWrapper;
import ninja.mpnguyen.chowders.things.json.Comment;

/**
 * Queries the database and wraps the given story with metadata in the background
 */
class MetafyTask extends FetcherTask<StoryMetadataWrapper> {
    private final BisqueStory story;
    private final WeakReference<Context> contextRef;

    MetafyTask(BisqueStory story, @Nullable Context context, Listener<StoryMetadataWrapper> listener) {
        super(listener);
        this.story = story;
        this.contextRef = new WeakReference<>(context);
    }

    @Override
    public StoryMetadataWrapper doBlockingStuff() throws Exception {
        if (story == null || story.post == null) return null;

        Context context = contextRef.get();
        if (context == null) return null;

        try {
            PostMetadataWrapper postWrapper = PostHelper.getMetadata(story.post, context);

            CommentMetadataWrapper[] commentWrappers = null;
            if (story.comments != null) {
                List<CommentMetadataWrapper> _commentWrappers = new ArrayList<>();
                for (Comment comment : story.comments) {
                    CommentMetadataWrapper commentWrapper = CommentHelper.getMetadata(comment, context);
                    _commentWrappers.add(commentWrapper);
                }
                commentWrappers = _commentWrappers.toArray(new CommentMetadataWrapper[_commentWrappers.size()]);
            }

            StoryMetadataWrapper storyWrapper = new StoryMetadataWrapper();
            storyWrapper.commentWrappers = commentWrappers;
            storyWrapper.postWrapper = postWrapper;
            return storyWrapper;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
