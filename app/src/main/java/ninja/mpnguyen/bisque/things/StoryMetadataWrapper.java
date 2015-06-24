package ninja.mpnguyen.bisque.things;

import java.io.Serializable;

public class StoryMetadataWrapper implements Serializable {
    public PostMetadataWrapper postWrapper;
    public CommentMetadataWrapper[] commentWrappers;
}
