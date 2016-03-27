package com.pabhinav.zovido.adpater;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pabhinav.zovido.fragments.OnBoardFiveFragmentTab;
import com.pabhinav.zovido.fragments.OnBoardFourFragmentTab;
import com.pabhinav.zovido.fragments.OnBoardOneFragmentTab;
import com.pabhinav.zovido.fragments.OnBoardSixFragmentTab;
import com.pabhinav.zovido.fragments.OnBoardThreeFragmentTab;
import com.pabhinav.zovido.fragments.OnBoardTwoFragmentTab;

/**
 * @author pabhinav
 */
public class OnBoardPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public OnBoardPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new OnBoardOneFragmentTab();
            case 1:
                return new OnBoardTwoFragmentTab();
            case 2:
                return new OnBoardThreeFragmentTab();
            case 3:
                return new OnBoardFourFragmentTab();
            case 4:
                return new OnBoardFiveFragmentTab();
            case 5:
                return new OnBoardSixFragmentTab();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
