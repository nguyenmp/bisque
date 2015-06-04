package ninja.mpnguyen.bisque.nio;

import ninja.mpnguyen.bisque.nio.FetcherTask;
import ninja.mpnguyen.chowders.nio.FrontPage;
import ninja.mpnguyen.chowders.things.Post;

public class PostsFetcherTask extends FetcherTask<Post[]> {
    public PostsFetcherTask(Listener<Post[]> listener) {
        super(listener);
    }

    @Override
    public Post[] doBlockingStuff() throws Exception {
        return FrontPage.get(FrontPage.Sort.Hottest);
    }
}
