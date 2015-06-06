package ninja.mpnguyen.bisque.views.posts;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PostItemViewHolder extends RecyclerView.ViewHolder {
    public final PostViewHolder vh;

    public PostItemViewHolder(View itemView) {
        super(itemView);
        vh = new PostViewHolder(itemView);
    }
}
