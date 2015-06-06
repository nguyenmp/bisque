package ninja.mpnguyen.bisque.views.progress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ninja.mpnguyen.bisque.R;

public class ProgressPresenter {
    public static ProgressViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        View postView = inflater.inflate(R.layout.list_item_loading, viewGroup, false);
        return new ProgressViewHolder(postView);
    }
    public static void bindListItem(ProgressViewHolder viewHolder, String itemType) {
        Context context = viewHolder.loadingText.getContext();
        String message = context.getString(R.string.loading_x, itemType);
        viewHolder.loadingText.setText(message);
    }
}
