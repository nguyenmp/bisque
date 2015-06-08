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
import ninja.mpnguyen.bisque.activities.StoryActivity;
import ninja.mpnguyen.bisque.activities.UserActivity;
import ninja.mpnguyen.bisque.databases.PostHelper;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.bisque.things.PostMetadata;
import ninja.mpnguyen.chowders.things.json.Post;

public class PostsPresenter {
    public static PostItemViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.list_item_post, viewGroup, false);
        return new PostItemViewHolder(postView);
    }

    public static void bindListItem(PostItemViewHolder holder, final MetaDataedPost metaDataedPost) {
        final PostMetadata metadata = metaDataedPost.metadata;
        final Post post = metaDataedPost.post;

        Context context = holder.itemView.getContext();
        Resources resources = context.getResources();

        int titleColorRes = metadata.read ? R.color.story_title_read : R.color.story_title_unread;
        int newTitleColor = resources.getColor(titleColorRes);
        final TextView title = holder.vh.title;
        title.setTextColor(newTitleColor);

        int cardColorRes = metadata.read ? R.color.background_card_read : R.color.background_card_unread;
        int newCardColor = resources.getColor(cardColorRes);
        holder.vh.container.setBackgroundColor(newCardColor);

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

        int toggleReadResource = metadata.read ? R.drawable.ic_action_visibility_off_gray : R.drawable.ic_action_visibility_gray;
        holder.action_toggle_read.setImageResource(toggleReadResource);

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

        holder.action_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (context == null) return;
                StoryActivity.showPost(context, post);
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
