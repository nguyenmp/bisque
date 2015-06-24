package ninja.mpnguyen.bisque.views.comments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class HiddenCommentViewHolder extends RecyclerView.ViewHolder {
    public final TextView author;
    public final TextView points;
    public final View content, container, padding;

    public HiddenCommentViewHolder(View itemView) {
        super(itemView);

        padding = itemView.findViewById(R.id.hidden_comment_padding);
        author = (TextView) itemView.findViewById(R.id.hidden_comment_author);
        points = (TextView) itemView.findViewById(R.id.hidden_comment_points);
        content = itemView.findViewById(R.id.hidden_comment_content);
        container = itemView.findViewById(R.id.hidden_comment_container);
    }
}
