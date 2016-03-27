package com.pabhinav.zovido.service;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.util.Constants;
import com.pabhinav.zovido.util.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author pabhinav
 */
public class FetchCallLogsIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchCallLogsIntentService(String name) {
        super(name);
    }

    /** Required constructor **/
    public FetchCallLogsIntentService(){
        super("FetchCallLogsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        /** Get the top most call log **/
        CallLogsDataParcel topCallLog = null;
        if (ZovidoApplication.getInstance() != null) {
            if(!(ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size() == 0)){
                topCallLog = ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().get(0);
            }
        }

        Cursor managedCursor = null;
        try {
            /** Get the cursor **/
            managedCursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    null,
                    null,
                    CallLog.Calls.DATE + " DESC"
            );
        } catch (SecurityException e){

            if(ZovidoApplication.getInstance() != null){
                ZovidoApplication.getInstance().trackException(e);

                /** Permissions required **/
            }

            return;
        }

        if(managedCursor == null){
            if(ZovidoApplication.getInstance() != null){
                ZovidoApplication.getInstance().trackEvent("Error", "Cursor Problem","Permissions present but cursor returned for fetching call logs is null");
            }
            return;
        }


        /** Now, we have the top call log already in recyclerview and a managed cursor for reading call logs **/
        /** Let's start fetching call logs data **/
        fetchCallLogs(managedCursor, topCallLog);

    }

    /**
     * Tries to fetch call log data given cursor and a top call log data currently in recycler view.
     *
     * @param managedCursor Cursor object for fetching call logs
     * @param topCallLog call log data currently at top of call log recycler view
     */
    private void fetchCallLogs(@NonNull Cursor managedCursor, CallLogsDataParcel topCallLog){

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        int counter = 0;

        /** When top call log is empty **/
        ArrayList<CallLogsDataParcel> tempCallLogsDataParcelArrayList = new ArrayList<>();

        /** Runs loop till 500 logs **/
        while(managedCursor.moveToNext() && counter <500){

            CallLogsDataParcel callLogsDataParcel = new CallLogsDataParcel();

            callLogsDataParcel.setPhoneNumber(getPhoneNumber(managedCursor, number));
            callLogsDataParcel.setCallDuration(getCallDuration(managedCursor, duration));
            callLogsDataParcel.setCallType(getCallType(managedCursor, type));
            callLogsDataParcel.setName(getName(managedCursor, number));
            callLogsDataParcel.setCallDate(getCallDate(managedCursor, date));

            /** Not a valid call log, continue to next one **/
            if(!validated(callLogsDataParcel)){
                continue;
            }


            /** If top call is not null, this means we need to add data on the top of recycler view adapter items **/
            if(topCallLog != null) {

                /** top call log matches **/
                if (topCallLog.equals(callLogsDataParcel)) {

                    /** Time to add all caught data to top of list, and send broadcast to update ui **/
                    if(ZovidoApplication.getInstance() != null && tempCallLogsDataParcelArrayList.size() >0){
                        ZovidoApplication.getInstance().addAllAtTopCallLogsDataParcel(tempCallLogsDataParcelArrayList);
                        sendBroadcastForUpdation();
                    }
                    break;
                }

                /** If data does not matches not in the list, add it temporarily in an arraylist **/
                tempCallLogsDataParcelArrayList.add(callLogsDataParcel);
            }

            /** Nothing on top of call logs, means we need to fetch complete data, and add one by one at the bottom of list **/
            if(topCallLog == null) {

                /** Add data to call logs **/
                if (ZovidoApplication.getInstance() != null) {
                    ZovidoApplication.getInstance().addCallLogsDataParcel(callLogsDataParcel);
                }

                /** Update recycler view adapter by sending broadcast **/
                if(counter != 0 && counter % 50 ==0){
                    sendBroadcastForUpdation();
                }
            }

            /** increase counter **/
            counter++;
        }

        /** Update recycler view adapter by sending broadcast **/
        sendBroadcastForUpdation();

        /** Important to free resources **/
        managedCursor.close();
    }

    /** Get the call type **/
    private String getCallType(@NonNull Cursor managedCursor, int callTypeColumnIndex){
        String dir = null;
        int callTypeCode = Integer.parseInt(managedCursor.getString(callTypeColumnIndex));
        switch (callTypeCode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = Constants.outgoing;
                break;

            case CallLog.Calls.INCOMING_TYPE:
                dir = Constants.incoming;
                break;

            case CallLog.Calls.MISSED_TYPE:
                dir = Constants.missed;
                break;
        }
        return dir;
    }

    /** Get the name **/
    private String getName(@NonNull Cursor managedCursor, int numberColumnIndex){

        String name;
        try {
            name = getContactName(managedCursor.getString(numberColumnIndex));
        }catch (Exception e){

            if(ZovidoApplication.getInstance() != null){
                ZovidoApplication.getInstance().trackException(e);
            }

            return "";
        }
        return name;
    }

    /** Get phone number **/
    private String getPhoneNumber(@NonNull Cursor managedCursor, int numberColumnIndex){
        return managedCursor.getString(numberColumnIndex);
    }

    /** Get call date **/
    private String getCallDate(@NonNull Cursor managedCursor, int dateColumnIndex){
        return new Date(Long.valueOf(managedCursor.getString(dateColumnIndex))).toString();
    }

    /** Get call duration **/
    private String getCallDuration(@NonNull Cursor managedCursor, int durationColumnIndex){

        String callDuration = managedCursor.getString(durationColumnIndex);
        int totalSecs = Integer.parseInt(callDuration);

        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        if(hours == 0 && minutes == 0){
            callDuration = String.format("%02d sec",seconds);
        } else if(hours == 0){
            callDuration = String.format("%02d min %02d sec", minutes, seconds);
        } else {
            callDuration = String.format("%02d hour %02d min %02d sec", hours, minutes, seconds);
        }
        return callDuration;
    }


    /** Tries to fetch contact name given phone number **/
    private String getContactName(String phoneNumber) throws Exception {

        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    /** Sends broadcast for updating call log recycler view  **/
    private void sendBroadcastForUpdation(){

        /** Send Broadcast **/
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);
    }

    /** Validates for a valid call log entry **/
    private boolean validated(CallLogsDataParcel callDataObjectParcel){

        return callDataObjectParcel.getCallDate() != null
                && callDataObjectParcel.getCallDuration() != null
                && callDataObjectParcel.getCallType() != null
                && callDataObjectParcel.getPhoneNumber() != null;
    }
}
