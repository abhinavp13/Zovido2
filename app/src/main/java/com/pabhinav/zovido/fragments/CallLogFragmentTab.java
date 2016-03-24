package com.pabhinav.zovido.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pabhinav.zovido.activities.CallDetailsActivity;
import com.pabhinav.zovido.adpater.CallLogsRecyclerViewAdapter;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.R;
import com.pabhinav.zovido.receiver.FetchCallLogsReceiver;
import com.pabhinav.zovido.receiver.FetchUploadedLogsFromDbReceiver;
import com.pabhinav.zovido.service.FetchCallLogsIntentService;
import com.pabhinav.zovido.service.FetchUploadedLogsFromDbIntentService;
import com.pabhinav.zovido.util.Constants;

/**
 * @author pabhinav
 */
public class CallLogFragmentTab  extends Fragment {

    /**
     * FetchCallLogsService receiver.
     */
    private FetchCallLogsReceiver fetchCallLogsReceiver;

    /**
     * Fetch Uploaded logs receiver.
     */
    private FetchUploadedLogsFromDbReceiver fetchUploadedLogsFromDbReceiver;

    /**
     * Adapter
     */
    public CallLogsRecyclerViewAdapter callLogsRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_call_log, container, false);

        /** Setup recycler view and its adapter **/
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.call_logs_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        callLogsRecyclerViewAdapter = new CallLogsRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(callLogsRecyclerViewAdapter);

        /** Pass adapter to receiver **/
        fetchCallLogsReceiver = new FetchCallLogsReceiver(callLogsRecyclerViewAdapter, (CallDetailsActivity)getActivity());
        fetchUploadedLogsFromDbReceiver = new FetchUploadedLogsFromDbReceiver(callLogsRecyclerViewAdapter);

        /** register uploaded data fetch service **/
        registerAndStartUploadedItemsDataFetch();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        /** Notify data set changed, due to changes caused by feed back activity **/
        callLogsRecyclerViewAdapter.notifyDataSetChanged();

        /** Tracking the screen view **/
        if(ZovidoApplication.getInstance() != null) {
            ZovidoApplication.getInstance().trackScreenView("CallLog Fragment");
        }

        /** Start Service for a new call log entry occurrence **/
        registerAndStartService();
    }

    /** register and start uploaded data fetch service **/
    private void registerAndStartUploadedItemsDataFetch(){

        IntentFilter filter = new IntentFilter(Constants.PROCESS_RESPONSE_UPLOADED_LOG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(fetchUploadedLogsFromDbReceiver, filter);

        Intent intent = new Intent(getActivity(), FetchUploadedLogsFromDbIntentService.class);
        getActivity().startService(intent);
    }

    /** Registers call log fetching service and start it **/
    private void registerAndStartService(){

        IntentFilter filter = new IntentFilter(Constants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(fetchCallLogsReceiver, filter);

        Intent intent = new Intent(getActivity(), FetchCallLogsIntentService.class);
        getActivity().startService(intent);
    }

    @Override
    public void onDestroy() {
        if(getActivity() != null) {
            getActivity().unregisterReceiver(fetchCallLogsReceiver);
            getActivity().unregisterReceiver(fetchUploadedLogsFromDbReceiver);
        }
        super.onDestroy();
    }

}
