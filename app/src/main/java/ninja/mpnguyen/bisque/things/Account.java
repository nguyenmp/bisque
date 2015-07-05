package ninja.mpnguyen.bisque.things;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import ninja.mpnguyen.chowders.things.json.Comment;

@DatabaseTable
public class Account implements Serializable {
    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(uniqueIndex = true)
    public String username;

    @DatabaseField
    public String cookie;

    @DatabaseField
    public String email;

    public Account() {
        // For ORM Lite
    }

    public Account(Account other) {
        this.id = other.id;
        this.username = other.username;
        this.cookie = other.cookie;
        this.email = other.email;
    }
}
