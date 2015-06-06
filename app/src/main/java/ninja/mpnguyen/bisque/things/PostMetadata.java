package ninja.mpnguyen.bisque.things;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import ninja.mpnguyen.chowders.things.json.Post;

@DatabaseTable
public class PostMetadata implements Serializable {
    public static final String COLUMN_NAME_SHORT_ID = "short_id_column_name";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(uniqueIndex = true, columnName = COLUMN_NAME_SHORT_ID)
    public String short_id;

    @DatabaseField
    public boolean hidden;

    @DatabaseField
    public boolean read;

    public PostMetadata() {
        // For ORM Lite
    }

    public PostMetadata(PostMetadata post) {
        this.id = post.id;
        this.short_id = post.short_id;
        this.hidden = post.hidden;
        this.read = post.read;
    }

    public PostMetadata(Post post) {
        this.short_id = post.short_id;
    }
}
