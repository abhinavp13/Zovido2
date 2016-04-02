package com.pabhinav.zovido.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.activityhelpers.CallDetailsHelper;
import com.pabhinav.zovido.alertdialogs.ZovidoAlertInputDialog;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.util.Constants;

public class CallDetailsActivity extends AbstractActivity {

    private boolean toggleUpload;
    private CallDetailsHelper callDetailsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /** Main content view **/
        setContentView(R.layout.activity_call_details);

        /** Toolbar And Navigation Drawer Setup **/
        toolbarAndNavigationDrawerSetup();

        /** Rest will be handled by call details helper class **/
        callDetailsHelper = new CallDetailsHelper(this);

        /** Setup Tabs for this activity **/
        callDetailsHelper.setupTabs();

        toggleUpload = false;
        updateDrawerValues();
    }

    /** Update the drawer values **/
    public void updateDrawerValues(){

        /** Update view values in navigation drawer **/
        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Update the agent name in the drawer **/
        ((TextView)findViewById(R.id.agent_name_text_view)).setText(ZovidoApplication.getInstance().getAgentName());

        /** Update the recent calls counter **/
        int counterValue = ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size();
        ((TextView)findViewById(R.id.recent_count_text_view)).setText(String.valueOf(counterValue));

        /** Update the saved calls counter **/
        int counterSavedValue = ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size();
        ((TextView)findViewById(R.id.saved_count_text_view)).setText(String.valueOf(counterSavedValue));

    }

    /** Need to add custom form **/
    public void customAdditionFormClicked(View v){
        Intent intent = new Intent(CallDetailsActivity.this, CallFeedbackActivity.class);
        intent.putExtra(Constants.customForm , true);
        startActivity(intent);
    }
    
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(toggleUpload) {
            toggleFabMenuitems();
        } else {
            super.onBackPressed();
        }
    }


    /** Navigation  **/
    public void navigationHeaderClicked(View v){ /** Nothing needs to be done **/ }

    /** Agent Name Changed **/
    public void agentNameChangeClicked(View v){

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Show input dialog **/
        ZovidoAlertInputDialog zovidoAlertInputDialog = new ZovidoAlertInputDialog(
                this,
                "Change Agent Name",
                Html.fromHtml("Your previously set name is : <u> "+ ZovidoApplication.getInstance().getAgentName() + "</u>. Provide your name below : "),
                "Your Name",
                "Cancel",
                "Change"
                );
        zovidoAlertInputDialog.show();
        zovidoAlertInputDialog.setOnAlertButtonClicked(new ZovidoAlertInputDialog.OnAlertButtonClicked() {
            @Override
            public void onLeftButtonClicked(View v) {
            }

            @Override
            public void onRightButtonClicked(View v, String editTextString) {

                /** Set agent name globally **/
                ZovidoApplication.getInstance().setAgentName(editTextString);

                /** Set in drawer **/
                TextView agentTextView = ((TextView)findViewById(R.id.agent_name_text_view));
                if(agentTextView != null){
                    agentTextView.setText(editTextString);
                }
            }
        });
    }

    /** Change sheet settings **/
    public void changeSheetSettings(View v){
        if(callDetailsHelper != null) {
            callDetailsHelper.handleSheetSettingChanges();
        }
    }

    /** About clicked **/
    public void aboutClicked(View v){
        if(callDetailsHelper != null){
            callDetailsHelper.handleAboutClicked();
        }
    }

    /** Feedback clicked **/
    public void feedbackClicked(View v){
        if(callDetailsHelper != null){
            callDetailsHelper.handleFeedbackClicked();
        }
    }

    /**
     * Sets up toolbar and Navigation View related configurations
     */
    private void toolbarAndNavigationDrawerSetup(){

        /** Toolbar **/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** Drawer layout for navigation drawer **/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if(drawer == null){
            return;
        }
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    /** Upload clicked **/
    public void uploadClicked(View v){
        if(callDetailsHelper.callUploadItems()){
            /** toggle fab menu items **/
            toggleFabMenuitems();
        }
    }

    /** local export required **/
    public void localExportClicked(View v){
        callDetailsHelper.callExportToLocalItems();
        toggleFabMenuitems();
    }

    /** Cloud export required **/
    public void cloudExportClicked(View v){
        callDetailsHelper.callExportToCloudItems();
        toggleFabMenuitems();
    }

    /** Open/close fab menu **/
    public void toggleFabMenuitems(){

        View fakeBackgroundEffect = (View) findViewById(R.id.fake_background_effect);
        RelativeLayout exportToCloudText = (RelativeLayout) findViewById(R.id.relative_layout_export_to_spread_sheet_text);
        RelativeLayout exportToLocalStorageText = (RelativeLayout) findViewById(R.id.relative_layout_export_to_local_storage_text);
        ImageView exportToCloudImage = (ImageView) findViewById(R.id.upload_cloud_fab);
        ImageView exportToLocalStorageImage = (ImageView) findViewById(R.id.upload_local_fab);
        ImageView uploadMenu = (ImageView)findViewById(R.id.upload_menu_button);

        /** they should all be non-null **/
        if(exportToCloudImage == null
                || exportToLocalStorageImage == null
                || exportToLocalStorageText == null
                || exportToCloudText == null
                || fakeBackgroundEffect == null
                || uploadMenu == null){
            return;
        }

        if(!toggleUpload){
            uploadMenu.setImageDrawable(getResources().getDrawable(R.drawable.upload_cross));
            fakeBackgroundEffect.setVisibility(View.VISIBLE);
            exportToCloudImage.setVisibility(View.VISIBLE);
            exportToCloudText.setVisibility(View.VISIBLE);
            exportToLocalStorageImage.setVisibility(View.VISIBLE);
            exportToLocalStorageText.setVisibility(View.VISIBLE);
        } else {
            uploadMenu.setImageDrawable(getResources().getDrawable(R.drawable.upload_fab));
            fakeBackgroundEffect.setVisibility(View.GONE);
            exportToCloudImage.setVisibility(View.GONE);
            exportToCloudText.setVisibility(View.GONE);
            exportToLocalStorageImage.setVisibility(View.GONE);
            exportToLocalStorageText.setVisibility(View.GONE);
        }

        toggleUpload = !toggleUpload;
    }
}
