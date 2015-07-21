package com.blackMonster.webkiosk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.ui.ActivityPremium;
import com.blackMonster.webkiosk.ui.LoginActivity;
import com.blackMonster.webkiosk.ui.StartupActivity;
import com.crittercism.app.Crittercism;
import com.sponsorpay.SponsorPay;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RefreshServicePrefs.resetIfrunningFromLongTime(this);

		try {
			Crittercism.initialize(getApplicationContext(), "53eb5a1683fb796b50000004");
			SponsorPay.start(PremiumManager.SponsorpayAppID, null, PremiumManager.SponsorpaySecurityToken, this);
	    } catch (RuntimeException e){
	        M.log(TAG, e.getLocalizedMessage());
	    }  


		if (WebkioskApp.canViewAttendance(this)) {
			launchStartupActivity(this);
		} else {
			startActivity(new Intent(this, LoginActivity.class));
		}

        finish();

	}


	public static void launchStartupActivity(Activity activity) {
		if (PremiumManager.showFirstTime(activity.getApplicationContext())) {
			activity.startActivity(new Intent(activity, ActivityPremium.class));
			PremiumManager.setFirstTimeDone(activity.getApplicationContext());
		}
		else
		{
		StartupActivity.setStartupActivity(activity);
		activity.startActivity(new Intent(activity, StartupActivity
				.getStartupActivity(activity)));
		}
	}

}
