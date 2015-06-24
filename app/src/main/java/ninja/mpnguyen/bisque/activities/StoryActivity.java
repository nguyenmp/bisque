package ninja.mpnguyen.bisque.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.List;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.databases.PostHelper;
import ninja.mpnguyen.bisque.fragments.story.StoryListFragment;
import ninja.mpnguyen.bisque.things.PostMetadataWrapper;
import ninja.mpnguyen.chowders.things.json.Post;
import ninja.mpnguyen.chowders.things.json.Story;

public class StoryActivity extends AppCompatActivity {
    public static final String EXTRA_POST = "ninja.mpnguyen.bisque.activities.StoryActivity.EXTRA_POST";
    public static final String EXTRA_COMMENTS = "ninja.mpnguyen.bisque.activities.StoryActivity.EXTRA_COMMENTS";

    public static void showPost(Context context, Post post, boolean commentsFirst) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_POST, post);
        intent.putExtra(StoryActivity.EXTRA_COMMENTS, commentsFirst);
        intent.setData(Uri.parse(post.comments_url));
        context.startActivity(intent);

        try {
            PostMetadataWrapper metadataWrapper = PostHelper.getMetadata(post, context);
            PostMetadataWrapper.markAsRead(metadataWrapper, context);
        } catch (SQLException ignored) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        // Only add fragment if we're not restoring from some previous instance
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();

            StoryListFragment f = new StoryListFragment.Builder()
                    .post(getStoryFromIntent())
                    .shortID(getShortId(getIntent().getData()))
                    .showCommentsFirst(getIntent().getBooleanExtra(EXTRA_COMMENTS, false))
                    .build();
            t.add(R.id.content_primary, f);
            t.commit();
        }
    }

    private Story getStoryFromIntent() {
        Intent intent = getIntent();
        Post post = intent.hasExtra(EXTRA_POST) ? (Post) intent.getSerializableExtra(EXTRA_POST) : null;
        return new Story(post);
    }

    private String getShortId(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths.size() >= 2 && paths.get(0).equalsIgnoreCase("s")) {
            return paths.get(1);
        } else {
            return null;
        }
    }

}
