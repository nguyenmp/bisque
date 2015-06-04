package ninja.mpnguyen.bisque.views.comments;

import android.view.View;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class CommentViewHolder extends StoryViewHolder {
    public final View padding;
    public final TextView text, tags;

    public CommentViewHolder(View itemView) {
        super(itemView);
        this.padding = itemView.findViewById(R.id.comment_padding);
        this.text = (TextView) itemView.findViewById(R.id.comment_text);
        this.tags = (TextView) itemView.findViewById(R.id.author);
    }
}
