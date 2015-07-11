package ninja.mpnguyen.bisque.views.accounts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.R;
import ninja.mpnguyen.bisque.databases.AccountsHelper;

public class ActiveAccountPresenter {
    public static ActiveAccountViewHolder inflateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_active_account, parent, false);
        return new ActiveAccountViewHolder(view);
    }

    public static void bindView(@NonNull ActiveAccountViewHolder vh, @Nullable AccountsHelper.Account account) {
        if (account == null) {
            Context context = vh.itemView.getContext();
            String anonymous_username = context.getString(R.string.not_logged_in_username);
            String anonymous_email = context.getString(R.string.not_logged_in_email);
            vh.usernameView.setText(anonymous_username);
            vh.emailView.setText(anonymous_email);
        } else {
            vh.usernameView.setText(account.username);
            vh.emailView.setText(account.email);
        }
    }
}
