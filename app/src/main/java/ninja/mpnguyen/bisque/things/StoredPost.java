package ninja.mpnguyen.bisque.things;

import ninja.mpnguyen.chowders.things.Post;

public class StoredPost extends Post {
    public boolean hidden;
    public boolean read;

    public StoredPost(Post post) {
        super(post);
    }

    public StoredPost(StoredPost post) {
        super(post);
        this.hidden = post.hidden;
        this.read = post.read;
    }
}
