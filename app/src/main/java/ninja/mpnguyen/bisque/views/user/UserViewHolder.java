package ninja.mpnguyen.bisque.views.user;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import ninja.mpnguyen.bisque.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public final ImageView profile_picture;
    public final TextView username, joined, status, about, karma;
    public final FloatingActionButton fab;

    public UserViewHolder(View itemView) {
        super(itemView);
        this.profile_picture = (ImageView) itemView.findViewById(R.id.profile_picture);
        this.username = (TextView) itemView.findViewById(R.id.profile_username);
        this.joined = (TextView) itemView.findViewById(R.id.profile_joined);
        this.status = (TextView) itemView.findViewById(R.id.profile_status);
        this.about = (TextView) itemView.findViewById(R.id.profile_about);
        this.karma = (TextView) itemView.findViewById(R.id.profile_karma);
        this.fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
    }
}
