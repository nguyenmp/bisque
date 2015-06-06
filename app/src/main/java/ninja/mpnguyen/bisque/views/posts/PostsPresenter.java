package ninja.mpnguyen.bisque.views.posts;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Arrays;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.activities.UserActivity;
import ninja.mpnguyen.bisque.databases.PostHelper;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.bisque.things.PostMetadata;
import ninja.mpnguyen.chowders.things.Post;

public class PostsPresenter {
    public static PostItemViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.list_item_post, viewGroup, false);
        return new PostItemViewHolder(postView);
    }

    public static void bindListItem(PostItemViewHolder holder, final MetaDataedPost metaDataedPost) {
        final PostMetadata metadata = metaDataedPost.metadata;
        final Post post = metaDataedPost.post;

        int titleColorRes = metadata.read ? R.color.story_title_read : R.color.story_title_unread;
        Context context = holder.itemView.getContext();
        Resources resources = context.getResources();
        int newTitleColor = resources.getColor(titleColorRes);
        final TextView title = holder.vh.title;
        int originalTitleColor = title.getCurrentTextColor();
        title.setTextColor(newTitleColor);

        bindItem(holder.vh, metaDataedPost);
    }

    public static PostViewHolder inflateItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.view_post, viewGroup, false);
        return new PostViewHolder(postView);
    }

    public static void bindItem(PostViewHolder holder, final MetaDataedPost metaDataedPost) {
        final PostMetadata metadata = metaDataedPost.metadata;
        final Post post = metaDataedPost.post;

        Context context = holder.itemView.getContext();

        holder.title.setText(post.title);
        holder.tags.setText(Arrays.toString(post.tags));

        String authorship = context.getString(R.string.by_x, post.submitter_user.username);
        String commentCount = context.getString(R.string.x_comments, post.comment_count);
        String subheading = String.format("%s with %s", authorship, commentCount);
        holder.subheading.setText(subheading);

        holder.action_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra(UserActivity.EXTRA_USER, post.submitter_user);
                intent.putExtra(UserActivity.EXTRA_USERNAME, post.submitter_user.username);
                context.startActivity(intent);
            }
        });

        holder.action_toggle_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metadata.read = !metadata.read;
                try {
                    PostHelper.setMetadata(metadata, v.getContext());
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            }
        });
    }
}
