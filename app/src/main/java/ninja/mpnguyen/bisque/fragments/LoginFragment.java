package ninja.mpnguyen.bisque.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class LoginFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        new AlertDialog.Builder(getActivity())
                .setNegativeButton("Cancel", new CancelListener())
                .setPositiveButton("Login", null)
                .create();
    }

    private static class CancelListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    private static class LoginListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    }
}
