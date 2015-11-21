package com.blackMonster.webkiosk.ui.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkioskApp.R;

/**
 * Created by Akshansh on 9/3/2015.
 */
public class ChangePasswordDialog {


    public static void show(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View myView = activity.getLayoutInflater().inflate(
                R.layout.dialog_change_pass, null);

        builder.setView(myView);


        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        String pass = ((EditText) myView
                                .findViewById(R.id.changepass_dialog_editbox))
                                .getEditableText().toString().trim();
                        if (pass.equals(""))
                            show(activity);
                        else {
                            MainPrefs.setPassword(pass, activity);
                            Toast.makeText(
                                    activity,
                                    activity.getResources()
                                            .getString(
                                                    R.string.password_changed_successfully),
                                    Toast.LENGTH_SHORT).show();
                            RefreshDBPrefs.setPasswordUptoDate(activity);

                        }
                    }

                });



        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }
}
