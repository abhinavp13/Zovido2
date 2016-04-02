package com.pabhinav.zovido.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.ExceptionInferType;
import com.pabhinav.zovido.service.UploadingService;
import com.pabhinav.zovido.util.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Plz do remember, service account needs to be made from developer console and then
 * .p12 format certificate file needs to be added in credentials for OAuth2.0
 * Also, remember your service account email address. Each spreadsheet accessed using below
 * method must have shared spreadsheet to this service account email id.
 *
 * @author pabhinav
 */
public class ListFeedSpreadsheetAsyncTask extends AsyncTask<Void, URL, ExceptionInferType> {

    /**
     * Calling activity context
     */
    private Context context;

    /**
     * Callback interface
     */
    private ListFeedSpreadsheetAsyncTaskEvents listFeedSpreadsheetAsyncTaskEvents;

    /**
     * Constructor for this class
     *
     * @param context of the calling activity
     */
    public ListFeedSpreadsheetAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected ExceptionInferType doInBackground(Void... params) {

        if(ZovidoApplication.getInstance() == null){
            return ExceptionInferType.ZOVIDO_NO_INSTANCE;
        }

        /** Set OAuth2Credential for spreadsheet connection **/
        GoogleCredential googleCredential = ZovidoApplication.getInstance().getGoogleCredential();
        if(googleCredential == null){
            return ExceptionInferType.GOOGLE_CREDENTIAL_EXCEPTION;
        }
        SpreadsheetService spreadsheetService = new SpreadsheetService("MySpreadsheetIntegration-v1");
        spreadsheetService.setOAuth2Credentials(googleCredential);
        ZovidoApplication.getInstance().setSpreadsheetService(spreadsheetService);

        /** try to prepare spreadsheet feed url **/
        URL SPREADSHEET_FEED_URL;
        try {
            SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/worksheets/"+ ZovidoApplication.getInstance().getSpreadsheetKey() +"/private/full");
        } catch (MalformedURLException e) {

            ZovidoApplication.getInstance().trackEvent(
                    "Error",
                    "Wrong URL",
                    "https://spreadsheets.google.com/feeds/worksheets/"+ ZovidoApplication.getInstance().getSpreadsheetKey() +"/private/full"
            );

            ZovidoApplication.getInstance().trackException(e);

            return ExceptionInferType.URL_EXCEPTION_WRONG_FILE_KEY;
        }


        /** try to get worksheets feed **/
        WorksheetFeed worksheetFeed;
        try {
            worksheetFeed = spreadsheetService.getFeed(SPREADSHEET_FEED_URL, WorksheetFeed.class);
        } catch (IOException | ServiceException e) {

            ZovidoApplication.getInstance().trackException(e);

            return ExceptionInferType.WRONG_FILE_KEY_OR_NO_FILE_PERMISSION;
        }

        /** Try to get worksheets feed entries **/
        List<WorksheetEntry> worksheets;
        if (worksheetFeed != null) {
            worksheets = worksheetFeed.getEntries();
        } else {
            ZovidoApplication.getInstance().trackEvent("Error", "Worksheet feed null", "Can't say for reason of this");
            return ExceptionInferType.WORKSHEET_FEED_NULL;
        }

        /** Try to get the specified worksheet **/
        WorksheetEntry worksheet = null;
        if (worksheets != null) {
            for(WorksheetEntry worksheetEntry : worksheets){
                if(worksheetEntry.getTitle().getPlainText().equals(ZovidoApplication.getInstance().getWorksheetName())){
                    worksheet = worksheetEntry;
                    break;
                }
            }
        } else {
            ZovidoApplication.getInstance().trackEvent("Error", "No Worksheet found", "There is no worksheet in the given spreadsheet");
            return ExceptionInferType.NO_WORKSHEET_IN_SPREADSHEET;
        }

        /** Worksheet of the specified name not found **/
        if(worksheet == null){
            ZovidoApplication.getInstance().trackEvent("Error", "No Worksheet found", "No worksheet with specified name found");
            return ExceptionInferType.NO_WORKSHEET_WITH_SPECIFIED_NAME_FOUND;
        }

        /** finally, try to get the list feed url **/
        URL listFeedUrl = worksheet.getListFeedUrl();
        if(listFeedUrl == null){
            return ExceptionInferType.NULL_LIST_FEED_URL_FROM_WORKSHEET;
        }

        /** publish to ui **/
        publishProgress(listFeedUrl);

        /** No exception occurred **/
        return ExceptionInferType.NO_EXCEPTION;
    }

    @Override
    protected void onProgressUpdate(URL... url) {

        if(context == null){
            return;
        }

        /** Everything checked, Time to start the uploading service **/
        Intent intent = new Intent(context, UploadingService.class);
        intent.putExtra(Constants.listfeedURL, url[0]);
        ((Activity) context).startService(intent);
    }


    @Override
    protected void onPostExecute(ExceptionInferType exceptionInferType){

        /** If there is some exception, report back **/
        if(!exceptionInferType.equals(ExceptionInferType.NO_EXCEPTION)){
            if(listFeedSpreadsheetAsyncTaskEvents != null){
                listFeedSpreadsheetAsyncTaskEvents.onTaskFailure(exceptionInferType);
            }
        }
    }

    /** Set call back interface **/
    public void setListFeedSpreadsheetAsyncTaskEvents(ListFeedSpreadsheetAsyncTaskEvents listFeedSpreadsheetAsyncTaskEvents) {
        this.listFeedSpreadsheetAsyncTaskEvents = listFeedSpreadsheetAsyncTaskEvents;
    }

    /** Callback events for this class **/
    public interface ListFeedSpreadsheetAsyncTaskEvents{
        void onTaskFailure(ExceptionInferType exceptionInferType);
    }
}
