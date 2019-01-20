package com.blackMonster.webkiosk.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.controller.appLogin.InitDB;
import com.blackMonster.webkiosk.controller.RefreshFullDB;
import com.blackMonster.webkiosk.controller.updateAtnd.SubjectChangedException;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;

/**
 * Service for logging into app.
 */
public class ServiceAppLogin extends IntentService {
    public static final String TAG = "ServiceLogin";
    String enroll, pass, batch, colg, dob;

    public ServiceAppLogin() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initGlobalVariables(intent);

        InitDB initDB = new InitDB(enroll,pass,batch,colg,dob,this);
        if (initDB.start()) {
            CrawlerDelegate crawlerDelegate = initDB.getCrawlerDelegate();      //Instance of crawler used while initialising DB.

            try {
                new RefreshFullDB(RefreshFullDB.MANUAL_REFRESH,this).refresh(crawlerDelegate);
            } catch (SubjectChangedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initGlobalVariables(Intent intent) {
        colg = intent.getExtras().getString(MainPrefs.COLG);
        enroll = intent.getExtras().getString(MainPrefs.ENROLL_NO);
        pass = intent.getExtras().getString(MainPrefs.PASSWORD);
        dob = intent.getExtras().getString(MainPrefs.DOB);
        batch = intent.getExtras().getString(MainPrefs.BATCH);
    }

    /**
     * Anyone starting this service is expected to pass intent returned by this function.
     */
    public static Intent getIntent(String colg, String enroll, String pass, String batch, String dob,
                                   Context context) {
        Intent intent = new Intent(context, ServiceAppLogin.class);
        intent.putExtra(MainPrefs.COLG, colg);
        intent.putExtra(MainPrefs.ENROLL_NO, enroll);
        intent.putExtra(MainPrefs.PASSWORD, pass);
        intent.putExtra(MainPrefs.BATCH, batch);
        intent.putExtra(MainPrefs.DOB, dob);
        return intent;
    }

}
