package ninja.mpnguyen.bisque.views.posts;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public String short_id;

    public final CardView cardView;
    public final TextView title, tags, subheading;
    public final ImageView action_comment, action_delete, action_toggle_read, action_author;
    public final View container;

    public PostViewHolder(View itemView) {
        super(itemView);
        this.container = itemView.findViewById(R.id.post_container);
        this.cardView = (CardView) itemView.findViewById(R.id.card_view);
        this.title = (TextView) itemView.findViewById(R.id.post_title);
        this.tags = (TextView) itemView.findViewById(R.id.post_tags);
        this.subheading = (TextView) itemView.findViewById(R.id.post_subheading);
        this.action_comment = (ImageView) itemView.findViewById(R.id.post_action_comments);
        this.action_delete = (ImageView) itemView.findViewById(R.id.post_action_delete);
        this.action_toggle_read = (ImageView) itemView.findViewById(R.id.post_action_read);
        this.action_author = (ImageView) itemView.findViewById(R.id.post_action_author);
    }
}
