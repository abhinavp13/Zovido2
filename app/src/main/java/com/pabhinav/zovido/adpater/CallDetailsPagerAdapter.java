package com.pabhinav.zovido.adpater;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pabhinav.zovido.fragments.CallLogFragmentTab;
import com.pabhinav.zovido.fragments.SavedLogFragmentTab;

/**
 * @author pabhinav
 */
public class CallDetailsPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    /** Tabs **/
    public CallLogFragmentTab callLogTab;
    public SavedLogFragmentTab savedLogTab;

    public CallDetailsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                callLogTab = new CallLogFragmentTab();
                return callLogTab;
            case 1:
                savedLogTab = new SavedLogFragmentTab();
                return savedLogTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
