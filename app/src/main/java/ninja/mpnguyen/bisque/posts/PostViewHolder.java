package ninja.mpnguyen.bisque.posts;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public final CardView cardView;
    public final TextView text, tags;

    public PostViewHolder(View itemView) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.card_view);
        this.text = (TextView) itemView.findViewById(R.id.info_text);
        this.tags = (TextView) itemView.findViewById(R.id.tags);
    }
}
