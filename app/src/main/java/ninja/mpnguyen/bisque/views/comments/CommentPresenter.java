package ninja.mpnguyen.bisque.views.comments;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.chowders.things.Comment;

public class CommentPresenter {
    public static CommentViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.list_item_comment, viewGroup, false);
        return new CommentViewHolder(postView);
    }

    public static void bindListItem(CommentViewHolder holder, Comment comment) {
        holder.text.setText(Html.fromHtml(comment.comment));
        holder.tags.setText(comment.commenting_user.username);
    }
}
