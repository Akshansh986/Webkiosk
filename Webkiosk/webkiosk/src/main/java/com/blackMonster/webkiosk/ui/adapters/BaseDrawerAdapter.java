package com.blackMonster.webkiosk.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackMonster.notifications.LocalData;
import com.blackMonster.webkioskApp.R;

import java.util.ArrayList;

//TODO rip english.
/**
 * Adapter for drawer(list we get from swipe from left edge of screen).
 */
public class BaseDrawerAdapter extends ArrayAdapter<String> {
    private ArrayList<String> objects;
    Context context;


    public BaseDrawerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.context = context;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View v;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.drawer_row, null);


        String data = objects.get(position);

        if (data != null) {
            TextView textView = (TextView) v.findViewById(R.id.drawer_text);
            if (textView != null) {
                textView.setText(data);
            }
            //TODO enumurate text
            if (data.equals(context.getString(R.string.notification_view)) && LocalData.isShowNotificationAlert(context)) { //Show red alert in drawer if any notification is published by developers.
                ((ImageView) v.findViewById(R.id.drawer_alert)).setVisibility(View.VISIBLE);
            }
        }

        return v;
    }
}
