package ninja.mpnguyen.bisque.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.bisque.nio.UserFetcherTask;
import ninja.mpnguyen.bisque.views.user.UserPresenter;
import ninja.mpnguyen.bisque.views.user.UserViewHolder;
import ninja.mpnguyen.chowders.things.User;

public class UserActivity extends AppCompatActivity {
    public static final String EXTRA_USER = "ninja.mpnguyen.bisque.activities.UserActivity.EXTRA_USER";
    public static final String EXTRA_USERNAME = "ninja.mpnguyen.bisque.activities.UserActivity.EXTRA_USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user);
        UserViewHolder viewHolder = new UserViewHolder(findViewById(R.id.user_container));

        User user = getUserFromIntent();
        if (user != null) {
            UserPresenter.bindItem(viewHolder, user);
        }

        Intent intent = getIntent();
        Uri data = intent.getData();
        String username = null;
        if (intent.hasExtra(EXTRA_USERNAME)) {
            username = intent.getStringExtra(EXTRA_USERNAME);
        } else if (data != null && data.toString().startsWith("https://lobste.rs/u/")) {
            List<String> paths = data.getPathSegments();
            if (paths.size() >= 2 && paths.get(0).equalsIgnoreCase("u")) username = paths.get(1);
        }

        if (username != null) {
            new UserFetcherTask(new UserListener(viewHolder), username).execute();
        }
    }

    private User getUserFromIntent() {
        Intent intent = getIntent();
        return intent.hasExtra(EXTRA_USER) ? (User) intent.getSerializableExtra(EXTRA_USER) : null;
    }

    private static class UserListener implements FetcherTask.Listener<User> {
        private final UserViewHolder vh;

        private UserListener(UserViewHolder vh) {
            this.vh = vh;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(@NonNull User result) {
            UserPresenter.bindItem(vh, result);
        }

        @Override
        public void onError() {

        }
    }
}
