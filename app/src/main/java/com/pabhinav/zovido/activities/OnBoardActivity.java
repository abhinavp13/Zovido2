package com.pabhinav.zovido.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.activityhelpers.CallDetailsHelper;
import com.pabhinav.zovido.activities.activityhelpers.OnBoardHelper;

public class OnBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        /** Set the status bar color **/
        setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        /** Rest will be handled by call details helper class **/
        OnBoardHelper onBoardHelper = new OnBoardHelper(this);

        /** Setup Tabs for this activity **/
        onBoardHelper.setupTabs();
    }

    /**
     * Sets the status bar color, if possible.
     *
     * @param color to be rendered on status bar.
     */
    public void setStatusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
