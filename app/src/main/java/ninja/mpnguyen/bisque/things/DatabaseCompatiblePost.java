package ninja.mpnguyen.bisque.things;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import ninja.mpnguyen.chowders.things.Post;
import ninja.mpnguyen.chowders.things.User;

@DatabaseTable
public class DatabaseCompatiblePost implements Serializable {
    public static final String SHORT_ID_NAME = "short_id";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(uniqueIndex = true,columnName = SHORT_ID_NAME)
    public String short_id;

    @DatabaseField
    public String created_at;

    @DatabaseField
    public String url;

    @DatabaseField
    public String title;

    @DatabaseField
    public int score;

    @DatabaseField
    public int comment_count;

    @DatabaseField
    public String description;

    @DatabaseField
    public String comments_url;

    @DatabaseField
    public User submitter_user;

    @DatabaseField
    public String[] tags;

    public DatabaseCompatiblePost(Post post) {
        this.short_id = post.short_id;
        this.created_at = post.created_at;
        this.url = post.url;
        this.title = post.title;
        this.score = post.score;
        this.comment_count = post.comment_count;
        this.description = post.description;
        this.comments_url = post.comments_url;
        this.submitter_user = new User(post.submitter_user);
        this.tags = post.tags;
    }

    public DatabaseCompatiblePost(DatabaseCompatiblePost post) {
        this.short_id = post.short_id;
        this.created_at = post.created_at;
        this.url = post.url;
        this.title = post.title;
        this.score = post.score;
        this.comment_count = post.comment_count;
        this.description = post.description;
        this.comments_url = post.comments_url;
        this.submitter_user = new User(post.submitter_user);
        this.tags = post.tags;
    }
}
