package com.example.cronogramacapilar.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.cronogramacapilar.R;

import java.util.concurrent.Callable;

public class DeleteTreatmentWithConfirm {

    public static void deleteTreatment(final Callable<Void> callback, final long id, final Context context) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        new TreatmentDaoAsync.DeleteTreatmentAsync(callback).execute(id);
                        NotificationHelper.cancelNotification(id, context);
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setMessage(context.getString(R.string.confirm_delete_message)).setPositiveButton(context.getString(R.string.delete), dialogClickListener)
                .setNegativeButton(context.getString(R.string.cancel), dialogClickListener).show();
    }

}
