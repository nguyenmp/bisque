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
    public interface OnClickListener {
        void onActiveAccountClicked(AccountsHelper.Account account);
    }

    public static ActiveAccountViewHolder inflateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_active_account, parent, false);
        return new ActiveAccountViewHolder(view);
    }

    public static void bindView(@NonNull ActiveAccountViewHolder vh, @Nullable AccountsHelper.Account account, @Nullable OnClickListener listener) {
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

        vh.container.setOnClickListener(new InternalOnClickListener(listener, account));
    }

    private static class InternalOnClickListener implements View.OnClickListener {
        private final OnClickListener listener;
        private final AccountsHelper.Account account;

        private InternalOnClickListener(OnClickListener listener, AccountsHelper.Account account) {
            this.listener = listener;
            this.account = account;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) listener.onActiveAccountClicked(account);
        }
    }
}
