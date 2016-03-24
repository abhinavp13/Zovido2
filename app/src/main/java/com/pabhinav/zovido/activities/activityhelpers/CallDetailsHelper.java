package com.pabhinav.zovido.activities.activityhelpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.AboutActivity;
import com.pabhinav.zovido.activities.CallDetailsActivity;
import com.pabhinav.zovido.activities.FeedbackActivity;
import com.pabhinav.zovido.adpater.CallDetailsPagerAdapter;
import com.pabhinav.zovido.fragments.CallLogFragmentTab;
import com.pabhinav.zovido.fragments.SavedLogFragmentTab;

/**
 * @author pabhinav
 */
public class CallDetailsHelper {

    private CallDetailsActivity callDetailsActivity;
    private CallDetailsPagerAdapter viewPagerAdapter;

    /** Constructor for this class **/
    public CallDetailsHelper(Context context){
        this.callDetailsActivity = (CallDetailsActivity)context;
    }

    /**
     * Handle all tab related events
     */
    public void setupTabs(){

        TabLayout tabLayout = (TabLayout) callDetailsActivity.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Recent Logs"));
        tabLayout.addTab(tabLayout.newTab().setText("Saved Logs"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) callDetailsActivity.findViewById(R.id.pager);
        viewPagerAdapter = new CallDetailsPagerAdapter(
                callDetailsActivity.getSupportFragmentManager(),
                tabLayout.getTabCount()
        );
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {

                /** update adapter **/
                CallLogFragmentTab callLogFragmentTab = viewPagerAdapter.callLogTab;
                if(callLogFragmentTab != null){
                    if(callLogFragmentTab.callLogsRecyclerViewAdapter != null){
                        callLogFragmentTab.callLogsRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }

                /** update adapter **/
                SavedLogFragmentTab savedLogFragmentTab = viewPagerAdapter.savedLogTab;
                if(savedLogFragmentTab != null){
                    if(savedLogFragmentTab.savedLogsRecyclerViewAdapter != null){
                        savedLogFragmentTab.savedLogsRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }

                /** Change the visibility of custom call icon (and may be fab menu, if fab menu is not part of fragment) **/
                if (position == 0) {
                    callDetailsActivity.findViewById(R.id.custom_addition_icon).setVisibility(View.GONE);
                } else {
                    callDetailsActivity.findViewById(R.id.custom_addition_icon).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

    }

    /** Handle upload items **/
    public boolean callUploadItems(){
        if(viewPagerAdapter != null){
            if(viewPagerAdapter.savedLogTab != null){
                return viewPagerAdapter.savedLogTab.uploadClicked();
            }
        }
        return false;
    }

    /** Handle export cloud items **/
    public void callExportToCloudItems(){
        if(viewPagerAdapter != null){
            if(viewPagerAdapter.savedLogTab != null){
                viewPagerAdapter.savedLogTab.cloudExport();
            }
        }
    }

    /** Handle export local items **/
    public void callExportToLocalItems(){
        if(viewPagerAdapter != null){
            if(viewPagerAdapter.savedLogTab != null){
                viewPagerAdapter.savedLogTab.localExport();
            }
        }
    }

    /**
     * Handle Feedback item clicked in drawer.
     */
    public void handleFeedbackClicked(){
        Intent intent = new Intent(callDetailsActivity, FeedbackActivity.class);
        callDetailsActivity.startActivity(intent);
    }

    /**
     * Handle About item clicked in drawer
     */
    public void handleAboutClicked(){
        Intent intent = new Intent(callDetailsActivity, AboutActivity.class);
        callDetailsActivity.startActivity(intent);
    }

}
