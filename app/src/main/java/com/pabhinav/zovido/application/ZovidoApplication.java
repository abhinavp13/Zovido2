package com.pabhinav.zovido.application;

import android.support.multidex.MultiDexApplication;

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
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.pojo.UploadedLogsDataParcel;
import com.pabhinav.zovido.util.ACRAReportSender;
import com.pabhinav.zovido.util.AnalyticsTrackers;
import com.pabhinav.zovido.util.GoogleCredentialUtils;
import com.pabhinav.zovido.util.SharedPreferencesMap;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.ArrayList;
import java.util.Arrays;

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
public class ZovidoApplication extends MultiDexApplication {

    private  static ZovidoApplication mInstance;
    private  ArrayList<UploadedLogsDataParcel> uploadedLogsDataParcelArrayList;
    private  ArrayList<SavedLogsDataParcel> savedLogsDataParcelArrayList;
    private  ArrayList<CallLogsDataParcel> callLogsDataParcelArrayList;
    private  String agentName;
    private  SQLiteDb sqLiteDb;
    private  boolean loadingTick = false;
    private  GoogleCredential googleCredential;
    private  String spreadsheetKey;
    private  String worksheetName;
    private  SpreadsheetService spreadsheetService;
    private  ArrayList<String> productList;
    private  ArrayList<String> purposeList;
    private  ArrayList<String> sportList;

    @Override
    public void onCreate() {

        /** Initialize ACRA error reports sending library **/
        try {
            ACRA.init(this);
            ACRAReportSender yourSender = new ACRAReportSender();
            ACRA.getErrorReporter().setReportSender(yourSender);
        } catch (Throwable ignored){ /** Nothing can be done at such early stage **/}

        super.onCreate();
        mInstance = this;

        try {
            /** Initialize firebase **/
            Firebase.setAndroidContext(this);

            /** Google Analytics **/
            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        } catch (Throwable ignored){/** Nothing can be done at such early stage **/}
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
        try {
            AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
            return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
        }catch (Throwable ignored){
            return null;
        }
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        try {
            Tracker t = getGoogleAnalyticsTracker();

            // Set screen name.
            t.setScreenName(screenName);

            // Send a screen view.
            t.send(new HitBuilders.ScreenViewBuilder().build());

            GoogleAnalytics.getInstance(this).dispatchLocalHits();
        } catch (Throwable ignored){}
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        try {
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
        }catch (Throwable ignored){}
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        try {
            Tracker t = getGoogleAnalyticsTracker();

            // Build and send an Event.
            t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
        } catch (Throwable ignored){}
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



    /** Category list fetch **/
    public void setProductList(ArrayList<String> productList){
        this.productList = new ArrayList<>(productList);
        
        /** Save product List **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.saveProductList(productList);
    }
    public void setPurposeList(ArrayList<String> purposeList){
        this.purposeList = new ArrayList<>(purposeList);

        /** Save purpose List **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.savePurposeList(purposeList);
    }
    public void setSportList(ArrayList<String> sportList){
        this.sportList = new ArrayList<>(sportList);

        /** Save sport List **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.saveSportList(sportList);
    }

    /** Fetch the category list **/
    public ArrayList<String> getProductList(){
        if(productList == null || productList.size() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            productList = new ArrayList<>(sharedPreferencesMap.loadProductList());
        }
        if(productList == null || productList.size() == 0){
            productList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.product)));
        }
        return productList;
    }
    public ArrayList<String> getPurposeList(){
        if(purposeList == null || purposeList.size() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            purposeList = new ArrayList<>(sharedPreferencesMap.loadPurposeList());
        }
        if(purposeList == null || purposeList.size() == 0){
            purposeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.purpose)));
        }
        return purposeList;
    }
    public ArrayList<String> getSportList(){
        if(sportList == null || sportList.size() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            sportList = new ArrayList<>(sharedPreferencesMap.loadSportList());
        }
        if(sportList == null || sportList.size() == 0){
            sportList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sport)));
        }
        return sportList;
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
