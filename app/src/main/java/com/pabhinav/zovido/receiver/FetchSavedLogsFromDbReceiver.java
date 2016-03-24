package com.pabhinav.zovido.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.CallDetailsActivity;
import com.pabhinav.zovido.adpater.SavedLogsRecyclerViewAdapter;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.util.Utils;

/**
 * @author pabhinav
 */
public class FetchSavedLogsFromDbReceiver extends BroadcastReceiver {

    SavedLogsRecyclerViewAdapter savedLogsRecyclerViewAdapter;
    CallDetailsActivity callDetailsActivity;

    public FetchSavedLogsFromDbReceiver(SavedLogsRecyclerViewAdapter savedLogsRecyclerViewAdapter, CallDetailsActivity callDetailsActivity){
        this.savedLogsRecyclerViewAdapter = savedLogsRecyclerViewAdapter;
        this.callDetailsActivity = callDetailsActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Update tick in call logs **/
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

        /** Make sure we show no saved logs, when we got empty saved logs **/
        if(callDetailsActivity != null){
            if(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() == 0){
                TextView loadingTextView = (TextView)(callDetailsActivity.findViewById(R.id.saved_log_loading_text_view_behind));
                loadingTextView.setText("No Saved Logs");
                loadingTextView.setVisibility(View.VISIBLE);
                callDetailsActivity.findViewById(R.id.empty_image).setVisibility(View.VISIBLE);
                callDetailsActivity.findViewById(R.id.avloadingIndicatorViewSavedLog).setVisibility(View.INVISIBLE);
            }
            if(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() > 0){

                /** Loading Views visibility **/
                TextView loadingTextView = (TextView)(callDetailsActivity.findViewById(R.id.saved_log_loading_text_view_behind));
                loadingTextView.setText("Loading Saved Logs");
                loadingTextView.setVisibility(View.INVISIBLE);
                callDetailsActivity.findViewById(R.id.empty_image).setVisibility(View.INVISIBLE);
                callDetailsActivity.findViewById(R.id.avloadingIndicatorViewSavedLog).setVisibility(View.INVISIBLE);
            }
        }

        /** Update adapter **/
        if(savedLogsRecyclerViewAdapter != null){
            savedLogsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
