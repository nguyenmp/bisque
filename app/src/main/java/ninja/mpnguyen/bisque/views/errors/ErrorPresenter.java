package ninja.mpnguyen.bisque.views.errors;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class ErrorPresenter {
    public static ErrorViewHolder inflateListItem(LayoutInflater inflater, ViewGroup viewGroup) {
        TextView errorView = (TextView) inflater.inflate(R.layout.list_item_feedback, viewGroup, false);
        return new ErrorViewHolder(errorView);
    }

    public static void bindListItem(ErrorViewHolder holder, String message) {
        holder.errorView.setText(message);
    }
}
