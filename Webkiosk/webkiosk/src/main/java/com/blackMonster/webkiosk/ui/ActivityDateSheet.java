package com.blackMonster.webkiosk.ui;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;
import com.blackMonster.webkiosk.databases.Tables.DSSPTable;
import com.blackMonster.webkiosk.ui.adapters.DatesheetAdapter;
import com.blackMonster.webkioskApp.R;

public class ActivityDateSheet extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        M.log("ActivityDatesheet", "oncreate");
        initActionBar();
        showListView();
    }

    private void showListView() {

        activityContent.removeAllViews();
        activityContent.setPadding(dpToPx(10), 0, dpToPx(10), 0);
        activityContent.setBackgroundColor(Color.parseColor("#E9EAED"));

        DatesheetAdapter adapter = new DatesheetAdapter(this, DSSPTable.getDS(this));
        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        activityContent.addView(listView);


    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Date Sheet");
        openDrawerWithIcon(true);
    }


    @Override
    public void inflateOnCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (TimetableDbHelper.databaseExists(this))
            inflater.inflate(R.menu.menu_without_refresh, menu);
        else
            inflater.inflate(R.menu.mainmenu, menu);
    }


}
