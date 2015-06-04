package ninja.mpnguyen.bisque.views.posts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public final TextView text, tags;

    public PostViewHolder(View itemView) {
        super(itemView);
        this.text = (TextView) itemView.findViewById(R.id.comment_text);
        this.tags = (TextView) itemView.findViewById(R.id.author);
    }
}
