package ninja.mpnguyen.bisque.views.posts;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.SQLException;

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

    public static void bindListItem(final PostItemViewHolder holder, final MetaDataedPost metaDataedPost) {
        final PostMetadata metadata = metaDataedPost.metadata;
        final Post post = metaDataedPost.post;

        Context context = holder.itemView.getContext();
        Resources resources = context.getResources();

        int oldTitleColor = holder.vh.title.getCurrentTextColor();
        int titleColorRes = metadata.read ? R.color.story_title_read : R.color.story_title_unread;
        int newTitleColor = resources.getColor(titleColorRes);
        ValueAnimator titleColorAnimator = ObjectAnimator.ofInt(holder.vh.title, "textColor", oldTitleColor, newTitleColor);
        titleColorAnimator.setEvaluator(new ArgbEvaluator());
        titleColorAnimator.setDuration(resources.getInteger(R.integer.posts_read_animation_duration));
        titleColorAnimator.start();

        // Animate color of card background
        int oldCardColor = ((ColorDrawable) holder.vh.container.getBackground()).getColor();
        int cardColorRes = metadata.read ? R.color.background_card_read : R.color.background_card_unread;
        int newCardColor = resources.getColor(cardColorRes);
        ValueAnimator cardColorAnimator = ObjectAnimator.ofInt(holder.vh.container, "backgroundColor", oldCardColor, newCardColor);
        cardColorAnimator.setEvaluator(new ArgbEvaluator());
        cardColorAnimator.setDuration(resources.getInteger(R.integer.posts_read_animation_duration));
        cardColorAnimator.start();

        // Animate card elevation
        float start = holder.vh.cardView.getElevation();
        float end = metadata.read ? 4 : 12;
        ObjectAnimator elevation = ObjectAnimator.ofFloat(holder.vh.cardView, "elevation", start, end);
        elevation.setDuration(resources.getInteger(R.integer.posts_read_animation_duration));
        elevation.start();

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
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String[] tags = post.tags;
        if (tags != null && tags.length > 0) {
            builder.append(tags[0]);
            builder.setSpan(new TopicSpan(context, tags[0]), 0, builder.length(), 0);
        }

        for (int i = 1; tags != null && i < tags.length; i++) {
            String tag = tags[i];
            builder.append(" ").append(tag);
            int start = builder.length() - tag.length();
            int end = builder.length();
            builder.setSpan(new TopicSpan(context, tag), start, end, 0);
        }
        holder.tags.setText(builder);

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
                StoryActivity.showPost(context, post, true);
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

    private static class TopicSpan extends DynamicDrawableSpan {
        private final Context context;
        private final String topic;
        private Rect bounds = new Rect(0, 0, 200, 200);

        public TopicSpan(Context context, String topic) {
            super();
            this.context = context;
            this.topic = topic;
            new Paint.FontMetrics();
        }

        @Override
        public Drawable getDrawable() {
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.tag_topic, context.getTheme());
            drawable.setBounds(bounds);
            return drawable;
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            paint.getTextBounds(text.toString(), start, end, bounds);
            bounds.bottom = -1;
            bounds.top = -64;
            return super.getSize(paint, text, start, end, fm);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            paint.getTextBounds(text.toString(), start, end, bounds);
            bounds.bottom = -1;
            bounds.top = -64;
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
            canvas.drawText(text, start, end, x, y, paint);
        }
    }
}
