package com.blackMonster.webkiosk.ui;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class greyPreference extends Preference {

	public greyPreference(Context context) {
		super(context);
	}
	
	public greyPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public greyPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
        ((TextView)view.findViewById(android.R.id.title)).setTextColor(Color.rgb(178, 178, 178));
        ((TextView)view.findViewById(android.R.id.summary)).setTextColor(Color.rgb(178, 178, 178));

	}
	
}