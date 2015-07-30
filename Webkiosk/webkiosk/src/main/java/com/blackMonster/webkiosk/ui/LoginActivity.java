package com.blackMonster.webkiosk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blackMonster.webkiosk.controller.appLogin.CreateDatabase;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.crawler.LoginStatus;
import com.blackMonster.webkiosk.controller.appLogin.InitDB;
import com.blackMonster.webkiosk.controller.RefreshFullDB;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateAvgAtnd;
import com.blackMonster.webkiosk.controller.Timetable.TimetableCreateRefresh;
import com.blackMonster.webkiosk.WebkioskApp;
import com.blackMonster.webkiosk.services.ServiceAppLogin;
import com.blackMonster.webkiosk.services.ServiceRefreshAll;
import com.blackMonster.webkiosk.utils.NetworkUtils;
import com.blackMonster.webkioskApp.R;
import com.google.analytics.tracking.android.EasyTracker;


/**
 * Activity showing login form.
 */
public class LoginActivity extends ActionBarActivity implements
        OnItemSelectedListener {
    static final String TAG = "LoginActivity";
    AlertDialog dialog = null;
    String prefColg;
    boolean isRecreatingDatabase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.theme)));
        getSupportActionBar().setLogo(
                getResources().getDrawable(R.drawable.ic_logo));

        setContentView(R.layout.activity_login);
        initSpinner();
    }

    //TODO add log here
    private void startLogginIfRecreating() {
        isRecreatingDatabase = getIntent().getBooleanExtra(
                ServiceRefreshAll.RECREATING_DATABASE, false);
        if (isRecreatingDatabase)
            startLogin(MainPrefs.getColg(this),
                    MainPrefs.getEnroll(this), MainPrefs.getPassword(this),
                    MainPrefs.getBatch(this));

    }

    private void initSpinner() {
        createSimpleSpinner(R.id.colg_select, R.array.prefs_colg_name);
    }

    private void createSimpleSpinner(int spinnerId, int arrayId) {
        Spinner spinner = (Spinner) findViewById(spinnerId);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayId, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    /**
     * Called when login button pressed
     */
    public void buttonLogin(View v) {
        ((WebkioskApp) getApplication()).nullifyAllVariables();
        hideKeyboard();
        String enroll, pass, batch;

        enroll = ((EditText) findViewById(R.id.enroll_num)).getEditableText()
                .toString().trim();
        pass = ((EditText) findViewById(R.id.password)).getEditableText()
                .toString().trim();
        batch = ((EditText) findViewById(R.id.batch_select)).getEditableText()
                .toString().trim().toUpperCase();

        if (!isValidDetails(enroll, pass, batch))
            return;

        if (!NetworkUtils.isInternetAvailable(LoginActivity.this)) {
            Toast.makeText(this, getString(R.string.con_error),
                    Toast.LENGTH_SHORT).show();

        } else {
            startLogin(prefColg, enroll, pass, batch);
        }

    }

    private void startLogin(String colg, String enroll,
                            String pass, String batch) {

        startService(ServiceAppLogin.getIntent(colg, enroll, pass, batch, this));
        dialog = createProgressDialog(R.string.logging_in);
        dialog.show();
    }

    private boolean isValidDetails(String enroll, String pass, String batch) {
        if (enroll.equals("") || pass.equals("") || batch.equals("")) {
            return false;
        } else
            return true;
    }

    /**
     * Called when only server login is done.. i.e when college servers had accepeted login details.
     */
    private BroadcastReceiver broadcastLoginResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // M.log(TAG, "received : broadcastLoginResult");
            if (dialog != null)
                dialog.dismiss();
            dialog = null;
            int result;
            result = intent.getExtras().getInt(
                    RefreshFullDB.BROADCAST_LOGIN_RESULT);
            if (result == LoginStatus.LOGIN_DONE) {
                dialog = createProgressDialog(R.string.loading);
                dialog.show();
            } else {
                AlertDialogHandler.checkDialog(LoginActivity.this);
            }

        }

    };

    void manageProgressDialog() {
        if (RefreshDBPrefs.isStatus(RefreshDBPrefs.LOGGING_IN, this)) {
            dialog = createProgressDialog(R.string.logging_in);
            dialog.show();
        } else if (RefreshDBPrefs.isStatus(RefreshDBPrefs.REFRESHING_O, this) ||
                RefreshDBPrefs.isStatus(RefreshDBPrefs.CREATING_DB, this)) {
            dialog = createProgressDialog(R.string.loading);
            dialog.show();
        } else if (WebkioskApp.canViewAttendance(this)) {
            MainActivity.launchStartupActivity(this);
            finish();
        }
    }

    private BroadcastReceiver broadcastDatabaseCreationResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // M.log(TAG, "received : broadcastDatabaseCreationResult");

            int result;
            result = intent.getExtras().getInt(
                    InitDB.BROADCAST_DATEBASE_CREATION_RESULT);
            if (result == CreateDatabase.ERROR || TimetableCreateRefresh.isError(result)) {
                if (dialog != null)
                    dialog.dismiss();
                dialog = null;
                AlertDialogHandler.checkDialog(LoginActivity.this);
            }

        }

    };

    private BroadcastReceiver broadcastUpdateAvgAtndResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // M.log(TAG, "received : broadcastUpdateAvgAtndResult");

            if (dialog != null)
                dialog.dismiss();
            dialog = null;
            int result;
            result = intent.getExtras().getInt(
                    RefreshFullDB.BROADCAST_UPDATE_AVG_ATND_RESULT);
            if (result == UpdateAvgAtnd.ERROR) {
                AlertDialogHandler.checkDialog(LoginActivity.this);
            } else {
                MainActivity.launchStartupActivity(getActivity());
                getActivity().finish();
            }

        }
    };

    private Activity getActivity() {
        return this;
    }

    @Override
    protected void onPause() {
        // M.log(TAG, "onpause");
        // /LocalBroadcastManager.getInstance(this).unregisterReceiver(
        // / broadcastTimetableloadResult);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                broadcastLoginResult);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                broadcastDatabaseCreationResult);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                broadcastUpdateAvgAtndResult);

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;

        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastLoginResult,
                new IntentFilter(RefreshFullDB.BROADCAST_LOGIN_RESULT));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(
                        broadcastDatabaseCreationResult,
                        new IntentFilter(
                                InitDB.BROADCAST_DATEBASE_CREATION_RESULT));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(
                        broadcastUpdateAvgAtndResult,
                        new IntentFilter(
                                RefreshFullDB.BROADCAST_UPDATE_AVG_ATND_RESULT));

        // M.log(TAG, "resuming dialog");
        manageProgressDialog();
        AlertDialogHandler.checkDialog(this);
        startLogginIfRecreating();

    }

    private AlertDialog createProgressDialog(int msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View myView = getLayoutInflater().inflate(R.layout.login_progressbar,
                null);
        ((TextView) myView.findViewById(R.id.login_dialog_msg)).setText(msg);

        builder.setView(myView);
        builder.setCancelable(false);

        return builder.create();
    }

    //TODO utils
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = getCurrentFocus();
        if (focus != null)
            inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {
        Spinner spinner = (Spinner) parent;

        if (spinner.getId() == R.id.colg_select) {
            prefColg = getResources().getStringArray(R.array.prefs_colg_code)[pos];
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        prefColg = getResources().getStringArray(R.array.prefs_colg_code)[0];
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this); // Google analytics
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this); // Google analytics

    }

}
