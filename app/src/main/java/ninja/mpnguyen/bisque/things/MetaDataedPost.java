package ninja.mpnguyen.bisque.things;

import ninja.mpnguyen.chowders.things.json.Post;

public class MetaDataedPost {
    public final PostMetadata metadata;
    public final Post post;

    public MetaDataedPost(PostMetadata metadata, Post post) {
        this.metadata = metadata;
        this.post = post;
    }

    public MetaDataedPost(MetaDataedPost other) {
        this.metadata = new PostMetadata(other.metadata);
        this.post = new Post(other.post);
    }
}
