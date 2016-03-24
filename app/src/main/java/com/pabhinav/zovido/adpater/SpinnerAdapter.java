package com.pabhinav.zovido.adpater;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pabhinav.zovido.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pabhinav
 */
public class SpinnerAdapter extends BaseAdapter {


    private List<String> mItems = new ArrayList<>();
    private Context context;
    private int dropDownLayout;
    private int nonDropDownLayout;

    /** Constructor **/
    public SpinnerAdapter(Context context, int dropDownLayout, int nonDropDownLayout){
        this.context = context;
        this.dropDownLayout = dropDownLayout;
        this.nonDropDownLayout = nonDropDownLayout;
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(String yourObject) {
        mItems.add(yourObject);
    }

    public void addItems(List<String> yourObjectList) {
        mItems.addAll(yourObjectList);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public String getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = ((Activity)context).getLayoutInflater().inflate(dropDownLayout, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        if(textView != null) {
            textView.setText(getDropDownItemTitle(position));
        }
        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = ((Activity)context).getLayoutInflater().inflate(nonDropDownLayout, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(R.id.text_view);
        if(textView != null) {
            textView.setText(getNonDropDownItemTitle(position));
        }
        return view;
    }

    /** returns text for each item **/
    public String getDropDownItemTitle(int position){
        return mItems.get(position);
    }

    public String getNonDropDownItemTitle(int position){
        return mItems.get(position);
    }
}