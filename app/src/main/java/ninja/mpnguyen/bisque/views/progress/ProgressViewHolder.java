package ninja.mpnguyen.bisque.views.progress;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import ninja.mpnguyen.bisque.R;

public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public final ProgressBar progressBar;
    public final TextView loadingText;

    public ProgressViewHolder(View itemView) {
        super(itemView);
        this.progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        this.loadingText = (TextView) itemView.findViewById(R.id.loading);
    }
}
