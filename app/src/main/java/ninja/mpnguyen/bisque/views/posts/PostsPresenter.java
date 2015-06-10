package ninja.mpnguyen.bisque.views.posts;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.activities.StoryActivity;
import ninja.mpnguyen.bisque.activities.UserActivity;
import ninja.mpnguyen.bisque.things.MetaDataedPost;
import ninja.mpnguyen.bisque.things.PostMetadata;
import ninja.mpnguyen.chowders.things.json.Post;

public class PostsPresenter {
    public static PostItemViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.list_item_post, viewGroup, false);
        return new PostItemViewHolder(postView);
    }

    public static void bindListItem(final PostItemViewHolder holder, final MetaDataedPost metaDataedPost) {
        final PostMetadata metadata = metaDataedPost.metadata;
        final Post post = metaDataedPost.post;

        boolean animate = post.short_id.equals(holder.vh.short_id);

        Context context = holder.itemView.getContext();
        Resources resources = context.getResources();

        if (animate) {
            int oldTitleColor = holder.vh.title.getCurrentTextColor();
            int titleColorRes = metadata.read ? R.color.story_title_read : R.color.story_title_unread;
            int newTitleColor = resources.getColor(titleColorRes);
            ValueAnimator titleColorAnimator = ObjectAnimator.ofInt(holder.vh.title, "textColor", oldTitleColor, newTitleColor);
            titleColorAnimator.setEvaluator(new ArgbEvaluator());
            titleColorAnimator.setDuration(resources.getInteger(R.integer.posts_read_animation_duration));
            titleColorAnimator.start();
        } else {
            int titleColorRes = metadata.read ? R.color.story_title_read : R.color.story_title_unread;
            int newTitleColor = resources.getColor(titleColorRes);
            holder.vh.title.setTextColor(newTitleColor);
        }

        if (animate) {
            // Animate color of card background
            int oldCardColor = ((ColorDrawable) holder.vh.container.getBackground()).getColor();
            int cardColorRes = metadata.read ? R.color.background_card_read : R.color.background_card_unread;
            int newCardColor = resources.getColor(cardColorRes);
            ValueAnimator cardColorAnimator = ObjectAnimator.ofInt(holder.vh.container, "backgroundColor", oldCardColor, newCardColor);
            cardColorAnimator.setEvaluator(new ArgbEvaluator());
            cardColorAnimator.setDuration(resources.getInteger(R.integer.posts_read_animation_duration));
            cardColorAnimator.start();
        } else {
            int cardColorRes = metadata.read ? R.color.background_card_read : R.color.background_card_unread;
            int newCardColor = resources.getColor(cardColorRes);
            holder.vh.container.setBackgroundColor(newCardColor);
        }

        if (animate) {
            // Animate card elevation
            float start = holder.vh.cardView.getElevation();
            float end = metadata.read ? 4 : 12;
            ObjectAnimator elevation = ObjectAnimator.ofFloat(holder.vh.cardView, "elevation", start, end);
            elevation.setDuration(resources.getInteger(R.integer.posts_read_animation_duration));
            elevation.start();
        } else {
            float end = metadata.read ? 4 : 12;
            holder.vh.cardView.setElevation(end);
        }

        bindItem(holder.vh, metaDataedPost);
        holder.vh.short_id = metadata.short_id;
    }

    public static PostViewHolder inflateItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.view_post, viewGroup, false);
        return new PostViewHolder(postView);
    }

    public static void bindItem(PostViewHolder holder, final MetaDataedPost metaDataedPost) {
        final PostMetadata metadata = metaDataedPost.metadata;
        final Post post = metaDataedPost.post;

        Context context = holder.itemView.getContext();
        Resources r = context.getResources();

        holder.title.setText(post.title);
        SpannableStringBuilder tagsBuilder = new SpannableStringBuilder();
        BackgroundColorSpan tagSpan = new BackgroundColorSpan(r.getColor(R.color.transparent));
        if (post.tags != null && post.tags.length > 0) {
            tagsBuilder.append(post.tags[0]);
            tagsBuilder.setSpan(tagSpan, 0, tagsBuilder.length(), 0);
        }
        for (int i = 1; post.tags != null && i < post.tags.length; i++) {
            tagsBuilder.append(" ").append(post.tags[i]);
            tagsBuilder.setSpan(tagSpan, tagsBuilder.length() - post.tags[i].length(), tagsBuilder.length(), 0);
        }
        holder.tags.setText(tagsBuilder);

        String authorship = context.getString(R.string.by_x, post.submitter_user.username);
        String commentCount = context.getString(R.string.x_comments, post.comment_count);
        String subheading = String.format("%s with %s", authorship, commentCount);
        SpannableStringBuilder builder = new SpannableStringBuilder(subheading);
        int newComments = post.comment_count - metadata.last_read_comment_count;
        if (newComments > 0 && metadata.read) {
            int start = builder.length();
            builder.append(" ").append(context.getString(R.string.new_comments, newComments));
            int end = builder.length();
            builder.setSpan(new ForegroundColorSpan(Color.RED), start, end, 0);
        }
        holder.subheading.setText(builder);

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
                StoryActivity.showPost(context, post, true);
            }
        });

        holder.action_toggle_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetaDataedPost.markAsRead(!metadata.read, metaDataedPost, v.getContext());
            }
        });
    }
}
