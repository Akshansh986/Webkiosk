package com.blackMonster.webkiosk.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackMonster.webkiosk.crawler.dateSheet.DS_SP;
import com.blackMonster.webkioskApp.R;

import java.util.List;

/**
 * Created by akshansh on 26/07/15.
 */
public class DatesheetAdapter extends ArrayAdapter<DS_SP> {
    Context context;
    List<DS_SP> values;

    public DatesheetAdapter(Context context, List<DS_SP> objects) {
        super(context, R.layout.activity_datesheet, objects);
        this.context = context;
        values = objects;

    }

    public void updateDataset(List<DS_SP> list) {
        values = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_datesheet, parent,
                false);

        DS_SP dssp = values.get(position);

        if (!isPresentBefore(position)) {
            ((TextView) view.findViewById(R.id.ds_header))
                    .setText(dssp.sheetCode);
            ((TextView) view.findViewById(R.id.ds_header))
                    .setVisibility(View.VISIBLE);

        } else {
            ((TextView) view.findViewById(R.id.ds_header))
                    .setVisibility(View.GONE);
        }

        ((TextView) view.findViewById(R.id.ds_Sub_name))
                .setText(dssp.course);
        ((TextView) view.findViewById(R.id.ds_date)).setText(dssp.date);
        ((TextView) view.findViewById(R.id.ds_time)).setText(dssp.time);

        if (dssp.roomNo == null || dssp.roomNo.equals(""))
            ((TextView) view.findViewById(R.id.ds_venue))
                    .setVisibility(View.GONE);
        else
            ((TextView) view.findViewById(R.id.ds_venue))
                    .setText(dssp.roomNo);

        if (dssp.seatNo == null || dssp.seatNo.equals(""))
            ((TextView) view.findViewById(R.id.ds_seatNo))
                    .setVisibility(View.GONE);
        else
            ((TextView) view.findViewById(R.id.ds_seatNo))
                    .setText(dssp.seatNo);

        return view;

    }

    private boolean isPresentBefore(int position) {
        for (int i = 0; i < position; ++i)
            if (values.get(i).sheetCode
                    .equals(values.get(position).sheetCode))
                return true;

        return false;
    }

}
