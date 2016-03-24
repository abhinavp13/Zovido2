package com.pabhinav.zovido.service;

import android.app.IntentService;
import android.content.Intent;

import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.pojo.UploadedLogsDataParcel;
import com.pabhinav.zovido.util.Constants;
import com.pabhinav.zovido.util.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author pabhinav
 */
public class FetchUploadedLogsFromDbIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchUploadedLogsFromDbIntentService(String name) {
        super(name);
    }

    public FetchUploadedLogsFromDbIntentService() {
        super("FetchUploadedLogsFromDbIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Loading started **/
        ZovidoApplication.getInstance().setIsLoadingTick(true);

        /** Fetch data **/
        ZovidoApplication.getInstance().getSqliteDb().getAllUploadedLogsData();

        /** Loading completed **/
        ZovidoApplication.getInstance().setIsLoadingTick(false);

        /** Send Broadcast **/
        sendBroadcastForUpdation();
    }

    /** Sends broadcast for updating call log recycler view  **/
    private void sendBroadcastForUpdation(){

        /** Send Broadcast **/
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.PROCESS_RESPONSE_UPLOADED_LOG);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);
    }
}
