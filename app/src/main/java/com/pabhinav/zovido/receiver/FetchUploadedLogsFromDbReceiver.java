package com.pabhinav.zovido.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pabhinav.zovido.adpater.CallLogsRecyclerViewAdapter;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.util.Utils;

/**
 * @author pabhinav
 */
public class FetchUploadedLogsFromDbReceiver extends BroadcastReceiver {

    CallLogsRecyclerViewAdapter callLogsRecyclerViewAdapter;

    public FetchUploadedLogsFromDbReceiver(CallLogsRecyclerViewAdapter callLogsRecyclerViewAdapter){
        this.callLogsRecyclerViewAdapter = callLogsRecyclerViewAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        /** Upload call logs with uploaded ticks **/
        for(int i = 0; i< ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size(); i++){

            CallLogsDataParcel callLogsDataParcel = ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().get(i);

            for(int j = 0; j < ZovidoApplication.getInstance().getUploadedLogsDataParcelArrayListInstance().size(); j++){
                if(Utils.equalsCallAndUploadedLog(callLogsDataParcel, ZovidoApplication.getInstance().getUploadedLogsDataParcelArrayListInstance().get(j))){
                    callLogsDataParcel.setUploadedTick(true);
                    break;
                }
            }

        }

        if(callLogsRecyclerViewAdapter != null){
            callLogsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
