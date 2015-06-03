package ninja.mpnguyen.bisque.posts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.StoryActivity;
import ninja.mpnguyen.chowders.things.Post;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private final Post[] posts;

    public PostsAdapter(Post[] posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return PostPresenter.inflateListItem(inflater, viewGroup);
    }

    @Override
    public void onBindViewHolder(PostViewHolder postViewHolder, int position) {
        Post post = posts[position];
        View.OnClickListener listener = new PostItemClickListener(post);
        postViewHolder.cardView.setOnClickListener(listener);
        PostPresenter.bindListItem(postViewHolder, post);
    }

    private static class PostItemClickListener implements View.OnClickListener {
        private final Post post;

        private PostItemClickListener(Post post) {
            this.post = post;
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, StoryActivity.class);
            intent.putExtra(StoryActivity.EXTRA_POST, post);
            intent.setData(Uri.parse(post.comments_url));
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return posts.length;
    }
}
