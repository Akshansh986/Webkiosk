package com.blackMonster.webkiosk.refresher;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;

/**
 * Created by akshansh on 17/07/15.
 */
public class ServiceLogin extends IntentService {
    public static final String TAG = "ServiceLogin";
    String enroll, pass, batch, colg;

    public ServiceLogin() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initGlobalVariables(intent);

        InitDB initDB = new InitDB(enroll,pass,batch,colg,this);
        if (initDB.start()) {
            CrawlerDelegate crawlerDelegate = initDB.getCrawlerDelegate();
            new RefreshDB(RefreshDB.MANUAL_REFRESH,this).refresh(crawlerDelegate);
        }

    }

    private void initGlobalVariables(Intent intent) {
        colg = intent.getExtras().getString(MainPrefs.COLG);
        enroll = intent.getExtras().getString(MainPrefs.ENROLL_NO);
        pass = intent.getExtras().getString(MainPrefs.PASSWORD);
        batch = intent.getExtras().getString(MainPrefs.BATCH);
    }

    public static Intent getIntent(String colg, String enroll, String pass, String batch,
                                   Context context) {
        Intent intent = new Intent(context, ServiceLogin.class);
        intent.putExtra(MainPrefs.COLG, colg);
        intent.putExtra(MainPrefs.ENROLL_NO, enroll);
        intent.putExtra(MainPrefs.PASSWORD, pass);
        intent.putExtra(MainPrefs.BATCH, batch);
        return intent;
    }

}
