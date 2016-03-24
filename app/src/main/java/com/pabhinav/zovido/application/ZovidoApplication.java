package com.pabhinav.zovido.application;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.pabhinav.zovido.BuildConfig;
import com.pabhinav.zovido.R;
import com.pabhinav.zovido.dao.SQLiteDb;
import com.pabhinav.zovido.drawer.NavigationDrawerElements;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.pojo.UploadedLogsDataParcel;
import com.pabhinav.zovido.util.AnalyticsTrackers;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import com.pabhinav.zovido.util.ACRAReportSender;
import com.pabhinav.zovido.util.GoogleCredentialUtils;
import com.pabhinav.zovido.util.SharedPreferencesMap;

import java.util.ArrayList;

/**
 * @author pabhinav
 */
@ReportsCrashes(
        formUri = "t",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast// optional. displays a Toast message when the user accepts to send a report.
)
public class ZovidoApplication extends Application {

    private  static ZovidoApplication mInstance;
    private  ArrayList<UploadedLogsDataParcel> uploadedLogsDataParcelArrayList;
    private  ArrayList<SavedLogsDataParcel> savedLogsDataParcelArrayList;
    private  ArrayList<CallLogsDataParcel> callLogsDataParcelArrayList;
    private  String agentName;
    private  NavigationDrawerElements navigationDrawerElements;
    private  SQLiteDb sqLiteDb;
    private  boolean loadingTick = false;
    private  GoogleCredential googleCredential;
    private  String spreadsheetKey;
    private  String worksheetName;
    private  SpreadsheetService spreadsheetService;

    @Override
    public void onCreate() {

        /** Initialize ACRA error reports sending library **/
        ACRA.init(this);
        ACRAReportSender yourSender = new ACRAReportSender();
        ACRA.getErrorReporter().setReportSender(yourSender);

        super.onCreate();
        mInstance = this;

        /** Initialize firebase **/
        Firebase.setAndroidContext(this);

        /** Google Analytics **/
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        /** TODO : compare install previous version, over which install this version and look for db exceptions **/


        /** TODO : find a way to only get uploaded data once. When its work is over empty its array list **/


        /** TODO : icon changes : call types, no saved log icon, logo of app, call feedback tick and cross. **/
    }

    /** Get the application instance **/
    public static synchronized ZovidoApplication getInstance() {
        return mInstance;
    }



    /**************************************************************************************
     ********************************** Google Analytics **********************************
     **************************************************************************************/

    /** Get google analytics tracker instance **/
    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }


    /******************************************************************************************
     *    Below definitions are for some static resources used throughout this application    *
     *    Make sure not to add any UI code below, because these resources are also updated    *
     *         through a background thread, not UI thread. So, can cause exceptions.          *
     ******************************************************************************************/

    /** Get synchronous sqlite db helper **/
    public synchronized SQLiteDb getSqliteDb(){
        if(sqLiteDb == null){
            sqLiteDb = new SQLiteDb(this);
        }
        return sqLiteDb;
    }



    /** Determines whether uploaded data items are still loading from database or not **/
    public synchronized void setIsLoadingTick(boolean value){
        loadingTick = value;
    }

    public boolean getIsLoadingTick(){
        return loadingTick;
    }



    /** Get google credentials **/
    public synchronized GoogleCredential getGoogleCredential() {
        if(googleCredential == null){
            try {
                googleCredential = GoogleCredentialUtils.getGoogleCredentials(this);
            } catch (Exception e){
                trackException(e);
                googleCredential = null;
            }
        }
        return googleCredential;
    }



    /** Getter and setter for Spreadsheet Service **/
    public SpreadsheetService getSpreadsheetService() {
        return spreadsheetService;
    }

    public void setSpreadsheetService(SpreadsheetService spreadsheetService) {
        this.spreadsheetService = spreadsheetService;
    }




    /** Get the agent name **/
    public String getAgentName(){
        if(agentName == null || agentName.length() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            agentName = sharedPreferencesMap.loadAgentName();
        }
        return agentName;
    }

    /** Sets the agent name **/
    public void setAgentName(String agentName){
        this.agentName = agentName;

        /** Save agent name permanently **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.saveAgentName(agentName);

        /** TODO :  Make call to this method from main activity **/
    }



    /** Get the spread sheet key **/
    public String getSpreadsheetKey(){
        if(spreadsheetKey == null || spreadsheetKey.length() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            spreadsheetKey = sharedPreferencesMap.loadSpreadsheetKey();
        }
        if(spreadsheetKey == null || spreadsheetKey.length() == 0){
            spreadsheetKey = BuildConfig.SPREADSHEETFILEKEY;
        }
        return spreadsheetKey;
    }

    /** Sets the spread sheet key **/
    public void setSpreadsheetKey(String spreadsheetKey){
        this.spreadsheetKey = spreadsheetKey;

        /** Save spread sheet key permanently **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.saveSpreadsheetKey(spreadsheetKey);
    }




    /** Get the worksheet name **/
    public String getWorksheetName(){
        if(worksheetName == null || worksheetName.length() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            worksheetName = sharedPreferencesMap.loadWorksheetName();
        }
        if (worksheetName == null || worksheetName.length() == 0) {
            worksheetName = BuildConfig.WORKSHEETNAME;
        }
        return worksheetName;
    }

    /** Sets the worksheet name **/
    public void setWorksheetName(String worksheetName){
        this.worksheetName = worksheetName;

        /** Save spread sheet key permanently **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.saveWorksheetName(worksheetName);
    }




    /** Get navigation view elements **/
    public synchronized NavigationDrawerElements getDrawerNavigationViewElements(Context context){

        /** Context must have navigation view in it, else exception is thrown **/
        if (navigationDrawerElements == null) {
            try {
                navigationDrawerElements = new NavigationDrawerElements(context);
            } catch (Exception e) {
                return null;
            }
        }
        return navigationDrawerElements;
    }




    /**************************************************************************************
     ********************************** Uploaded Items ************************************
     **************************************************************************************/


    /** Get the list of call details data parcel **/
    public synchronized ArrayList<UploadedLogsDataParcel> getUploadedLogsDataParcelArrayListInstance(){
        if(uploadedLogsDataParcelArrayList != null){
            return uploadedLogsDataParcelArrayList;
        }
        uploadedLogsDataParcelArrayList = new ArrayList<>();
        return uploadedLogsDataParcelArrayList;
    }

    /** Check whether there already exist this call detail data parcel **/
    public int indexOfUploadedLogsDataParcel(UploadedLogsDataParcel uploadedLogsDataParcel){
        if(uploadedLogsDataParcel == null){
            return -1;
        }
        return getUploadedLogsDataParcelArrayListInstance().indexOf(uploadedLogsDataParcel);
    }

    /** Add call details data parcel (always added at last position) **/
    public void addUploadedLogsDataParcel(UploadedLogsDataParcel uploadedLogsDataParcel){
        if(uploadedLogsDataParcel != null){
            if(indexOfUploadedLogsDataParcel(uploadedLogsDataParcel) == -1){
                getUploadedLogsDataParcelArrayListInstance().add(uploadedLogsDataParcel);
            }
        }
    }




    /**************************************************************************************
     ********************************** Saved Logs Items **********************************
     **************************************************************************************/



    /** Get the list of call details data parcel **/
    public synchronized ArrayList<SavedLogsDataParcel> getSavedLogsDataParcelArrayListInstance(){
        if(savedLogsDataParcelArrayList != null){
            return savedLogsDataParcelArrayList;
        }
        savedLogsDataParcelArrayList = new ArrayList<>();
        return savedLogsDataParcelArrayList;
    }

    /** Check whether there already exist this call detail data parcel **/
    public int indexOfSavedLogsDataParcel(SavedLogsDataParcel savedLogsDataParcel){
        if(savedLogsDataParcel == null){
            return -1;
        }
        return getSavedLogsDataParcelArrayListInstance().indexOf(savedLogsDataParcel);
    }

    /** Add call details data parcel (always added at last position) **/
    public void addSavedLogsDataParcel(SavedLogsDataParcel savedLogsDataParcel){
        if(savedLogsDataParcel != null){
            if(indexOfSavedLogsDataParcel(savedLogsDataParcel) == -1){
                getSavedLogsDataParcelArrayListInstance().add(savedLogsDataParcel);
            }
        }
    }

    /** Remove the call details data parcel from arraylist **/
    public void removeSavedLogsDataParcelIfExists(SavedLogsDataParcel savedLogsDataParcel){
        if(savedLogsDataParcel == null){
            return;
        }
        int index = indexOfSavedLogsDataParcel(savedLogsDataParcel);
        if(index != -1){
            getSavedLogsDataParcelArrayListInstance().remove(index);
        }
    }

    public void updateSavedLogsDataParcelIfExists(SavedLogsDataParcel savedLogsDataParcel){
        if(savedLogsDataParcel == null){
            return;
        }
        int index = indexOfSavedLogsDataParcel(savedLogsDataParcel);
        if(index != -1){
            getSavedLogsDataParcelArrayListInstance().set(index, savedLogsDataParcel);
        }
    }

    public void addAllAtTopSavedLogsDataParcel(ArrayList<SavedLogsDataParcel> savedLogsDataParcels){
        getSavedLogsDataParcelArrayListInstance().addAll(0, savedLogsDataParcels);
    }



    /**************************************************************************************
     ********************************** Call Logs Items ***********************************
     **************************************************************************************/


    /** Get the list of call details data parcel **/
    public synchronized ArrayList<CallLogsDataParcel> getCallLogsDataParcelArrayListInstance(){
        if(callLogsDataParcelArrayList != null){
            return callLogsDataParcelArrayList;
        }
        callLogsDataParcelArrayList = new ArrayList<>();
        return callLogsDataParcelArrayList;
    }

    /** Check whether there already exist this call detail data parcel **/
    public int indexOfCallLogsDataParcel(CallLogsDataParcel callLogsDataParcel){
        if(callLogsDataParcel == null){
            return -1;
        }
        return getCallLogsDataParcelArrayListInstance().indexOf(callLogsDataParcel);
    }

    /** Add call details data parcel (always added at last position) **/
    public void addCallLogsDataParcel(CallLogsDataParcel callLogsDataParcel){
        if(callLogsDataParcel != null){
            if(indexOfCallLogsDataParcel(callLogsDataParcel) == -1){
                getCallLogsDataParcelArrayListInstance().add(callLogsDataParcel);
            }
        }
    }

    /** Remove the call details data parcel from arraylist **/
    public void removeCallLogsDataParcelIfExists(CallLogsDataParcel callLogsDataParcel){
        if(callLogsDataParcel == null){
            return;
        }
        int index = indexOfCallLogsDataParcel(callLogsDataParcel);
        if(index != -1){
            getCallLogsDataParcelArrayListInstance().remove(index);
        }
    }
    
    public void updateCallLogsDataParcelIfExists(CallLogsDataParcel callLogsDataParcel){
        if(callLogsDataParcel == null){
            return;
        }
        int index = indexOfCallLogsDataParcel(callLogsDataParcel);
        if(index != -1){
            getCallLogsDataParcelArrayListInstance().set(index, callLogsDataParcel);
        }
    }
    
    public void addAllAtTopCallLogsDataParcel(ArrayList<CallLogsDataParcel> callLogsDataParcels){
        getCallLogsDataParcelArrayListInstance().addAll(0,callLogsDataParcels);
    }
}
