package ninja.mpnguyen.bisque;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

import ninja.mpnguyen.bisque.databases.AccountsHelper;
import ninja.mpnguyen.bisque.fragments.LoginDialogFragment;
import ninja.mpnguyen.bisque.views.accounts.ActiveAccountPresenter;
import ninja.mpnguyen.bisque.views.accounts.ActiveAccountViewHolder;
import ninja.mpnguyen.bisque.views.nav_item.NavItemPresenter;
import ninja.mpnguyen.bisque.views.nav_item.NavItemViewHolder;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    public static class NavItem {
        public final String title;
        public final int iconResource;

        private NavItem(String title, int iconResource) {
            this.title = title;
            this.iconResource = iconResource;
        }
    }

    private final NavItem[] items = new NavItem[] {
        new NavItem("Profile", R.drawable.ic_action_account_box_black_no_padding),
        new NavItem("Messages", R.drawable.ic_communication_email_black_no_padding),
        new NavItem("Filters", R.drawable.ic_content_filter_list_black_no_padding),
        new NavItem("Deleted", R.drawable.ic_action_delete_black_no_padding),
        new NavItem("Settings", R.drawable.ic_action_settings_black_no_padding),
        new NavItem("Feedback", R.drawable.ic_action_question_answer_black_no_padding),
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_navigation_drawer1, container, false);

        RecyclerView mDrawerListView = (RecyclerView) contentView.findViewById(R.id.nav_list);
        Context context = mDrawerListView.getContext();
        int orientation = LinearLayoutManager.VERTICAL;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, orientation, false);
        mDrawerListView.setLayoutManager(layoutManager);
        mDrawerListView.setAdapter(new NavAdapter(new ActiveAccountClickedListener(this), items));
        return contentView;
    }

    private static class NavAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int ITEM_VIEW_TYPE_ACTIVE_ACCOUNT = 0, ITEM_VIEW_TYPE_NAV_ITEM = 1;
        private final ActiveAccountClickedListener accountListener;
        private final NavItem[] items;

        public NavAdapter(ActiveAccountClickedListener accountListener, NavItem[] items) {
            this.accountListener = accountListener;
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            if (viewType == ITEM_VIEW_TYPE_ACTIVE_ACCOUNT) {
                return ActiveAccountPresenter.inflateView(inflater, parent);
            } else if (viewType == ITEM_VIEW_TYPE_NAV_ITEM) {
                return NavItemPresenter.inflateView(inflater, parent);
            } else {
                throw new UnsupportedOperationException("NavAdapter does " +
                        "not currently support this view type:" + viewType);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == ITEM_VIEW_TYPE_ACTIVE_ACCOUNT) {
                onBindAccountViewHolder((ActiveAccountViewHolder) holder);
            } else if (viewType == ITEM_VIEW_TYPE_NAV_ITEM) {
                NavItem navItem = items[position - 1];
                onBindNavItemViewHolder((NavItemViewHolder) holder, navItem);
            } else {
                throw new UnsupportedOperationException("NavAdapter does " +
                        "not currently support this view type:" + viewType);
            }
        }

        private void onBindNavItemViewHolder(NavItemViewHolder vh, NavItem navItem) {
            NavItemPresenter.bindView(vh, navItem);
        }

        public void onBindAccountViewHolder(ActiveAccountViewHolder vh) {
            Context context = vh.itemView.getContext();
            try {
                AccountsHelper.Account account = AccountsHelper.getActiveAccount(context);
                ActiveAccountPresenter.bindView(vh, account, accountListener);
            } catch (SQLException e) {
                e.printStackTrace();
                // TODO: Not sure what to do here. Probably just log and adjust from there.
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return ITEM_VIEW_TYPE_ACTIVE_ACCOUNT;
            else return ITEM_VIEW_TYPE_NAV_ITEM;
        }

        @Override
        public int getItemCount() {
            return items.length + 1;
        }
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's items view with items and click listener
    }

    private static class ActiveAccountClickedListener implements ActiveAccountPresenter.OnClickListener {
        private final WeakReference<Fragment> fRef;

        private ActiveAccountClickedListener(Fragment f) {
            this.fRef = new WeakReference<>(f);
        }

        @Override
        public void onActiveAccountClicked(AccountsHelper.Account account) {
            Fragment f = fRef.get();
            if (f == null) return;

            FragmentManager fm = f.getChildFragmentManager();
            LoginDialogFragment loginDialogFragment = LoginDialogFragment.newInstance();
            loginDialogFragment.show(fm, null);
        }
    }
}
