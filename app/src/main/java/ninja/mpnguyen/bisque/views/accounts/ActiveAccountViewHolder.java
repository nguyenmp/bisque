package ninja.mpnguyen.bisque.views.accounts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class ActiveAccountViewHolder extends RecyclerView.ViewHolder {
    public final TextView emailView, usernameView;
    public final View container;

    public ActiveAccountViewHolder(View itemView) {
        super(itemView);
        emailView = (TextView) itemView.findViewById(R.id.active_account_email);
        usernameView = (TextView) itemView.findViewById(R.id.active_account_username);
        container = itemView.findViewById(R.id.active_account_container);
    }
}
