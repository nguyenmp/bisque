package ninja.mpnguyen.bisque.views.nav_item;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.NavigationDrawerFragment;
import ninja.mpnguyen.bisque.R;

public class NavItemPresenter {
    public static NavItemViewHolder inflateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item_nav, parent, false);
        return new NavItemViewHolder(view);
    }

    public static void bindView(@NonNull NavItemViewHolder vh, @NonNull NavigationDrawerFragment.NavItem navItem) {
        vh.icon.setImageResource(navItem.iconResource);
        vh.label.setText(navItem.title);
    }
}
