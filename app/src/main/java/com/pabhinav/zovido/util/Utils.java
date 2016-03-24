package com.pabhinav.zovido.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.pabhinav.zovido.alertdialogs.ZovidoAlertMessageDialog;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.pojo.UploadedLogsDataParcel;

/**
 * @author pabhinav
 */
public class Utils {

    /** Hides Keyboard **/
    public static void hideKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    /** Permissions denied by user, ask him to allow again or close the app **/
    public static void needPermissionsDialog(final Context context){

        ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                context,
                "Permissions Denied",
                "Zovido requires following permissions :  READ_CONTACTS, WRITE_TO_EXTERNAL_STORAGE and READ_CALL_LOG. Please go to app settings, then click on permissions, and check all permissions that Zovido demands.",
                "Cancel",
                "App Settings"
        );

        /** Show the alert dialog **/
        zovidoAlertMessageDialog.show();

        /** capture click events **/
        zovidoAlertMessageDialog.setOnAlertButtonClicked(new ZovidoAlertMessageDialog.OnAlertButtonClicked() {
            @Override
            public void onLeftButtonClicked(View v) {
                ((Activity) context).finish();
            }

            @Override
            public void onRightButtonClicked(View v) {

                /** Take user to app setttings **/
                Utils.openAppSettings(context);

                ((Activity) context).finish();
            }
        });
    }

    /** Opens app permission name **/
    public static void openAppSettings(Context context){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /** Returns string value from initialized bundle **/
    public static String getStringFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, String defaultValue){

        if(savedInstanceState == null){
            if(intentExtras == null){
                return defaultValue;
            } else {
                return intentExtras.getString(token);
            }
        } else {
            return (String) savedInstanceState.getSerializable(token);
        }
    }

    /** Returns string value from initialized bundle **/
    public static int getIntFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, int defaultValue){

        if(savedInstanceState == null){
            if(intentExtras == null){
                return defaultValue;
            } else {
                return intentExtras.getInt(token);
            }
        } else {
            return (int) savedInstanceState.getSerializable(token);
        }
    }

    /** Returns boolean value from initialized bundle **/
    public static boolean getBooleanFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, boolean defaultValue){
        if(savedInstanceState == null){
            if(intentExtras == null){
                return defaultValue;
            } else {
                return intentExtras.getBoolean(token);
            }
        } else {
            return (boolean) savedInstanceState.getSerializable(token);
        }
    }

    /** Returns int value from initialized bundle **/
    public static Long getLongFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, Long defaultValue){
        if(savedInstanceState == null){
            if(intentExtras == null){
                return defaultValue;
            } else {
                return intentExtras.getLong(token);
            }
        } else {
            return (Long) savedInstanceState.getSerializable(token);
        }
    }

    /** Give a position of call log in the global arraylist, this method prepares intent to be passed on to other activity  **/
    public static Intent prepareCallLogDataParcelIntent(Context context, int callLogItemPosition, Class activityClassToBeCalled){

        if(ZovidoApplication.getInstance() == null
                || ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size() ==0){
            return null;
        }

        CallLogsDataParcel callLogsDataParcel = ZovidoApplication.getInstance()
                                                    .getCallLogsDataParcelArrayListInstance()
                                                    .get(callLogItemPosition);

        String datetime = callLogsDataParcel.getCallDate();
        String date, time;
        if(datetime.length() >20){
            date = datetime.substring(0,10);
            time = datetime.substring(11,20);
        } else {
            date = "";
            time = datetime;
        }

        Intent intent = new Intent(context, activityClassToBeCalled);
        intent.putExtra(Constants.phoneNumber, callLogsDataParcel.getPhoneNumber());
        intent.putExtra(Constants.date, date);
        intent.putExtra(Constants.time, time);
        intent.putExtra(Constants.name,
                (callLogsDataParcel.getName() == null || callLogsDataParcel.getName().length() ==0)
                        ? "Unknown"
                        : callLogsDataParcel.getName());
        intent.putExtra(Constants.duration, callLogsDataParcel.getCallDuration());

        return intent;
    }

    /** Give a position of call log in the global arraylist, this method prepares intent to be passed on to other activity  **/
    public static Intent prepareSavedLogDataParcelIntent(Context context, int savedLogItemPosition, Class activityClassToBeCalled){

        if(ZovidoApplication.getInstance() == null
                || ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() ==0){
            return null;
        }

        SavedLogsDataParcel savedLogsDataParcel = ZovidoApplication.getInstance()
                .getSavedLogsDataParcelArrayListInstance()
                .get(savedLogItemPosition);

        Intent intent = new Intent(context, activityClassToBeCalled);
        intent.putExtra(Constants.id, savedLogsDataParcel.getId());
        intent.putExtra(Constants.phoneNumber, savedLogsDataParcel.getPhoneNumber());
        intent.putExtra(Constants.duration, savedLogsDataParcel.getDuration());
        intent.putExtra(Constants.date, savedLogsDataParcel.getDate());
        intent.putExtra(Constants.time, savedLogsDataParcel.getTime());
        intent.putExtra(Constants.name, savedLogsDataParcel.getName());
        intent.putExtra(Constants.purpose, savedLogsDataParcel.getPurpose());
        intent.putExtra(Constants.product, savedLogsDataParcel.getProduct());
        intent.putExtra(Constants.sport, savedLogsDataParcel.getSport());
        intent.putExtra(Constants.callRemarks, savedLogsDataParcel.getCallRemarks());
        intent.putExtra(Constants.modifyForm, true);
        intent.putExtra(Constants.indexInSavedLogs, savedLogItemPosition);

        return intent;
    }

    /** Compares call log and uplaoded log data **/
    public static boolean equalsCallAndSavedLog(CallLogsDataParcel callLogsDataParcel,
                                                   SavedLogsDataParcel savedLogsDataParcel) {

        return !(callLogsDataParcel == null || savedLogsDataParcel == null)
                && compareDateTime(
                callLogsDataParcel.getCallDate(),
                savedLogsDataParcel.getDate(),
                savedLogsDataParcel.getTime()
        );

    }

    /** Compares call log and uplaoded log data **/
    public static boolean equalsCallAndUploadedLog(CallLogsDataParcel callLogsDataParcel,
                                                   UploadedLogsDataParcel uploadedLogsDataParcel) {

        return !(callLogsDataParcel == null || uploadedLogsDataParcel == null)
                && compareDateTime(
                callLogsDataParcel.getCallDate(),
                uploadedLogsDataParcel.getDate(),
                uploadedLogsDataParcel.getTime()
        );

    }

    /** Compare date and time for call logs and uploaded logs **/
    private static boolean compareDateTime(String callLogDateTime, String uploadedDate, String uploadedTime) {

        if(uploadedDate == null || uploadedTime == null || callLogDateTime == null){
            return false;
        }

        if(uploadedDate.length() == 0 && uploadedTime.length() == 0){
            return false;
        }

        if(uploadedDate.length() == 0 && uploadedTime.length() != 0){
            if(callLogDateTime.contains(uploadedTime)){
                return true;
            }
        }

        if(uploadedDate.length() != 0 && uploadedTime.length() != 0){
            if(callLogDateTime.contains(uploadedDate) && callLogDateTime.contains(uploadedTime)){
                return true;
            }
        }

        return false;
    }

    /** Removes '+91' from phone number **/
    public static String invalidatePhoneNumber(String number){

        if(number == null || number.length() == 0 || number.length() == 10){
            return number;
        }

        if(number.length() == 12 && number.startsWith("91")){
            return number.substring(2);
        }

        if(number.length() == 13 && number.startsWith("+91")){
            return number.substring(3);
        }

        if(number.length() == 14 && (number.startsWith("+91 ") || number.startsWith("+91-"))){
            return number.substring(4);
        }

        return number;
    }

    /** Transform saved log to uploaded log **/
    public static UploadedLogsDataParcel transformSavedLogToUploadLog(SavedLogsDataParcel savedLogsDataParcel){
        UploadedLogsDataParcel uploadedLogsDataParcel = new UploadedLogsDataParcel();
        uploadedLogsDataParcel.setId(savedLogsDataParcel.getId());
        uploadedLogsDataParcel.setName(savedLogsDataParcel.getName());
        uploadedLogsDataParcel.setPhoneNumber(savedLogsDataParcel.getPhoneNumber());
        uploadedLogsDataParcel.setDuration(savedLogsDataParcel.getDuration());
        uploadedLogsDataParcel.setDate(savedLogsDataParcel.getDate());
        uploadedLogsDataParcel.setTime(savedLogsDataParcel.getTime());
        uploadedLogsDataParcel.setAgentName(savedLogsDataParcel.getAgentName());
        uploadedLogsDataParcel.setProduct(savedLogsDataParcel.getProduct());
        uploadedLogsDataParcel.setPurpose(savedLogsDataParcel.getPurpose());
        uploadedLogsDataParcel.setSport(savedLogsDataParcel.getSport());
        uploadedLogsDataParcel.setCallRemarks(savedLogsDataParcel.getCallRemarks());
        return uploadedLogsDataParcel;
    }

}
