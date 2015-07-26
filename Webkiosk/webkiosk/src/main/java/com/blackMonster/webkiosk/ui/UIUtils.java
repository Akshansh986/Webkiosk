package com.blackMonster.webkiosk.ui;

import android.content.Context;
import android.widget.ProgressBar;

import com.blackMonster.webkiosk.WebkioskApp;
import com.blackMonster.webkioskApp.R;

/**
 * Created by akshansh on 26/07/15.
 */
public class UIUtils {
    public static void setProgressBarColor(ProgressBar pb, Integer attendence,
                                           Context context) {
        if (attendence == null || attendence < 0) attendence = 0;

        if (attendence >= WebkioskApp.ATTENDENCE_GOOD)
            pb.setProgressDrawable(context.getResources().getDrawable(
                    R.drawable.custom_progressbar_green));
        else if (attendence >= WebkioskApp.ATTENDENCE_AVG)
            pb.setProgressDrawable(context.getResources().getDrawable(
                    R.drawable.custom_progressbar_orange));
        else
            pb.setProgressDrawable(context.getResources().getDrawable(
                    R.drawable.custom_progressbar_red));

    }
}
