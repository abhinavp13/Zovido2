package com.pabhinav.zovido.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.util.Constants;
import com.pabhinav.zovido.util.Utils;
import com.pabhinav.zovido.util.ZLog;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the base activity class.
 * Main Call Details Activity extends it.
 *
 * @author pabhinav
 */
public abstract class AbstractActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Set the status bar color **/
        setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        /** Ask For permissions **/
        checkAllPermissions();
    }

    /**  Ask and grant all permissions (for android > 6.0) **/
    private void checkAllPermissions(){

        /** Check for permissions **/
        int readCallLog = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG);
        int accessNetworkState = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NETWORK_STATE);
        int internet = ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET);
        int readContacts = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS);
        int writeExternalStorage = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readLogs = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_LOGS);

        /** List all the permissions not granted **/
        List<String> permissions = new ArrayList<String>();
        if (readCallLog != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CALL_LOG );
        }
        if (accessNetworkState != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE );
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET );
        }
        if (readContacts != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CONTACTS );
        }
        if (writeExternalStorage != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }
        if (readLogs != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_LOGS );
        }


        if(!permissions.isEmpty() ) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions.toArray(new String[permissions.size()]),
                    Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS
            );
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch ( requestCode ) {
            case Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {

                        if(ZovidoApplication.getInstance() != null) {
                            ZovidoApplication.getInstance().trackEvent("Error", "Permission Denied", permissions[i]);
                        }

                        /** If permission denied, take user to settings -> app permissions **/
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(permissions[i].equals(Manifest.permission.READ_CONTACTS)
                                    || permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    || permissions[i].equals(Manifest.permission.READ_CALL_LOG)){

                                /** Take user to app settings and ask for permissions **/
                                Utils.needPermissionsDialog(AbstractActivity.this);
                            }
                        }
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
