package com.pabhinav.zovido.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.pabhinav.zovido.R;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.asynctasks.UploadServiceAsyncTask;
import com.pabhinav.zovido.util.Constants;

import java.net.URL;

/**
 * @author pabhinav
 */
public class UploadingService extends Service {

    /** Have a static reference for this service **/
    public static UploadingService uploadingService;

    @Override
    public void onCreate(){
        super.onCreate();
        uploadingService = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(ZovidoApplication.getInstance() == null || intent == null || intent.getExtras() == null){
            stopSelf();

            return START_NOT_STICKY;
        }
        URL listFeedUrl = (URL) intent.getExtras().get(Constants.listfeedURL);
        SpreadsheetService service = ZovidoApplication.getInstance().getSpreadsheetService();
        if(service == null || listFeedUrl == null){
            stopSelf();

            return START_NOT_STICKY;
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        android.support.v4.app.NotificationCompat.Builder mBuilder = new android.support.v4.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.noti_icon_white_96)
                .setContent(remoteViews)
                .setAutoCancel(false)
                .setOngoing(true);
        Notification notification = mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.notificationId, notification);

        /** Important for make it live, other wise killed by android os **/
        startForeground(Constants.notificationId, notification);

        /** Async task Execute   **/
        UploadServiceAsyncTask uploadServiceAsyncTask = new UploadServiceAsyncTask(this, notificationManager, notification, remoteViews, listFeedUrl);
        uploadServiceAsyncTask.execute();


        /** Important to start sticky **/
        return START_STICKY;
    }
}
