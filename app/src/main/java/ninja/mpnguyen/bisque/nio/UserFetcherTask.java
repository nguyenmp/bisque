package ninja.mpnguyen.bisque.nio;

import ninja.mpnguyen.chowders.nio.json.UserFetcher;
import ninja.mpnguyen.chowders.things.json.User;

public class UserFetcherTask extends FetcherTask<User> {
    private final String username;

    public UserFetcherTask(Listener<User> listener, String username) {
        super(listener);
        this.username = username;
    }

    @Override
    public User doBlockingStuff() throws Exception {
        return new UserFetcher(username).call();
    }
}
