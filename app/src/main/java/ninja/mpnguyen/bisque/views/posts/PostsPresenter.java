package ninja.mpnguyen.bisque.views.posts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.chowders.things.Post;

public class PostsPresenter {
    public static PostItemViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.list_item_post, viewGroup, false);
        return new PostItemViewHolder(postView);
    }

    public static void bindListItem(PostItemViewHolder holder, Post post) {
        holder.text.setText(post.title);
        holder.tags.setText(Arrays.toString(post.tags) + "    " + post.comment_count + " comments");
    }

    public static PostViewHolder inflateItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.view_post, viewGroup, false);
        return new PostViewHolder(postView);
    }

    public static void bindItem(PostViewHolder holder, Post post) {
        holder.text.setText(post.title);
        holder.tags.setText(Arrays.toString(post.tags) + "    " + post.comment_count + " comments");
    }
}
