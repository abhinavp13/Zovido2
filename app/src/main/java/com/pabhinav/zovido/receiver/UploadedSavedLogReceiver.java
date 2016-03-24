package com.pabhinav.zovido.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.CallDetailsActivity;
import com.pabhinav.zovido.adpater.SavedLogsRecyclerViewAdapter;
import com.pabhinav.zovido.application.ZovidoApplication;

/**
 * @author pabhinav
 */
public class UploadedSavedLogReceiver extends BroadcastReceiver{

    private SavedLogsRecyclerViewAdapter savedLogsRecyclerViewAdapter;

    public UploadedSavedLogReceiver(SavedLogsRecyclerViewAdapter savedLogsRecyclerViewAdapter, Context context){
        this.savedLogsRecyclerViewAdapter = savedLogsRecyclerViewAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        /** If adapter is empty....show no saved logs **/
        if(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() == 0 && context != null){
            TextView loadingTextView = (TextView)(((Activity)context).findViewById(R.id.saved_log_loading_text_view_behind));
            loadingTextView.setText("No Saved Logs");
            loadingTextView.setVisibility(View.VISIBLE);
            ((Activity)context).findViewById(R.id.empty_image).setVisibility(View.VISIBLE);
            ((Activity)context).findViewById(R.id.avloadingIndicatorViewSavedLog).setVisibility(View.INVISIBLE);
        }

        /** update saved log counter in drawer **/
        if(context != null){
            ZovidoApplication.getInstance().getDrawerNavigationViewElements((CallDetailsActivity) context).getSavedLogCounter().setText(String.valueOf(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size()));
        }

        /** If saved log recycler view adapter **/
        if(savedLogsRecyclerViewAdapter != null){
            savedLogsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
