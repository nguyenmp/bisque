package ninja.mpnguyen.bisque.views.posts;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public final CardView cardView;
    public final TextView title, tags, subheading;
    public final View action_comment, action_delete, action_toggle_read, action_author;

    public PostViewHolder(View itemView) {
        super(itemView);
        this.cardView = (CardView) itemView.findViewById(R.id.card_view);
        this.title = (TextView) itemView.findViewById(R.id.post_title);
        this.tags = (TextView) itemView.findViewById(R.id.post_tags);
        this.subheading = (TextView) itemView.findViewById(R.id.post_subheading);
        this.action_comment = itemView.findViewById(R.id.post_action_comments);
        this.action_delete = itemView.findViewById(R.id.post_action_delete);
        this.action_toggle_read = itemView.findViewById(R.id.post_action_read);
        this.action_author = itemView.findViewById(R.id.post_action_author);
    }
}
