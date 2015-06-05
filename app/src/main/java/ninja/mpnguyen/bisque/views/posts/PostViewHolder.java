package ninja.mpnguyen.bisque.views.posts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public final PostItemViewHolder vh;

    public PostViewHolder(View itemView) {
        super(itemView);
        vh = new PostItemViewHolder(itemView);
    }
}
