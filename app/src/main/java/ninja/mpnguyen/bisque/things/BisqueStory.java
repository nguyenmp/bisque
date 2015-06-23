package ninja.mpnguyen.bisque.things;

import java.io.Serializable;

import ninja.mpnguyen.chowders.things.json.Comment;
import ninja.mpnguyen.chowders.things.json.Post;

public class BisqueStory implements Serializable {
    public Post post;
    public Comment[] comments;
}
