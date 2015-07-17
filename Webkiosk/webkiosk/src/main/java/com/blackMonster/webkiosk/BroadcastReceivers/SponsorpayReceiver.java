package com.blackMonster.webkiosk.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SponsorpayReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.d("YourReceiver", "Received");
    	

        com.sponsorpay.advertiser.InstallReferrerReceiver spInstallReferrerReceiver =
            new com.sponsorpay.advertiser.InstallReferrerReceiver();
        spInstallReferrerReceiver.onReceive(context, intent);
    }
}