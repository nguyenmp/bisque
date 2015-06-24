package ninja.mpnguyen.bisque.views.comments;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.things.CommentMetadataWrapper;
import ninja.mpnguyen.chowders.things.json.Comment;

public class HiddenCommentPresenter {
    public static HiddenCommentViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.list_item_hidden_comment, viewGroup, false);
        return new HiddenCommentViewHolder(v);
    }

    public static void bindListItem(HiddenCommentViewHolder vh, CommentMetadataWrapper wrapper) {
        Comment comment = wrapper.comment;
        String author = comment.commenting_user.username;
        int points = comment.score;

        ViewGroup.LayoutParams layoutParams = vh.padding.getLayoutParams();
        layoutParams.width = (comment.indent_level - 1) * 40;
        vh.padding.setLayoutParams(layoutParams);

        vh.author.setText(author + " ");
        Context c = vh.itemView.getContext();
        Resources rs = c.getResources();
        vh.points.setText(rs.getQuantityString(R.plurals.x_points, points, points));
    }
}
