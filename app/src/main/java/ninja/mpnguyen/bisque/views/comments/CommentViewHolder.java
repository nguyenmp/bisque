package ninja.mpnguyen.bisque.views.comments;

import android.view.View;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class CommentViewHolder extends StoryViewHolder {
    public final View padding, content, container;
    public final TextView comment_text, comment_author;

    public CommentViewHolder(View itemView) {
        super(itemView);
        this.content = itemView.findViewById(R.id.comment_content);
        this.container = itemView.findViewById(R.id.comment_container);
        this.padding = itemView.findViewById(R.id.comment_padding);
        this.comment_text = (TextView) itemView.findViewById(R.id.comment_text);
        this.comment_author = (TextView) itemView.findViewById(R.id.comment_author);
    }
}
