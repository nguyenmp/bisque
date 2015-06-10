package ninja.mpnguyen.bisque.things;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import ninja.mpnguyen.chowders.things.json.Comment;

@DatabaseTable
public class CommentMetadata implements Serializable {
    public static final String COLUMN_NAME_SHORT_ID = "short_id_column_name";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(uniqueIndex = true, columnName = COLUMN_NAME_SHORT_ID)
    public String short_id;

    @DatabaseField
    public boolean hide_children;

    public CommentMetadata() {
        // For ORM Lite
    }

    public CommentMetadata(CommentMetadata other) {
        this.id = other.id;
        this.short_id = other.short_id;
        this.hide_children = other.hide_children;
    }

    public CommentMetadata(Comment comment) {
        this.short_id = comment.short_id;
    }
}
