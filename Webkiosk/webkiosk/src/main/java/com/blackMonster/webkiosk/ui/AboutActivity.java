package com.blackMonster.webkiosk.ui;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import com.blackMonster.webkioskApp.R;

public class AboutActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getSupportActionBar().setLogo(
				getResources().getDrawable(R.drawable.ic_logo));
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.theme)));
		getSupportActionBar().setTitle("About Webkiosk");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setAppVer();
	}

	private void setAppVer() {
		String app_ver;
		try {
			app_ver = "Version "
					+ getPackageManager().getPackageInfo(this.getPackageName(),
							0).versionName;
		} catch (NameNotFoundException e) {
			app_ver = "NA";
		}
		((TextView) findViewById(R.id.about_version)).setText(app_ver);

	}

	public void fbClick(View v) {
		Intent fbIntent;
		try {
			getPackageManager().getPackageInfo("com.facebook.katana", 0);
			fbIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("fb://profile/468673819914172"));
		} catch (Exception e) {
			fbIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/webkioskapp"));
		}

		// Intent browserIntent = new Intent(Intent.ACTION_VIEW,
		// Uri.parse("https://www.facebook.com/webkioskapp"));
		startActivity(fbIntent);

	}

	public void akkiClick(View v) {
		Intent fbIntent;
		try {
			getPackageManager().getPackageInfo("com.facebook.katana", 0);
			fbIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("fb://profile/100000545453973"));
		} catch (Exception e) {
			fbIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/akshansh986"));
		}

		startActivity(fbIntent);

	}

	public void nikClick(View v) {
		Intent fbIntent;
		try {
			getPackageManager().getPackageInfo("com.facebook.katana", 0);
			fbIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("fb://profile/100001034477688"));
		} catch (Exception e) {
			fbIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/007niks007"));
		}

		startActivity(fbIntent);

	}

	public void sattyClick(View v) {
		Intent fbIntent;
		try {
			getPackageManager().getPackageInfo("com.facebook.katana", 0);
			fbIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("fb://profile/100000352284657"));
		} catch (Exception e) {
			fbIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/sathya.sairam.1"));
		}

		startActivity(fbIntent);

	}

}
