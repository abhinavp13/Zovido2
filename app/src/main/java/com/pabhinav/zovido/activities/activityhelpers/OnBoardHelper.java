package com.pabhinav.zovido.activities.activityhelpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.CallDetailsActivity;
import com.pabhinav.zovido.activities.OnBoardActivity;
import com.pabhinav.zovido.adpater.OnBoardPagerAdapter;
import com.pabhinav.zovido.application.ZovidoApplication;

/**
 * @author pabhinav
 */
public class OnBoardHelper {

    private OnBoardActivity onBoardActivity;

    /** Constructor for this class **/
    public OnBoardHelper(Context context){
        this.onBoardActivity = (OnBoardActivity)context;
    }

    /**
     * Handle all tab related events
     */
    public void setupTabs(){

        if(onBoardActivity == null){
            return;
        }

        TabLayout tabLayout = (TabLayout) onBoardActivity.findViewById(R.id.tab_layout);
        if(tabLayout == null){
            return;
        }
        tabLayout.addTab(tabLayout.newTab().setText("Tab One"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab Two"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab Three"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab Four"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab Five"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab Six"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) onBoardActivity.findViewById(R.id.pager);
        if(viewPager == null){
            return;
        }
        OnBoardPagerAdapter onBoardPagerAdapter = new OnBoardPagerAdapter(
                onBoardActivity.getSupportFragmentManager(),
                tabLayout.getTabCount()
        );
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(onBoardPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        /** Setup dot click events **/
        final TabDotViewHandler tabDotViewHandler = new TabDotViewHandler(viewPager, onBoardActivity);
        tabDotViewHandler.registerClickEvents();

        /** Setup action bar buttons **/
        final ActionBarHandler actionBarHandler = new ActionBarHandler(viewPager, onBoardActivity);
        actionBarHandler.registerClickEvents();
        actionBarHandler.invalidateCurrentPage(1);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabDotViewHandler.changeDotsColor(position+1);
                actionBarHandler.invalidateCurrentPage(position + 1);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * This class handles all the dots click events
     * present at the bottom of each OnBoarding Screen.
     */
    class TabDotViewHandler implements View.OnClickListener {

        private ViewPager viewPager;
        private OnBoardActivity onBoardActivity;
        private ImageView tabOneDot;
        private ImageView tabTwoDot;
        private ImageView tabThreeDot;
        private ImageView tabFourDot;
        private ImageView tabFiveDot;
        private ImageView tabSixDot;

        public TabDotViewHandler(ViewPager viewPager, OnBoardActivity onBoardActivity){
            this.viewPager = viewPager;
            this.onBoardActivity = onBoardActivity;
            if(onBoardActivity == null || viewPager == null){
                return;
            }
            this.tabOneDot = (ImageView) onBoardActivity.findViewById(R.id.tab_one_dot);
            this.tabTwoDot = (ImageView) onBoardActivity.findViewById(R.id.tab_two_dot);
            this.tabThreeDot = (ImageView) onBoardActivity.findViewById(R.id.tab_three_dot);
            this.tabFourDot = (ImageView) onBoardActivity.findViewById(R.id.tab_four_dot);
            this.tabFiveDot = (ImageView) onBoardActivity.findViewById(R.id.tab_five_dot);
            this.tabSixDot = (ImageView) onBoardActivity.findViewById(R.id.tab_six_dot);
        }

        /**
         * Register click events for each dot view
         */
        public void registerClickEvents(){

            if(tabOneDot == null
                    || tabTwoDot == null
                    || tabThreeDot == null
                    || tabFourDot == null
                    || tabFiveDot == null
                    || tabSixDot == null){
                return;
            }

            tabOneDot.setOnClickListener(this);
            tabTwoDot.setOnClickListener(this);
            tabThreeDot.setOnClickListener(this);
            tabFourDot.setOnClickListener(this);
            tabFiveDot.setOnClickListener(this);
            tabSixDot.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(v == null || viewPager == null){
                return;
            }

            switch (v.getId()){
                case R.id.tab_one_dot:
                    changeDotsColor(1);
                    viewPager.setCurrentItem(0, true);
                    break;
                case R.id.tab_two_dot:
                    changeDotsColor(2);
                    viewPager.setCurrentItem(1, true);
                    break;
                case R.id.tab_three_dot:
                    changeDotsColor(3);
                    viewPager.setCurrentItem(2, true);
                    break;
                case R.id.tab_four_dot:
                    changeDotsColor(4);
                    viewPager.setCurrentItem(3, true);
                    break;
                 case R.id.tab_five_dot:
                    changeDotsColor(5);
                    viewPager.setCurrentItem(4, true);
                    break;
                case R.id.tab_six_dot:
                    changeDotsColor(6);
                    viewPager.setCurrentItem(5, true);
                    break;
            }
        }

        /**
         * Changes the color of dots according to the selected tab
         *
         * @param position denotes the current tab position + 1
         */
        public void changeDotsColor(int position){

            if(tabOneDot == null
                    || tabTwoDot == null
                    || tabThreeDot == null
                    || tabFourDot == null
                    || tabFiveDot == null
                    || tabSixDot == null
                    || onBoardActivity == null){
                return;
            }

            tabOneDot.setImageDrawable(position == 1 ?
                    onBoardActivity.getResources().getDrawable(R.drawable.blue_dot)
                    : onBoardActivity.getResources().getDrawable(R.drawable.grey_dot));
            tabTwoDot.setImageDrawable(position == 2 ?
                    onBoardActivity.getResources().getDrawable(R.drawable.blue_dot)
                    : onBoardActivity.getResources().getDrawable(R.drawable.grey_dot));
            tabThreeDot.setImageDrawable(position == 3 ?
                    onBoardActivity.getResources().getDrawable(R.drawable.blue_dot)
                    : onBoardActivity.getResources().getDrawable(R.drawable.grey_dot));
            tabFourDot.setImageDrawable(position == 4 ?
                    onBoardActivity.getResources().getDrawable(R.drawable.blue_dot)
                    : onBoardActivity.getResources().getDrawable(R.drawable.grey_dot));
            tabFiveDot.setImageDrawable(position == 5 ?
                    onBoardActivity.getResources().getDrawable(R.drawable.blue_dot)
                    : onBoardActivity.getResources().getDrawable(R.drawable.grey_dot));
            tabSixDot.setImageDrawable(position == 6 ?
                    onBoardActivity.getResources().getDrawable(R.drawable.blue_dot)
                    : onBoardActivity.getResources().getDrawable(R.drawable.grey_dot));
        }
    }

    /**
     * Handles all the views which will be displayed on the top of each tab,
     * this mostly involves next, skip, back and tick buttons.
     */
    class ActionBarHandler implements View.OnClickListener {

        TextView nextTextView;
        ImageView nextImageView;
        RelativeLayout nextRelativeLayout;
        RelativeLayout skipRelativeView;
        TextView skipTextView;
        ImageView skipFastForward;
        TextView backTextView;
        ImageView backImageView;
        RelativeLayout backRelativeLayout;
        ImageView tickImageView;
        ViewPager viewPager;
        OnBoardActivity onBoardActivity;

        public ActionBarHandler(ViewPager viewPager, OnBoardActivity onBoardActivity){
            if(onBoardActivity == null){
                return;
            }
            this.viewPager = viewPager;
            this.onBoardActivity = onBoardActivity;
            backImageView = (ImageView) onBoardActivity.findViewById(R.id.back_image_view);
            backRelativeLayout = (RelativeLayout) onBoardActivity.findViewById(R.id.relative_layout_back);
            nextTextView = (TextView)onBoardActivity.findViewById(R.id.next_text_view);
            nextImageView = (ImageView)onBoardActivity.findViewById(R.id.next_image_view);
            nextRelativeLayout = (RelativeLayout) onBoardActivity.findViewById(R.id.next_relative_layout);
            skipRelativeView = (RelativeLayout)onBoardActivity.findViewById(R.id.relativeLayout_skip_view);
            backTextView = (TextView)onBoardActivity.findViewById(R.id.back_text_view);
            tickImageView = (ImageView)onBoardActivity.findViewById(R.id.tick_image_view);
            skipTextView = (TextView)onBoardActivity.findViewById(R.id.skip_text_view);
            skipFastForward = (ImageView) onBoardActivity.findViewById(R.id.skip_fast_forward_image_view);
        }

        /**
         * Register Click events for views
         */
        public void registerClickEvents(){

            if(nextTextView == null
                    || skipRelativeView == null
                    || backTextView == null
                    || tickImageView == null
                    || skipTextView == null
                    || skipFastForward == null
                    || skipRelativeView == null
                    || nextImageView == null
                    || nextRelativeLayout == null
                    || backImageView == null
                    || backRelativeLayout == null){
                return;
            }

            nextTextView.setOnClickListener(this);
            skipRelativeView.setOnClickListener(this);
            backTextView.setOnClickListener(this);
            tickImageView.setOnClickListener(this);
            skipTextView.setOnClickListener(this);
            skipFastForward.setOnClickListener(this);
            skipRelativeView.setOnClickListener(this);
            nextImageView.setOnClickListener(this);
            nextRelativeLayout.setOnClickListener(this);
            backImageView.setOnClickListener(this);
            backRelativeLayout.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            if(v == null || viewPager == null){
                return;
            }

            switch (v.getId()){
                case R.id.next_image_view:
                case R.id.next_relative_layout:
                case R.id.next_text_view:
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    invalidateCurrentPage(viewPager.getCurrentItem() + 1);
                    break;
                case R.id.relative_layout_back:
                case R.id.back_image_view:
                case R.id.back_text_view:
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1 , true);
                    invalidateCurrentPage(viewPager.getCurrentItem() + 1);
                    break;
                case R.id.skip_text_view:
                case R.id.skip_fast_forward_image_view:
                case R.id.relativeLayout_skip_view:
                    viewPager.setCurrentItem(5, true);
                    invalidateCurrentPage(6);
                    break;
                case R.id.tick_image_view:
                    validateNameEntered();
                    break;
            }
        }

        /**
         * Invalidate the current page is correctly displayed.
         *
         * @param pageNumber the current page number of the tab displayed.
         */
        public void invalidateCurrentPage(int pageNumber){

            if(skipRelativeView == null
                    || nextTextView == null
                    || backTextView == null
                    || tickImageView == null) {
                return;
            }

            skipRelativeView.setVisibility(pageNumber == 1 ? View.VISIBLE : View.GONE);
            nextRelativeLayout.setVisibility(pageNumber == 6 ? View.GONE : View.VISIBLE);
            backRelativeLayout.setVisibility(pageNumber == 1 ? View.GONE : View.VISIBLE);
            tickImageView.setVisibility(pageNumber == 6 ? View.VISIBLE : View.GONE);
        }

        /**
         * Validate the name entered by user and start the new activity if correctly entered.
         */
        public void validateNameEntered(){
            if(onBoardActivity == null){
                return;
            }
            AppCompatEditText appCompatEditText = (AppCompatEditText) onBoardActivity.findViewById(R.id.name_edit_text);
            if(appCompatEditText == null){
                return;
            }
            if(appCompatEditText.getText() == null || appCompatEditText.getText().length() == 0){
                TextView textView = (TextView)onBoardActivity.findViewById(R.id.may_i_know_text_view);
                textView.setText("Please Enter Your Name");
            } else {
                if(ZovidoApplication.getInstance() == null){
                    return;
                }

                /** Track event **/
                ZovidoApplication.getInstance().trackEvent("Agent Name", "Logged in First", appCompatEditText.getText().toString());

                ZovidoApplication.getInstance().setAgentName(appCompatEditText.getText().toString());

                Intent intent = new Intent(onBoardActivity, CallDetailsActivity.class);
                onBoardActivity.startActivity(intent);

                onBoardActivity.finish();
            }
        }
    }
}
