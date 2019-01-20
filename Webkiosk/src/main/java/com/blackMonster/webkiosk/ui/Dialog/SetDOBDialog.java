package com.blackMonster.webkiosk.ui.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkioskApp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Akshansh on 9/3/2015.
 */
public class SetDOBDialog {


    public static void show(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View myView = activity.getLayoutInflater().inflate(
                R.layout.dialog_set_dob, null);

        builder.setView(myView);


        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        String dob = ((EditText) myView
                                .findViewById(R.id.set_dob_dialog_editbox))
                                .getEditableText().toString().trim();
                        Pattern pattern = Pattern.compile("\\d\\d-\\d\\d-\\d\\d\\d\\d");
                        Matcher matcher = pattern.matcher(dob);
                        if (!matcher.find()){
                            Toast.makeText(activity, activity.getString(R.string.invalid_dob_format),
                                    Toast.LENGTH_LONG).show();
                            show(activity);
                        } else {
                            MainPrefs.setDOB(dob, activity);
                            Toast.makeText(
                                    activity,
                                    activity.getResources()
                                            .getString(
                                                    R.string.dob_set_successfully),
                                    Toast.LENGTH_SHORT).show();
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
