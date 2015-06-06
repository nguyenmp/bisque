package ninja.mpnguyen.bisque.views.user;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.okhttp.HttpUrl;
import com.squareup.picasso.Picasso;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.chowders.things.json.User;

public class UserPresenter {

    public static UserViewHolder inflateItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.view_user, viewGroup, false);
        return new UserViewHolder(postView);
    }

    public static void bindItem(UserViewHolder holder, User user) {
        Context context = holder.itemView.getContext();
        HttpUrl url = HttpUrl.parse(user.avatar_url)
                .newBuilder()
                .setQueryParameter("s", "200")
                .setQueryParameter("size", "200")
                .build();

        Drawable placeholder = context.getDrawable(R.drawable.black_woman_sillouette);
        Picasso.with(context).load(url.toString()).placeholder(placeholder).into(holder.profile_picture);
        holder.username.setText(String.format("/u/%s", user.username));
        holder.karma.setText(String.format("Karma: %d", user.karma));
        holder.about.setText(Html.fromHtml(user.about));
        holder.joined.setText(user.created_at);
        holder.status.setText(Boolean.toString(user.is_moderator));
    }
}
