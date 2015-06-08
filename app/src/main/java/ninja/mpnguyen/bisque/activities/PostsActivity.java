package ninja.mpnguyen.bisque.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.lang.ref.WeakReference;

import ninja.mpnguyen.bisque.NavigationDrawerFragment;
import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.fragments.PostsListFragment;
import ninja.mpnguyen.bisque.fragments.StoryListFragment;
import ninja.mpnguyen.bisque.things.MetaDataedPost;

public class PostsActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            PostsListFragment list = PostsListFragment.newInstance(new PostClickListener(this));
            t.add(R.id.content_primary, list);
            t.commit();
        }

        NavigationDrawerFragment f = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // For some reason, setting navigation icon via XML does not work
        // Seems to be a big in Android SDK
        // https://stackoverflow.com/questions/26525229/toolbar-navigation-icon-never-set
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_menu_white);
        setSupportActionBar(toolbar);

        f.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void showPost(MetaDataedPost post) {
        FragmentManager fm = getSupportFragmentManager();
        StoryListFragment f = new StoryListFragment.Builder()
                .post(post.post)
                .build();
        fm.beginTransaction()
                .replace(R.id.content_secondary, f)
                .addToBackStack("content_secondary")
                .commit();
    }

    private static class PostClickListener implements PostsListFragment.PostClickListener {
        private final WeakReference<PostsActivity> activityRef;

        private PostClickListener(PostsActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onPostClicked(MetaDataedPost post) {
            PostsActivity postsActivity = activityRef.get();
            postsActivity.showPost(post);
        }
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Do nothing
    }
}
