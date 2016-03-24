package com.pabhinav.zovido.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pabhinav.zovido.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void nextClicked(View v){
        Intent intent = new Intent(this, CallDetailsActivity.class);
        startActivity(intent);
    }
}
