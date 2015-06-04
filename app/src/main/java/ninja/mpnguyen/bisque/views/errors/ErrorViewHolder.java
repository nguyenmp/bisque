package ninja.mpnguyen.bisque.views.errors;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class ErrorViewHolder extends RecyclerView.ViewHolder{
    public final TextView errorView;

    public ErrorViewHolder(TextView errorView) {
        super(errorView);
        this.errorView = errorView;
    }
}
