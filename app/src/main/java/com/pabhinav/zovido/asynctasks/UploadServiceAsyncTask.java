package com.pabhinav.zovido.asynctasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.util.ServiceException;
import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.NotificationViewActivity;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.util.Constants;
import com.pabhinav.zovido.util.ExceptionInferTypeResponseUtil;
import com.pabhinav.zovido.util.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author pabhinav
 */
public class UploadServiceAsyncTask extends AsyncTask<Void, Void, Void> {

    /**
     * Context of the calling service.
     */
    private Context context;

    /** Notification related objects **/
    NotificationManager notificationManager;
    Notification notification;
    RemoteViews remoteViews;
    URL listfeedUrl;

    public UploadServiceAsyncTask(Context context, 
                                  NotificationManager notificationManager, 
                                  Notification notification, 
                                  RemoteViews remoteViews,
                                  URL listfeedURL){
        this.context = context;
        this.notification = notification;
        this.notificationManager = notificationManager;
        this.remoteViews = remoteViews;
        this.listfeedUrl = listfeedURL;
    }

    @Override
    protected Void doInBackground(Void... params) {

        if (remoteViews == null
                || notificationManager == null
                || notification == null
                || listfeedUrl == null
                || context == null) {

            return null;
        }

        /** Update notification **/
        showNormalNotification(remoteViews, "Uploading ...");
        remoteViews.setProgressBar(R.id.firstBar, ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size(), 0, false);
        notificationManager.notify(Constants.notificationId, notification);

        SpreadsheetService service = ZovidoApplication.getInstance().getSpreadsheetService();
        if(service == null){
            return null;
        }

        /** Set connect timeout **/
        service.setConnectTimeout(10000);

        /** Need to keep a temp copy and loop over it **/
        ArrayList<SavedLogsDataParcel> tempSavedLogsArrayList = new ArrayList<>(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance());

        /** Loop through every element in saved logs **/
        for (int i = 0; i < tempSavedLogsArrayList.size(); i++) {
            SavedLogsDataParcel savedLogsDataParcel = tempSavedLogsArrayList.get(i);

            ListEntry row = getListFeedRow(savedLogsDataParcel);
            try {
                service.insert(listfeedUrl, row);
            } catch (IOException | ServiceException e) {

                /** If there was a timeout by spreadsheet or there is no internet connection, show error **/
                if ((e.getStackTrace().toString().toLowerCase().contains("timeout") || e.getMessage().toString().toLowerCase().contains("timeout"))
                        || !ExceptionInferTypeResponseUtil.isNetworkAvailable(context)) {

                    /** No Internet connection **/
                    /** update notification **/
                    showErrorNotification(
                            remoteViews,
                            "Uploading Error : Timeout",
                            "Connection Timeout",
                            "Seems like you don't have proper connectivity or server is not responding, please try again later."
                    );
                    notificationManager.notify(Constants.notificationId, notification);
                    return null;
                } else {

                    /** column mismatch show in notification **/
                    showErrorNotification(
                            remoteViews,
                            "Uploading Failed : Column Mismatch",
                            "Columns Mismatch :(",
                            "Seems like your column headers are not what is expected by Zovido. Make sure that your first row has Zovido's database table headings, and then try again."
                    );
                    notificationManager.notify(Constants.notificationId, notification);
                    return null;
                }
            }

            /** DELETE from saved logs global **/
            ZovidoApplication.getInstance().removeSavedLogsDataParcelIfExists(savedLogsDataParcel);
            /** DELETE from saved logs database **/
            ZovidoApplication.getInstance().getSqliteDb().deleteSavedLogData(savedLogsDataParcel);
            /** INSERT into uploaded logs database **/
            ZovidoApplication.getInstance().getSqliteDb().insertUploadedLogData(Utils.transformSavedLogToUploadLog(savedLogsDataParcel));
            /** REMOVE black tick and show blue tick **/
            for (int k = 0; k < ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size(); k++) {
                if (Utils.equalsCallAndSavedLog(ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().get(k), savedLogsDataParcel)) {
                    ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().get(k).setShowTick(false);
                    ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().get(k).setUploadedTick(true);
                    break;
                }
            }

            /** Send broadcast for ui update **/
            Intent intent = new Intent();
            intent.setAction(Constants.savedUIUpdate);
            context.sendBroadcast(intent);

            /** update notification **/
            remoteViews.setProgressBar(R.id.firstBar, ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size(), i, false);
            notificationManager.notify(Constants.notificationId, notification);
        }

        /** Successfully Uploaded **/
        remoteViews.setProgressBar(R.id.firstBar, 100, 100, false);
        showDismissNotification(remoteViews, "Upload Completed Successfully");

        notificationManager.notify(Constants.notificationId, notification);

        return null;
    }

    /** List entry **/
    private ListEntry getListFeedRow(SavedLogsDataParcel savedLogsDataParcel){

        ListEntry row = new ListEntry();
        row.getCustomElements().setValueLocal("Name", savedLogsDataParcel.getName());
        row.getCustomElements().setValueLocal("Phone", Utils.invalidatePhoneNumber(savedLogsDataParcel.getPhoneNumber()));
        row.getCustomElements().setValueLocal("Agent", savedLogsDataParcel.getAgentName());
        row.getCustomElements().setValueLocal("Date", savedLogsDataParcel.getDate());
        row.getCustomElements().setValueLocal("Time", savedLogsDataParcel.getTime());
        row.getCustomElements().setValueLocal("Duration", savedLogsDataParcel.getDuration());
        row.getCustomElements().setValueLocal("Purpose", savedLogsDataParcel.getPurpose());
        row.getCustomElements().setValueLocal("Product", savedLogsDataParcel.getProduct());
        row.getCustomElements().setValueLocal("Sport", savedLogsDataParcel.getSport());
        row.getCustomElements().setValueLocal("Remarks", savedLogsDataParcel.getCallRemarks());

        return row;
    }

    /** pending intents **/
    public PendingIntent createPendingIntentForShowingDialog(String title, String mssg){

        Intent intent = new Intent(context, NotificationViewActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("MESSAGE", mssg);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent createPendingIntentForDismiss(){

        Intent intent1 = new Intent(context, NotificationViewActivity.class);
        intent1.putExtra("DISMISS", true);
        return PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    /** Notifications Updates **/
    public void showErrorNotification(RemoteViews remoteViews, String hintText, String errorTitle, String errorMssg){

        remoteViews.setViewVisibility(R.id.textView16, View.GONE);
        remoteViews.setViewVisibility(R.id.textView17, View.VISIBLE);

        remoteViews.setTextViewText(R.id.hint_below_first_bar, hintText);
        remoteViews.setTextColor(R.id.hint_below_first_bar, context.getResources().getColor(android.R.color.holo_red_light));

        remoteViews.setOnClickPendingIntent(R.id.textView17, createPendingIntentForShowingDialog(errorTitle, errorMssg));
    }

    public void showDismissNotification(RemoteViews remoteViews, String hintText){

        remoteViews.setViewVisibility(R.id.textView16, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.textView17, View.GONE);

        remoteViews.setTextViewText(R.id.hint_below_first_bar, hintText);
        remoteViews.setTextColor(R.id.hint_below_first_bar, context.getResources().getColor(R.color.heading_grey));

        remoteViews.setOnClickPendingIntent(R.id.textView16, createPendingIntentForDismiss());
    }

    public void showNormalNotification(RemoteViews remoteViews, String hintText) {

        remoteViews.setViewVisibility(R.id.textView16, View.GONE);
        remoteViews.setViewVisibility(R.id.textView17, View.GONE);

        remoteViews.setTextViewText(R.id.hint_below_first_bar, hintText);
        remoteViews.setTextColor(R.id.hint_below_first_bar, context.getResources().getColor(R.color.heading_grey));
    }
}