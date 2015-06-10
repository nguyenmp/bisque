package ninja.mpnguyen.bisque;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    private static class ListItem {
        public final String title;
        public final int iconResource;

        private ListItem(String title, int iconResource) {
            this.title = title;
            this.iconResource = iconResource;
        }
    }

    private final ListItem[] items = new ListItem[] {
        new ListItem("Profile", R.drawable.ic_action_account_box_black_no_padding),
        new ListItem("Messages", R.drawable.ic_communication_email_black_no_padding),
        new ListItem("Filters", R.drawable.ic_content_filter_list_black_no_padding),
        new ListItem("Deleted", R.drawable.ic_action_delete_black_no_padding),
        new ListItem("Settings", R.drawable.ic_action_settings_black_no_padding),
        new ListItem("Feedback", R.drawable.ic_action_question_answer_black_no_padding),
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_navigation_drawer1, container, false);

        ListView mDrawerListView = (ListView) contentView.findViewById(R.id.nav_list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(new NavAdapter(items));
        return contentView;
    }

    private static class NavAdapter extends BaseAdapter {
        private final ListItem[] items;

        public NavAdapter(ListItem[] items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public ListItem getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItem item = items[position];
            Context context = parent.getContext();
            LayoutInflater li = LayoutInflater.from(context);
            View view = li.inflate(R.layout.list_item_nav, parent, false);

            ImageView icon = (ImageView) view.findViewById(R.id.list_item_nav_icon);
            icon.setImageResource(item.iconResource);

            TextView text = (TextView) view.findViewById(R.id.list_item_nav_text);
            text.setText(item.title);

            return view;
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

    private void selectItem(int position) {

    }

}
