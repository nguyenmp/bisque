package ninja.mpnguyen.bisque.views.posts;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class PostItemViewHolder extends RecyclerView.ViewHolder {
    public final CardView cardView;
    public final TextView text, tags;
    public final ImageButton comments, delete, toggleRead, author;

    public PostItemViewHolder(View itemView) {
        super(itemView);
        this.cardView = (CardView) itemView.findViewById(R.id.card_view);
        this.text = (TextView) itemView.findViewById(R.id.comment_text);
        this.tags = (TextView) itemView.findViewById(R.id.author);
        this.comments = (ImageButton) itemView.findViewById(R.id.story_action_comments);
        this.delete = (ImageButton) itemView.findViewById(R.id.story_action_delete);
        this.toggleRead = (ImageButton) itemView.findViewById(R.id.story_action_read);
        this.author = (ImageButton) itemView.findViewById(R.id.story_action_author);
    }
}
