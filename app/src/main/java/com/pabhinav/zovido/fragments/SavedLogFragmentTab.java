package com.pabhinav.zovido.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pabhinav.zovido.activities.CallDetailsActivity;
import com.pabhinav.zovido.adpater.CallLogsRecyclerViewAdapter;
import com.pabhinav.zovido.adpater.SavedLogsRecyclerViewAdapter;
import com.pabhinav.zovido.alertdialogs.ZovidoAlertMessageDialog;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.R;
import com.pabhinav.zovido.asynctasks.ListFeedSpreadsheetAsyncTask;
import com.pabhinav.zovido.asynctasks.UploadServiceAsyncTask;
import com.pabhinav.zovido.pojo.ExceptionInferType;
import com.pabhinav.zovido.receiver.FetchSavedLogsFromDbReceiver;
import com.pabhinav.zovido.receiver.UploadedSavedLogReceiver;
import com.pabhinav.zovido.service.FetchCallLogsIntentService;
import com.pabhinav.zovido.service.FetchSavedLogsFromDbIntentService;
import com.pabhinav.zovido.util.Constants;
import com.pabhinav.zovido.util.DirectoryChoserDialog;
import com.pabhinav.zovido.util.ExcelCreator;
import com.pabhinav.zovido.util.ExceptionInferTypeResponseUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static jxl.Workbook.createWorkbook;

/**
 * @author pabhinav
 */
public class SavedLogFragmentTab extends Fragment {

    /** Receiver **/
    FetchSavedLogsFromDbReceiver fetchSavedLogsFromDbReceiver;
    UploadedSavedLogReceiver uploadedSavedLogReceiver;

    /** Adapter **/
    public SavedLogsRecyclerViewAdapter savedLogsRecyclerViewAdapter;

    /** Loading Views  **/
    private TextView loadingTextView;
    private ImageView emptyImageView;
    private View avLoadingIndicator;
    private TextView savedCounter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_saved_log, container, false);

        /** Setup recycler view and its adapter **/
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.saved_logs_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        savedLogsRecyclerViewAdapter = new SavedLogsRecyclerViewAdapter(getActivity(), rootView);
        recyclerView.setAdapter(savedLogsRecyclerViewAdapter);

        /** Register and start service **/
        fetchSavedLogsFromDbReceiver = new FetchSavedLogsFromDbReceiver(savedLogsRecyclerViewAdapter, (CallDetailsActivity) getActivity());
        registerAndStartSavedLogFetch();

        /** Register **/
        uploadedSavedLogReceiver = new UploadedSavedLogReceiver(savedLogsRecyclerViewAdapter, getActivity());
        registerUploadedSavedLogReceiver();

        /** Loading views **/
        loadingTextView = (TextView)(rootView.findViewById(R.id.saved_log_loading_text_view_behind));
        emptyImageView = (ImageView)(rootView.findViewById(R.id.empty_image));
        avLoadingIndicator = rootView.findViewById(R.id.avloadingIndicatorViewSavedLog);
        savedCounter = ZovidoApplication.getInstance().getDrawerNavigationViewElements((CallDetailsActivity)getActivity()).getSavedLogCounter();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Notify data set changed, due to changes caused by feed back activity **/
        savedLogsRecyclerViewAdapter.notifyDataSetChanged();

        /** Update loading views **/
        if(savedLogsRecyclerViewAdapter.getItemCount() > 0){

            /** Loading Views visibility **/
            loadingTextView.setText("Loading Saved Logs");
            loadingTextView.setVisibility(View.INVISIBLE);
            emptyImageView.setVisibility(View.INVISIBLE);
            avLoadingIndicator.setVisibility(View.INVISIBLE);

            /** Update saved logs counter **/
            savedCounter.setText(String.valueOf(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size()));
        }


        /** Tracking the screen view **/
        ZovidoApplication.getInstance().trackScreenView("SavedLog Fragment");
    }

    /** Registers call log fetching service and start it **/
    private void registerAndStartSavedLogFetch(){

        IntentFilter filter = new IntentFilter(Constants.PROCESS_RESPONSE_SAVED_LOG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(fetchSavedLogsFromDbReceiver, filter);

        Intent intent = new Intent(getActivity(), FetchSavedLogsFromDbIntentService.class);
        getActivity().startService(intent);
    }

    /** Register uploaded saved log receiver **/
    private void registerUploadedSavedLogReceiver(){

        IntentFilter filter = new IntentFilter(Constants.savedUIUpdate);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(uploadedSavedLogReceiver, filter);
    }

    /**
     * upload button clicked
     *
     * It returns true if toggling of upload action menu is required.
     */
    public boolean uploadClicked(){
        if(ZovidoApplication.getInstance() == null){
            return false;
        }

        /** Check whether there is something to export **/
        if(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() == 0){
            ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                    getActivity(),
                    "No Saved Log",
                    "You don't have any call log saved. Go to call logs tab, click on any call log tile, enter some details and save it.",
                    "Cancel",
                    "Ok, Got it"
            );
            zovidoAlertMessageDialog.show();
            zovidoAlertMessageDialog.setOnAlertButtonClicked(new ZovidoAlertMessageDialog.OnAlertButtonClicked() {
                @Override
                public void onLeftButtonClicked(View v) {}

                @Override
                public void onRightButtonClicked(View v) {}
            });

            return false;
        }

        return true;
    }

    /** local export required **/
    public void localExport(){

        /** Try to get the worksheet labels **/
        Label[][] labels = null;
        final View fabView = getActivity().findViewById(R.id.fab_upload);
        try {
            labels = ExcelCreator.createExcelLabels();
        } catch (Exception e){

            /** Could not create excel **/
            final Snackbar snackBar = Snackbar.make(fabView, "Labels creation operation not possible", Snackbar.LENGTH_LONG);
            snackBar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackBar.dismiss();
                }
            } );
            snackBar.show();
            if(ZovidoApplication.getInstance() != null){
                ZovidoApplication.getInstance().trackException(e);
            }
            return;
        }

        /** Choose File explorer for saving excel to local storage **/
        final Label[][] finalLabels = labels;
        DirectoryChoserDialog directoryChooserDialog = new DirectoryChoserDialog(getActivity(), new DirectoryChoserDialog.ChosenDirectoryListener() {
            @Override
            public void onChosenDir(String chosenDir) {

                long timeInMillis = System.currentTimeMillis();
                String date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US).format(new Date(timeInMillis));
                date = date.replace(" ","::");
                File file = new File(chosenDir, "Zovido-"+ date + ".xls");

                try {
                    WritableWorkbook wb = createWorkbook(file);
                    WritableSheet writableSheet = wb.createSheet("Saved Calls Details", 0);
                    for(int i = 0; i< ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() + 1; i++){
                        for (int j = 0; j<ExcelCreator.numberOfColumns; j++){
                            writableSheet.addCell(finalLabels[j][i]);
                        }
                    }
                    wb.write();
                    wb.close();

                    /** Successfully written **/
                    final Snackbar snackBar = Snackbar.make(fabView, "File Successfully Written !", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackBar.dismiss();
                                }
                            } );
                    snackBar.show();
                } catch (Exception e){

                    /** Unable to write **/
                    final Snackbar snackBar = Snackbar.make(fabView, "Unable to write in the directory specified", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                        }
                    } );
                    snackBar.show();
                }
            }
        });

        /** Set new folder button **/
        directoryChooserDialog.setNewFolderEnabled(true);
        /** Set selected folder as sdcard directory **/
        directoryChooserDialog.chooseDirectory("");

    }

    /** export to cloud, (spreadsheet for now) **/
    public void cloudExport(){

        /** Started Uploading **/
        final View fabView = getActivity().findViewById(R.id.fab_upload);
        final Snackbar snackBar = Snackbar.make(fabView, "Uploading Started ... ", Snackbar.LENGTH_LONG);
        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        } );
        snackBar.show();

        ListFeedSpreadsheetAsyncTask listFeedSpreadsheetAsyncTask = new ListFeedSpreadsheetAsyncTask(getContext());
        listFeedSpreadsheetAsyncTask.execute();
        listFeedSpreadsheetAsyncTask.setListFeedSpreadsheetAsyncTaskEvents(new ListFeedSpreadsheetAsyncTask.ListFeedSpreadsheetAsyncTaskEvents() {
            @Override
            public void onTaskFailure(ExceptionInferType exceptionInferType) {
                switch (exceptionInferType){
                    case GOOGLE_CREDENTIAL_EXCEPTION:
                        ExceptionInferTypeResponseUtil.GoogleCredentialExceptionHandler(getContext());
                        break;
                    case URL_EXCEPTION_WRONG_FILE_KEY:
                        ExceptionInferTypeResponseUtil.WrongFileKeyExceptionHandler(getContext());
                        break;
                    case WRONG_FILE_KEY_OR_NO_FILE_PERMISSION:
                        ExceptionInferTypeResponseUtil.WrongFileKeyOrNoPermissionExceptionHandler(getContext());
                        break;
                    case NO_WORKSHEET_IN_SPREADSHEET:
                        ExceptionInferTypeResponseUtil.NoWorksheetInSpreadSheetExceptionHandler(getContext());
                        break;
                    case NO_WORKSHEET_WITH_SPECIFIED_NAME_FOUND:
                        ExceptionInferTypeResponseUtil.NoNameInSpreadSheetExceptionHandler(getContext());
                        break;
                    case WORKSHEET_FEED_NULL:
                    case NULL_LIST_FEED_URL_FROM_WORKSHEET:
                        ExceptionInferTypeResponseUtil.SomeUnknownExceptionHandler(getContext());
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroy(){
        if(getContext() != null){
            getContext().unregisterReceiver(fetchSavedLogsFromDbReceiver);
            getActivity().unregisterReceiver(uploadedSavedLogReceiver);
        }
        super.onDestroy();
    }
}