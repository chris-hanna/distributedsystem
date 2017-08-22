package com.command.chris.connector;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Device> devices;

    public MyListAdapter(Context c, ArrayList<Device> dList) {
        context = c;
        devices = dList;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        super.registerDataSetObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        super.unregisterDataSetObserver(dataSetObserver);
    }

    @Override
    public int getGroupCount() {
        return devices.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return devices.get(i).functions.size();
    }

    @Override
    public Device getGroup(int i) {
        return devices.get(i);
    }

    @Override
    public DeviceFunction getChild(int i, int ii) {
        return devices.get(i).functions.get(ii);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_device, null);
        }

        TextView group = view.findViewById(R.id.device_item);
        group.setText(getGroup(i).toString());

        return view;
    }

    @Override
    public View getChildView(int i, int ii, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.device_operation, null);
        }

        TextView childItem = view.findViewById(R.id.operation_item);
        childItem.setText(getChild(i, ii).toString());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
