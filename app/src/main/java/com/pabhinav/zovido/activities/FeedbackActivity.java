package com.pabhinav.zovido.activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.util.FirebaseHelper;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        /** Set the status bar color **/
        setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

    }

    public void setStatusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public void backClicked(View v){
        super.onBackPressed();
    }

    public void takeFeedback(View v){
        EditText headingText = (EditText)findViewById(R.id.heading_message);
        EditText longMessageText = (EditText)findViewById(R.id.long_message);

        if(headingText.getText().toString().length() == 0 && longMessageText.getText().toString().length() == 0){
            Toast.makeText(this,"Empty Feedback Recorded :(", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        /**
         * Linked with firebase for receiving feedback.
         * If there is no internet connectivity, then
         * feedback is sent whenever internet connection
         * comes back again. NetworkChangeRecevier does
         * this work.
         */
        final String feedback = headingText.getText().toString() + " Detail : " + longMessageText.getText().toString();
        FirebaseHelper firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.sendFeedback(feedback);

        Toast.makeText(this, "Thanks for your response !", Toast.LENGTH_LONG).show();

        finish();
    }
}
