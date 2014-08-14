package com.blackMonster.webkiosk;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.crittercism.app.CritterCallback;
import com.crittercism.app.CritterUserData;
import com.crittercism.app.CritterUserDataRequest;
import com.crittercism.app.Crittercism;

public class RateMe {
	private static final String TAG = "RateMe";
	
	static final Handler myHandler = new AlertDialogHandler();

	private static class AlertDialogHandler extends Handler {
	    private final static int ALERT_DIALOG_WHAT = 1;
	    private AlertDialog ad = null;

	public void setAlertDialog(AlertDialog ad) {
	     this.ad = ad;
	}

	public void dismissAlertDialog() {
	   if (ad!=null) ad.dismiss();
	}

	@Override public void handleMessage(Message msg) {
	    switch (msg.what) {
	        case ALERT_DIALOG_WHAT:
	            // Ensure that Crittercism.generateRateMyAppAlertDialog(Context)
	            // did not return null.
	            if (ad != null) {
	                ad.show();
	            }
	            break;
	        default:
	            break;
	       }
	   }
	}
	
	
	

	public static void haldleRateMeDialog(final Activity activity) {
    	Log.d(TAG, "haldleRateMeDialog");
    	

	    CritterCallback cb = new CritterCallback() {
	    
	        @Override public void onCritterDataReceived(CritterUserData userData) {
	            boolean shouldShowRateMyAppAlert = userData.shouldShowRateMyAppAlert();
	            String title = userData.getRateMyAppTitle();
	            String message = userData.getRateMyAppMessage();
	            Log.d(TAG, "Received " + shouldShowRateMyAppAlert + " " + title + "  " + message);
	            

	            if (shouldShowRateMyAppAlert) {
	                Looper.prepare();

	             
	                ((AlertDialogHandler)myHandler).setAlertDialog(
	                    Crittercism.generateRateMyAppAlertDialog(activity));
	                myHandler.dispatchMessage(Message.obtain(myHandler,
	                    AlertDialogHandler.ALERT_DIALOG_WHAT));

	                Looper.loop();
	                Looper.myLooper().quit();
	             
	            }
	        }
	    };


	    CritterUserDataRequest request = new CritterUserDataRequest(cb)
	                                            .requestRateMyAppInfo();

	    request.makeRequest();
	}
	
	public static void hadleOnPause() {
		AlertDialogHandler adh =  ((AlertDialogHandler)RateMe.myHandler);
		if (adh!=null) adh.dismissAlertDialog();
	}
}
