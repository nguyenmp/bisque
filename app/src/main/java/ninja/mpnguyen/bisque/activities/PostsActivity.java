package ninja.mpnguyen.bisque.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.fragments.PostsListFragment;

public class PostsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.add(R.id.content_main, PostsListFragment.newInstance());
        t.commit();
    }
}
