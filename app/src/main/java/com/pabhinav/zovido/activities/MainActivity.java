package com.pabhinav.zovido.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.customviews.CustomFloatingActionButton;
import com.pabhinav.zovido.customviews.FlickerAnimationView;
import com.pabhinav.zovido.customviews.SceneAnimationView;
import com.pabhinav.zovido.util.FlickerAnimationDrawables;

public class MainActivity extends AppCompatActivity {

    private String agentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /** Start Flicker animation **/
        new SceneAnimationView((ImageView) findViewById(R.id.gif_animation_view), new FlickerAnimationDrawables().getSplashDrawables(), 35);

        /** Set the status bar color **/
        setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        /** View required for animations **/
        TextView welcomeTextView = (TextView)findViewById(R.id.welcome_text_view);
        CustomFloatingActionButton forwardFab = (CustomFloatingActionButton) findViewById(R.id.fab_forward);
        final TextView appName = (TextView)findViewById(R.id.textView3);
        TextView appSubName = (TextView) findViewById(R.id.textView7);
        LinearLayout linearLayoutForward = (LinearLayout) findViewById(R.id.linear_layout_forward);

        /** Make them invisible for now **/
        appName.setVisibility(View.INVISIBLE);
        appSubName.setVisibility(View.INVISIBLE);
        linearLayoutForward.setVisibility(View.INVISIBLE);

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** if agent name present, Say Hi, else say Welcome **/
        agentName = ZovidoApplication.getInstance().getAgentName();
        if(agentName == null || agentName .length() == 0){
            welcomeTextView.setText("Welcome !");
        } else {
            welcomeTextView.setText("Hi, " + agentName);
        }

        /** TODO  CALL BACK FROM SCENE ANIMATION, INSTEAD OF BELOW WAITING**/
        /** First Animation Time for which the 'invisible' views gets visible **/
        final int firstAnimationTime = 2000;
        postHandlerForVisibleAnimation(appName, firstAnimationTime);
        postHandlerForVisibleAnimation(appSubName, firstAnimationTime);
        postHandlerForVisibleAnimation(linearLayoutForward, firstAnimationTime);
    }

    /**
     * Creates an handler object for delayed animation effect (fade In).
     *
     * @param view on which delayed animation has to be performed.
     * @param timeInMillis delay time
     */
    public void postHandlerForVisibleAnimation(final View view, int timeInMillis){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(view != null) {
                    Animation fadeIn = new AlphaAnimation(0, 1);
                    fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                    fadeIn.setDuration(500);
                    fadeIn.setFillAfter(true);
                    view.startAnimation(fadeIn);
                }
            }
        }, timeInMillis);
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

    /**
     * Forward/Next button clicked by user
     *
     * @param v View of the button.
     */
    public void nextClicked(View v){

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        Intent intent;
        if(agentName == null || agentName.length() == 0){
            intent = new Intent(this, OnBoardActivity.class);
        } else {
            intent = new Intent(this, CallDetailsActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
