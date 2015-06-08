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
import ninja.mpnguyen.bisque.fragments.StoryListFragment;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.chowders.things.json.Post;
import ninja.mpnguyen.chowders.things.json.Story;

public class StoryActivity extends AppCompatActivity {
    public static final String EXTRA_POST = "ninja.mpnguyen.bisque.activities.StoryActivity.ARGUMENT_POST";

    public static void showPost(Context context, Post post) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_POST, post);
        intent.setData(Uri.parse(post.comments_url));
        context.startActivity(intent);

        try {
            MetaDataedPost metadata = PostHelper.getMetadata(post, context);
            metadata.metadata.read = true;
            PostHelper.setMetadata(metadata.metadata, context);
        } catch (SQLException ignored) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        StoryListFragment f = new StoryListFragment.Builder()
                .post(getStoryFromIntent())
                .shortID(getShortId(getIntent().getData()))
                .build();
        t.add(R.id.content_primary, f);
        t.commit();
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
