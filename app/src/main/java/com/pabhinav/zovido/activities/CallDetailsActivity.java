package com.pabhinav.zovido.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.activityhelpers.CallDetailsHelper;
import com.pabhinav.zovido.alertdialogs.ZovidoAlertInputDialog;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.drawer.NavigationDrawerElements;
import com.pabhinav.zovido.util.SharedPreferencesMap;

public class CallDetailsActivity extends AbstractActivity implements NavigationView.OnNavigationItemSelectedListener {

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

        /** Set agent name in drawer **/
        setAgentNameInDrawer();

        toggleUpload = false;

    }

    /** Need to add custom form **/
    public void customAdditionFormClicked(View v){
        Intent intent = new Intent(CallDetailsActivity.this, CallFeedbackActivity.class);
        startActivity(intent);
    }

    /** Sets the agent name in the navigation drawer **/
    public void setAgentNameInDrawer() {
        if(ZovidoApplication.getInstance() != null){
            NavigationDrawerElements navigationDrawerElements = ZovidoApplication.getInstance().getDrawerNavigationViewElements(CallDetailsActivity.this);

            if(navigationDrawerElements != null){
                TextView agentNameTextView = navigationDrawerElements.getAgentNameTextView();
                if(agentNameTextView != null){
                    agentNameTextView.setText(ZovidoApplication.getInstance().getAgentName());
                }
            }

        }
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
                ZovidoApplication.getInstance().getDrawerNavigationViewElements(CallDetailsActivity.this).getAgentNameTextView().setText(editTextString);
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        /** Handle navigation view item clicks here. **/
        int id = item.getItemId();

        /** TODO : Navigation drawer item click events  **/
        if (id == R.id.nav_sheet_settings && callDetailsHelper != null) {

        } else if (id == R.id.nav_feedback && callDetailsHelper != null) {
            callDetailsHelper.handleFeedbackClicked();
        } else if (id == R.id.nav_about && callDetailsHelper != null) {
            callDetailsHelper.handleAboutClicked();
        }

        /** Close drawer **/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        /** Add item click listener for navigation view **/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        FloatingActionButton fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload);
        FloatingActionButton localDataStorageFab = (FloatingActionButton) findViewById(R.id.export_to_local_storage);
        FloatingActionButton cloudDataStorageFab = (FloatingActionButton) findViewById(R.id.export_to_cloud_storage);
        TextView exportToDriveText = (TextView) findViewById(R.id.text_for_export_to_drive);
        TextView exportToLocalText = (TextView) findViewById(R.id.text_for_export_to_local_storage);

        /** they should all be non-null **/
        if(fakeBackgroundEffect == null
                || fabUpload == null
                || localDataStorageFab == null
                || cloudDataStorageFab == null
                || exportToDriveText == null
                || exportToLocalText == null){
            return;
        }

        if(!toggleUpload){
            fabUpload.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear_white_24dp));
            fakeBackgroundEffect.setVisibility(View.VISIBLE);
            localDataStorageFab.setVisibility(View.VISIBLE);
            cloudDataStorageFab.setVisibility(View.VISIBLE);
            exportToDriveText.setVisibility(View.VISIBLE);
            exportToLocalText.setVisibility(View.VISIBLE);

        } else {
            fabUpload.setImageDrawable(getResources().getDrawable(R.drawable.ic_file_upload_white_24dp));
            fakeBackgroundEffect.setVisibility(View.GONE);
            localDataStorageFab.setVisibility(View.GONE);
            cloudDataStorageFab.setVisibility(View.GONE);
            exportToDriveText.setVisibility(View.GONE);
            exportToLocalText.setVisibility(View.GONE);
        }

        toggleUpload = !toggleUpload;
    }
}
