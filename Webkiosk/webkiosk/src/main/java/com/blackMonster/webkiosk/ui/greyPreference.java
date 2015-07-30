package com.blackMonster.webkiosk.ui;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Grey color of text(Student name and enroll) in settings menu.
 */
public class GreyPreference extends Preference {

	public GreyPreference(Context context) {
		super(context);
	}
	
	public GreyPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public GreyPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
        ((TextView)view.findViewById(android.R.id.title)).setTextColor(Color.rgb(178, 178, 178));
        ((TextView)view.findViewById(android.R.id.summary)).setTextColor(Color.rgb(178, 178, 178));

	}
	
}