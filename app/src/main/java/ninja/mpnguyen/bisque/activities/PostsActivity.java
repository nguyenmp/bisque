package ninja.mpnguyen.bisque.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ninja.mpnguyen.bisque.NavigationDrawerFragment;
import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.fragments.PostsListFragment;

public class PostsActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            DrawerLayout d = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (d.isDrawerOpen(GravityCompat.START)) {
                d.closeDrawer(GravityCompat.START);
            } else {
                d.openDrawer(GravityCompat.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            PostsListFragment list = PostsListFragment.newInstance();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        f.setUp(R.id.navigation_drawer, drawer);
    }
}
