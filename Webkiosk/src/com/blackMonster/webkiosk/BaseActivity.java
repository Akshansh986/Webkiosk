package com.blackMonster.webkiosk;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.ClipData.Item;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackMonster.notifications.ActivityNotification;
import com.blackMonster.notifications.LocalData;
import com.blackMonster.notifications.NotificationManager;
import com.blackMonster.webkiosk.dateSheet.ActivityDateSheet;
import com.blackMonster.webkiosk.dateSheet.ActivityPremium;
import com.google.analytics.tracking.android.EasyTracker;
import com.sponsorpay.publisher.SponsorPayPublisher;
import com.sponsorpay.publisher.currency.SPCurrencyServerErrorResponse;
import com.sponsorpay.publisher.currency.SPCurrencyServerListener;
import com.sponsorpay.publisher.currency.SPCurrencyServerSuccesfulResponse;

public class BaseActivity extends ActionBarActivity {
	public String TAG = "BaseActivity";
	public static final int OFFERWALL_REQUEST_CODE = 5689;

	Menu actionbarMenu = null;
	public LinearLayout activityContent = null;

	private ActionBarDrawerToggle mDrawerToggle;
	public boolean openDrawerWithIcon = true;

	public boolean isOptionsMenuLoaded = false;
	public boolean drawProgressCircle = false;
	public boolean isReceiverRegistered = false;
	private boolean isRefreshBtnAnim = false;
	DrawerAdapter drawerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Log.d(TAG, "oncreate");
		Log.d(TAG, LocalData.getNotification(getBaseContext()).link + " " + LocalData.getNotification(getBaseContext()).title);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		activityContent = (LinearLayout) findViewById(R.id.act_content);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.theme)));
		getSupportActionBar().setLogo(
				getResources().getDrawable(R.drawable.ic_logo));


	}

	void initDrawer() {
		ArrayList<String> drawerList = getDrawerList();

		drawerAdapter = new DrawerAdapter(this, R.layout.drawer_row,
				 drawerList);
		drawerAdapter.setNotifyOnChange(true);

		ListView listView = ((ListView) findViewById(R.id.left_drawer));
		listView.setAdapter(drawerAdapter);

		listView.setOnItemClickListener(new DrawerItemClickListener());

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
		list.add("Timetable View");
		list.add("Attendence View");
		list.add("Web View");

		if (PremiumManager.isPermiumUser(this))
			list.add("Exam DateSheet");

		list.add("Get Premium");

		if (NotificationManager.isNotificationAvailable(this))
			list.add("Notification");
		return list;
		
	}
	
	private void updateDrawer() {
		drawerAdapter.clear();
		drawerAdapter.addAll(getDrawerList());
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

	class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
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
				// intent = new Intent(BaseActivity.this,
				// ActivityPremium.class);
				// startActivity(intent);
				startActivity(new Intent(BaseActivity.this,
					WebViewActivity.class));


				break;

			case 3:
				if (PremiumManager.isPermiumUser(getApplicationContext()))
					startActivity(new Intent(BaseActivity.this,
							ActivityDateSheet.class));
				else {
					startActivity(new Intent(BaseActivity.this,
							ActivityPremium.class));

				}
				break;

			case 4:
				if (PremiumManager.isPermiumUser(getApplicationContext()))
					startActivity(new Intent(BaseActivity.this,
							ActivityPremium.class));
				else {
					startActivity(new Intent(BaseActivity.this,
							ActivityNotification.class));
					setNotificationAlertVisibilisty(false);
				}

				break;
			case 5:
				startActivity(new Intent(BaseActivity.this,
						ActivityNotification.class));
				setNotificationAlertVisibilisty(false);
				break;

			}
			if (!getClassName().equals(
					StartupActivity.getStartupActivity(getBaseContext())
							.getSimpleName()))
				finish();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		inflateOnCreateOptionsMenu(menu);
		actionbarMenu = menu;

		isOptionsMenuLoaded = true;
		if (drawProgressCircle) {
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
		if (RefreshServicePrefs.isRunning(this)) {
			Toast.makeText(this, getString(R.string.refresh_takingplace),
					Toast.LENGTH_SHORT).show();
			return true;

		} else
			return false;

	}

	private void refresh() {
		RefreshServicePrefs.resetIfrunningFromLongTime(this);
		if (RefreshServicePrefs.isRunning(this)) {
			if (RefreshServicePrefs.getStatus(this) == RefreshServicePrefs.REFRESHING_D)
				Toast.makeText(BaseActivity.this,
						getString(R.string.refreshing_detailed_atnd),
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(BaseActivity.this,
						getString(R.string.refresh_in_progress),
						Toast.LENGTH_SHORT).show();
			return;
		}

		animateRefreshButton();
		registerReceivers();

		Intent intent = ServiceLoginRefresh.getIntent(MainPrefs.getColg(this),
				MainPrefs.getEnroll(this), MainPrefs.getPassword(this),
				MainPrefs.getBatch(this), ServiceLoginRefresh.MANUAL_REFRESH,
				false, this);

		startService(intent);

	}

	public void registerReceivers() {
		// Log.d(TAG, "superclass register receiver");
		if (!isReceiverRegistered) {
			// Log.d(TAG, "registered loginresult");

			LocalBroadcastManager
					.getInstance(this)
					.registerReceiver(
							broadcastLoginResult,
							new IntentFilter(
									ServiceLoginRefresh.BROADCAST_LOGIN_RESULT));

			LocalBroadcastManager
					.getInstance(this)
					.registerReceiver(
							broadcastNotificationResult,
							new IntentFilter(
									NotificationManager.BROADCAST_NOTIFICATION_UPDATE_RESULT));
			isReceiverRegistered = true;
		}
	}

	public void unregisterIfRegistered() {
		 Log.d(TAG, "subperclass unregister receiver");

		if (isReceiverRegistered) {
			// Log.d(TAG, "unregistered broadcast login result");
			LocalBroadcastManager.getInstance(BaseActivity.this)
					.unregisterReceiver(broadcastLoginResult);
			LocalBroadcastManager.getInstance(BaseActivity.this)
					.unregisterReceiver(broadcastNotificationResult);
			isReceiverRegistered = false;
		}
	}

	BroadcastReceiver broadcastLoginResult = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.d(TAG, "received : broadcastLoginResult " + getClassName());
			int result = intent.getExtras().getInt(
					ServiceLoginRefresh.BROADCAST_LOGIN_RESULT);
			if (result == SiteConnection.LOGIN_DONE) {
				// loginResultMessege();

			} else {
				unanimateRefreshButton();
				if (result == SiteConnection.INVALID_PASS
						|| result == SiteConnection.ACCOUNT_LOCKED)
					MyAlertDialog.showChangePasswordDialog(BaseActivity.this);
				else
					MyAlertDialog.checkDialog(BaseActivity.this);

			}

		}

	};

	BroadcastReceiver broadcastNotificationResult = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "received : broadcastNotificationResult ");

			int result = intent.getExtras().getInt(
					NotificationManager.BROADCAST_NOTIFICATION_UPDATE_RESULT);
			Log.d(TAG, result + "");
			if (result != NotificationManager.NOTIFICATION_NO_CHANGE) updateDrawer();
		/*	if (result == NotificationManager.NOTIFICATION_ADDED) {
				drawerAdapter.add("Notification");
				//setNotificationAlertVisibilisty(true);
			} else if (result == NotificationManager.NOTIFICAITON_REMOVED) {
				drawerAdapter.remove("Notification");
			} else if (result == NotificationManager.NOTIFICATION_CHANGED) {
				setNotificationAlertVisibilisty(true);

			}*/
		}

		

	};
	
	
	
	
	private void setNotificationAlertVisibilisty(boolean b) {
		int visibility;
		if (b) visibility = View.VISIBLE;
		else
			visibility = View.GONE;
		
		ListView listView = ((ListView) findViewById(R.id.left_drawer));
		//listView.getItemAtPosition(position)
		int pos = drawerAdapter.getPosition("Notification");
		if (pos!=-1) {
			Log.d(TAG, pos + "");
			((ImageView)listView.getChildAt(pos).findViewById(R.id.drawer_alert)).setVisibility(visibility);
		}
		
	}

	public void unanimateRefreshButton() {
		if (isRefreshBtnAnim) {
			MenuItemCompat.setActionView(
					actionbarMenu.findItem(R.id.action_refresh), null);
			isRefreshBtnAnim = false;
		}
	}

	public void animateRefreshButton() {
		if (isOptionsMenuLoaded) {
			if (actionbarMenu != null) {
				MenuItemCompat.setActionView(
						actionbarMenu.findItem(R.id.action_refresh),
						R.layout.progressbar);
				isRefreshBtnAnim = true;

			}
		} else
			drawProgressCircle = true;

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
	
	public class DrawerAdapter extends ArrayAdapter<String> {
		private ArrayList<String> objects;

	
		public DrawerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		
		public View getView(int position, View convertView, ViewGroup parent){
			View v = convertView;

			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.drawer_row, null);
			}

		
			String i = objects.get(position);

			if (i != null) {

				

				TextView tt = (TextView) v.findViewById(R.id.drawer_text);
				
			
				if (tt != null){
					tt.setText(i);
				}
				if (i.equals("Notification") && LocalData.isShowNotificationAlert(getBaseContext())) 
					((ImageView)v.findViewById(R.id.drawer_alert)).setVisibility(View.VISIBLE);

			
			}

			return v;

			
		}
		
		
		
	
	}
	
	SPCurrencyServerListener requestListener = new SPCurrencyServerListener() {

		@Override
		public void onSPCurrencyServerError(
				SPCurrencyServerErrorResponse response) {
			Log.d("SPCurrencyServerListener", "Request or Response Error: "
					+ response.getErrorType());
		}

		@Override
		public void onSPCurrencyDeltaReceived(
				SPCurrencyServerSuccesfulResponse response) {
			double coins = response.getDeltaOfCoins();
			if (PremiumManager.startUpdate(coins, getApplicationContext()))
				updateDrawer();
			Log.d("SPCurrencyServerListener",
					"Response From Currency Server. Delta of Coins: "
							+ String.valueOf(response.getDeltaOfCoins())
							+ ", Latest Transaction Id: "
							+ response.getLatestTransactionId());
		}
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		SponsorPayPublisher.displayNotificationForSuccessfullCoinRequest(false);
		PremiumManager.updateDays(requestListener,this);
	}	

	
	

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.

	}
	
	

}
