package com.blackMonster.webkiosk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.blackMonster.notifications.NotificationManager;
import com.blackMonster.webkiosk.SharedPrefs.RefreshBroadcasts;
import com.blackMonster.webkiosk.SharedPrefs.RefreshStatus;
import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.controller.RefreshFullDB;
import com.blackMonster.webkiosk.crawler.LoginStatus;
import com.blackMonster.webkiosk.services.ServiceRefreshAll;
import com.blackMonster.webkiosk.ui.adapters.BaseDrawerAdapter;
import com.blackMonster.webkiosk.utils.NetworkUtils;
import com.blackMonster.webkioskApp.R;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;

public class BaseActivity extends ActionBarActivity {
    private String TAG = "BaseActivity";
    public LinearLayout activityContent = null; //Any activity extending BaseActivity will fill put all it's content here.
    public boolean isReceiverRegistered = false;

    private boolean openDrawerWithIcon = true; //Sets if drawer could be opened with tap on icon on top left.
    private boolean isOptionsMenuLoaded = false;
    private boolean showCirularProgressBar = false;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean isRefreshBtnAnimated = false;
    private Menu actionbarMenu = null;
    private BaseDrawerAdapter drawerAdapter;


    /**
     * Received when notification is published by developers.
     */
    private BroadcastReceiver broadcastNotificationResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            M.log(TAG, "received : broadcastNotificationResult ");

            int result = intent.getExtras().getInt(
                    NotificationManager.BROADCAST_NOTIFICATION_UPDATE_RESULT);
            M.log(TAG, result + "");
            if (result != NotificationManager.NOTIFICATION_NO_CHANGE) updateDrawer();
        }


    };


    private BroadcastReceiver broadcastLoginResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // M.log(TAG, "received : broadcastLoginResult " + getClassName());
            int result = intent.getExtras().getInt(
                    RefreshBroadcasts.BROADCAST_LOGIN_RESULT);
            if (result == LoginStatus.LOGIN_DONE) {
                // loginResultMessege();

            } else {
                unanimateRefreshButton();
                if (result == LoginStatus.INVALID_PASS
                        || result == LoginStatus.ACCOUNT_LOCKED)
                    AlertDialogHandler.showChangePasswordDialog(BaseActivity.this);
                else
                    AlertDialogHandler.checkDialog(BaseActivity.this);

            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        activityContent = (LinearLayout) findViewById(R.id.act_content);
        initActionbar();
    }

    private void initActionbar() {
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.theme)));
        getSupportActionBar().setLogo(
                getResources().getDrawable(R.drawable.ic_logo));
    }

    /**
     * Initialises drawer(one we get from swiping from left edge of screen).
     */
    private void initDrawer() {
        ArrayList<String> drawerList = getDrawerList();

        drawerAdapter = new BaseDrawerAdapter(this, R.layout.drawer_row,
                drawerList);
        drawerAdapter.setNotifyOnChange(true);

        ListView listView = ((ListView) findViewById(R.id.left_drawer));
        listView.setAdapter(drawerAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {

                    case 0:
                        intent = new Intent(BaseActivity.this, TimetableActivity.class);
                        if (StartupActivity.isStartupActivity(TimetableActivity.class,
                                getBaseContext()))
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(BaseActivity.this,
                                AtndOverviewActivity.class);
                        if (StartupActivity.isStartupActivity(
                                AtndOverviewActivity.class, getBaseContext()))
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        break;
                    case 2:
                        if (NetworkUtils.isInternetAvailable(getBaseContext())) {
                            startActivity(new Intent(BaseActivity.this,
                                    WebViewActivity.class));
                        } else
                            Toast.makeText(getBaseContext(), getString(R.string.con_error),
                                    Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        startActivity(new Intent(BaseActivity.this,
                                ActivityDateSheet.class));
                        break;

                    case 4:
                        startNotificationActivity();
                        break;

                }
                if (!getClassName().equals(
                        StartupActivity.getStartupActivity(getBaseContext())
                                .getSimpleName()))
                    finish();

            }
        });



        DrawerLayout mDrawerLayout;
        if (openDrawerWithIcon) {

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                    mDrawerLayout, /* DrawerLayout object */
                    R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                    R.string.drawer_open, /* "open drawer" description */
                    R.string.drawer_close /* "close drawer" description */
            ) {

            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }
    }

    private ArrayList<String> getDrawerList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(getString(R.string.timetable_view));
        list.add(getString(R.string.attendance_view));
        list.add(getString(R.string.web_view));
        list.add(getString(R.string.datesheet_view));
        if (NotificationManager.isNotificationAvailable(this))
            list.add(getString(R.string.notification_view));
        return list;
    }

    private void updateDrawer() {
        drawerAdapter.clear();
        ArrayList<String> list = getDrawerList();

        for (String str : list) {
            drawerAdapter.add(str);
        }
    }

    public void openDrawerWithIcon(boolean b) {
        openDrawerWithIcon = b;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initDrawer();


        if (openDrawerWithIcon)
            mDrawerToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (openDrawerWithIcon)
            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void startNotificationActivity() {
        if (NetworkUtils.isInternetAvailable(this)) {
            startActivity(new Intent(BaseActivity.this,
                    ActivityNotification.class));
            setNotificationAlertVisibilisty(false);
        } else
            Toast.makeText(this, getString(R.string.con_error),
                    Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflateOnCreateOptionsMenu(menu);
        actionbarMenu = menu;

        isOptionsMenuLoaded = true;
        if (showCirularProgressBar) {
            animateRefreshButton();
        }

        return true;
    }

    public void inflateOnCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (openDrawerWithIcon) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        return switchForOnOptionsItemSelected(item.getItemId());

    }

    boolean switchForOnOptionsItemSelected(int itemId) {
        switch (itemId) {
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_settings:
                if (showToastIfRefreshing())
                    return true;
                startActivity(new Intent(this, PrefsActivity.class));
                return true;
            case R.id.action_report_problem:
                startActivity(new Intent(this, ActivityCustomerSupport.class));
                return true;

            default:
                return false;
        }
    }

    private boolean showToastIfRefreshing() {
        if (RefreshDBPrefs.isRunning(this)) {
            Toast.makeText(this, getString(R.string.refresh_takingplace),
                    Toast.LENGTH_SHORT).show();
            return true;

        } else
            return false;

    }

    private void refresh() {
        RefreshDBPrefs.resetIfrunningFromLongTime(this);
        if (RefreshDBPrefs.isRunning(this)) {
            Toast.makeText(BaseActivity.this,
                    RefreshStatus.getStatusMessage(this),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        animateRefreshButton();
        registerReceivers();
        Intent intent = ServiceRefreshAll.getIntent(RefreshFullDB.MANUAL_REFRESH, this);
        startService(intent);
    }

    /**
     * Register broadcast receivers to be used at the time of refreshing database.
     */
    public void registerReceivers() {
        if (!isReceiverRegistered) {

            LocalBroadcastManager
                    .getInstance(this)
                    .registerReceiver(
                            broadcastLoginResult,
                            new IntentFilter(
                                    RefreshBroadcasts.BROADCAST_LOGIN_RESULT));

            LocalBroadcastManager
                    .getInstance(this)
                    .registerReceiver(
                            broadcastNotificationResult,
                            new IntentFilter(
                                    NotificationManager.BROADCAST_NOTIFICATION_UPDATE_RESULT));
            isReceiverRegistered = true;
        }
    }

    public void unregisterReceivers() {
        M.log(TAG, "subperclass unregister receiver");

        if (isReceiverRegistered) {
            // M.log(TAG, "unregistered broadcast login result");
            LocalBroadcastManager.getInstance(BaseActivity.this)
                    .unregisterReceiver(broadcastLoginResult);
            LocalBroadcastManager.getInstance(BaseActivity.this)
                    .unregisterReceiver(broadcastNotificationResult);
            isReceiverRegistered = false;
        }
    }

    private void setNotificationAlertVisibilisty(boolean b) {
        int visibility;
        if (b) visibility = View.VISIBLE;
        else
            visibility = View.GONE;

        ListView listView = ((ListView) findViewById(R.id.left_drawer));
        //listView.getItemAtPosition(position)
        int pos = drawerAdapter.getPosition(getString(R.string.notification_view));
        if (pos != -1) {
            M.log(TAG, pos + "");
            ((ImageView) listView.getChildAt(pos).findViewById(R.id.drawer_alert)).setVisibility(visibility);
        }

    }

    public void unanimateRefreshButton() {
        if (isRefreshBtnAnimated) {
            MenuItemCompat.setActionView(
                    actionbarMenu.findItem(R.id.action_refresh), null);
            isRefreshBtnAnimated = false;
        }
    }

    /**
     * Replaces refresh button in action bar with circular progress bar.
     * If optionMenu and actionbar is not yet loaded a flag(showCircularProgressBar) is marked.
     * This flag is checked when everything loads.
     */
    public void animateRefreshButton() {
        if (isOptionsMenuLoaded) {
            if (actionbarMenu != null) {
                MenuItemCompat.setActionView(
                        actionbarMenu.findItem(R.id.action_refresh),
                        R.layout.progressbar);
                isRefreshBtnAnimated = true;

            }
        } else
            showCirularProgressBar = true;

    }

    private String getClassName() {
        return getClass().getSimpleName();
    }

    void putTimestampInSubtitle(long time) {
        getSupportActionBar().setSubtitle(
                "refreshed "
                        + DateUtils.getRelativeDateTimeString(this, time,
                        DateUtils.DAY_IN_MILLIS,
                        DateUtils.WEEK_IN_MILLIS,
                        DateUtils.FORMAT_NO_YEAR));
    }


    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this); //Google analytics
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this); //Google analytics

    }

}
