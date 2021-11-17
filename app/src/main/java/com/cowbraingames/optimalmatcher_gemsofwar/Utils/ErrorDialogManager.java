package com.cowbraingames.optimalmatcher_gemsofwar.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cowbraingames.optimalmatcher_gemsofwar.R;

public class ErrorDialogManager {

    public static void showErrorDialog(Context context, int titleResource, int descriptionResource) {
        final Dialog errorDialog = new Dialog(context);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.setCancelable(true);
        errorDialog.setContentView(R.layout.error_dialog);

        final TextView title = errorDialog.findViewById(R.id.error_dialog_title);
        final TextView description = errorDialog.findViewById(R.id.error_dialog_description);
        final Button okButton = errorDialog.findViewById(R.id.error_dialog_ok_button);

        title.setText(titleResource);
        description.setText(descriptionResource);
        okButton.setOnClickListener(view -> errorDialog.dismiss());

        errorDialog.show();
    }
}
