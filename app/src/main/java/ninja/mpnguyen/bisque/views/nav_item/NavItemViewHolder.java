package ninja.mpnguyen.bisque.views.nav_item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class NavItemViewHolder extends RecyclerView.ViewHolder {
    public final ImageView icon;
    public final TextView label;
    public final View container;

    public NavItemViewHolder(View itemView) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.list_item_nav_icon);
        label = (TextView) itemView.findViewById(R.id.list_item_nav_text);
        container = itemView.findViewById(R.id.list_item_nav_container);
    }
}
