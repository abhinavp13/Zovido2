package com.pabhinav.zovido.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.pabhinav.zovido.activities.CallDetailsActivity;
import com.pabhinav.zovido.adpater.CallLogsRecyclerViewAdapter;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.drawer.NavigationDrawerElements;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.service.FetchUploadedLogsFromDbIntentService;
import com.pabhinav.zovido.util.Utils;

/**
 * @author pabhinav
 */
public class FetchCallLogsReceiver extends BroadcastReceiver {

    private CallLogsRecyclerViewAdapter callLogsRecyclerViewAdapter;
    private CallDetailsActivity callDetailsActivity;

    public FetchCallLogsReceiver(CallLogsRecyclerViewAdapter callLogsRecyclerViewAdapter, CallDetailsActivity callDetailsActivity){
        this.callLogsRecyclerViewAdapter = callLogsRecyclerViewAdapter;
        this.callDetailsActivity = callDetailsActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        /** Update call logs with saved logs tick **/
        for(int j = 0; j < ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size(); j++){
            SavedLogsDataParcel savedLogsDataParcel = ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().get(j);

            for(int i = 0; i< ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size(); i++){
                if(Utils.equalsCallAndSavedLog(ZovidoApplication.getInstance()
                        .getCallLogsDataParcelArrayListInstance().get(i), savedLogsDataParcel)){

                    CallLogsDataParcel callLogsDataParcel = ZovidoApplication.getInstance()
                            .getCallLogsDataParcelArrayListInstance().get(i);
                    callLogsDataParcel.setShowTick(true);

                    ZovidoApplication.getInstance().updateCallLogsDataParcelIfExists(callLogsDataParcel);

                    break;
                }
            }
        }

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

        /** Simply ask for ui update **/
        if(callLogsRecyclerViewAdapter != null){
            callLogsRecyclerViewAdapter.notifyDataSetChanged();
        }


        /** Update drawer count **/
        updateDrawerRecentLogsCount();
    }

    /** Update drawer count **/
    private void updateDrawerRecentLogsCount(){

        if(ZovidoApplication.getInstance() != null && callDetailsActivity != null){
            NavigationDrawerElements navigationDrawerElements = ZovidoApplication.getInstance().getDrawerNavigationViewElements(callDetailsActivity);
            if(navigationDrawerElements != null){
                TextView recentLogCounter = navigationDrawerElements.getRecentLogCounter();
                if (recentLogCounter != null) {
                    recentLogCounter.setText(String.valueOf(
                                    ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size())
                    );
                }
            }
        }
    }
}
