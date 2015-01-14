package com.blackMonster.webkiosk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class YourReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.d("YourReceiver", "Received");
    	

        com.sponsorpay.advertiser.InstallReferrerReceiver spInstallReferrerReceiver =
            new com.sponsorpay.advertiser.InstallReferrerReceiver();
        spInstallReferrerReceiver.onReceive(context, intent);
    }
}