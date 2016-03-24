package com.pabhinav.zovido.service;

import android.app.IntentService;
import android.content.Intent;

import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.util.Constants;

/**
 * @author pabhinav
 */
public class FetchSavedLogsFromDbIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchSavedLogsFromDbIntentService(String name) {
        super(name);
    }

    public FetchSavedLogsFromDbIntentService(){
        super("FetchSavedLogsFromDbIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Fetch all the saved logs **/
        ZovidoApplication.getInstance().getSqliteDb().getAllSavedLogsData();

        /** Send Broadcast **/
        sendBroadcastForUpdation();
    }

    /** Sends broadcast for updating call log recycler view  **/
    private void sendBroadcastForUpdation(){

        /** Send Broadcast **/
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.PROCESS_RESPONSE_SAVED_LOG);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);
    }
}
