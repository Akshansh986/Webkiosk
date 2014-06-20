package com.blackMonster.webkiosk.dateSheet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.blackMonster.webkiosk.BaseActivity;
import com.blackMonster.webkiosk.PremiumManager;
import com.blackMonster.webkiosk.R;
import com.blackMonster.webkiosk.RefreshServicePrefs;
import com.blackMonster.webkiosk.TimetableDataHelper;
import com.sponsorpay.publisher.SponsorPayPublisher;

public class ActivityPremium extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_premium);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Get Premium Days");
		getSupportActionBar().setSubtitle(PremiumManager.getDaysLeft(this) + " days remaining");
		openDrawerWithIcon(true);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.activity_premium, null, false);
		activityContent.addView(view);

	}

	public void buttonGetForFree(View v) {
		Intent offerWallIntent = SponsorPayPublisher
				.getIntentForOfferWallActivity(getApplicationContext(), true);
		startActivityForResult(offerWallIntent, OFFERWALL_REQUEST_CODE);

	}

	@Override
	public void inflateOnCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (TimetableDataHelper.databaseExists(this))
			inflater.inflate(R.menu.menu_without_refresh, menu);
		else
			inflater.inflate(R.menu.mainmenu, menu);
		// Log.d(TAG, "oncreateoptinosmenu");
	}
}
