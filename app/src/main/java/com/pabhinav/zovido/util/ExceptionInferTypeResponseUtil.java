package com.pabhinav.zovido.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.view.View;

import com.pabhinav.zovido.alertdialogs.ZovidoAlertMessageDialog;
import com.pabhinav.zovido.application.ZovidoApplication;

/**
 * @author pabhinav
 */
public class ExceptionInferTypeResponseUtil {

    /** Handle Google credential exception infer type **/
    public static void GoogleCredentialExceptionHandler(Context context) {
        if (isHandlerRequired(context)) {
            ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                    context,
                    "Google Credential Problem",
                    "Google rejected to give credentials to your app, something went wrong, reported to developer !",
                    "Cancel",
                    "Ok"
            );
            zovidoAlertMessageDialog.show();
        }
    }

    /** Handle wrong file key exception infer type **/
    public static void WrongFileKeyExceptionHandler(Context context) {
        if (isHandlerRequired(context)) {
            ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                    context,
                    "Wrong File Key",
                    Html.fromHtml("Incorrect File key configured. Please check your spreadsheet file key. Your current configured file key : <u>" + ZovidoApplication.getInstance().getSpreadsheetKey() + "</u>. You can modify file key from app drawer."),
                    "Cancel",
                    "Ok"
            );
            zovidoAlertMessageDialog.show();
        }
    }

    /** Handle wrong file key or no spreadsheet file exception infer type **/
    public static void WrongFileKeyOrNoPermissionExceptionHandler(Context context) {
        if (isHandlerRequired(context)) {
            ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                    context,
                    "SpreadSheet Permission Problem",
                    Html.fromHtml("Either your file key is incorrect or you have not shared spreadsheet with Zovido. Your current configured file key : <u>" + ZovidoApplication.getInstance().getSpreadsheetKey() + "</u>. You can modify file key from app drawer."),
                    "Cancel",
                    "Ok"
            );
            zovidoAlertMessageDialog.show();
        }
    }

    /** Handle no worksheet in spreadsheet file exception infer type **/
    public static void NoWorksheetInSpreadSheetExceptionHandler(Context context) {
        if (isHandlerRequired(context)) {
            ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                    context,
                    "No Worksheet in configured SpreadSheet",
                    Html.fromHtml("Seems like the configured spreadsheet does not have any worksheet. Please check this problem and try again !"),
                    "Cancel",
                    "Ok"
            );
            zovidoAlertMessageDialog.show();
        }
    }

    /** Handle no worksheet name in spreadsheet file exception infer type **/
    public static void NoNameInSpreadSheetExceptionHandler(Context context) {
        if (isHandlerRequired(context)) {
            ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                    context,
                    "No Such WorkSheet Found",
                    Html.fromHtml("Seems like the configured spreadsheet does not have any worksheet with name : <u>" + ZovidoApplication.getInstance().getWorksheetName() + "</u>. Please check this problem and try again !"),
                    "Cancel",
                    "Ok"
            );
            zovidoAlertMessageDialog.show();
        }
    }


    /** Some unknown exception occurred **/
    public static void SomeUnknownExceptionHandler(Context context){
        if(isHandlerRequired(context)){
            ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                    context,
                    "Unknown Exception Occurred",
                    Html.fromHtml("Some unknown exception caught. Please make sure <u>" + ZovidoApplication.getInstance().getWorksheetName() + "</u> worksheet is present in spreadsheet with key : <u>" + ZovidoApplication.getInstance().getSpreadsheetKey() + "</u>, and Zovido has permission to write in this spreadsheet. You can always change these values from app drawer."),
                    "Cancel",
                    "Ok"
            );
            zovidoAlertMessageDialog.show();
        }
    }

    /** if internet not available, no need for actual handler **/
    private static boolean isHandlerRequired(Context context){

        /** check for internet connection **/
        if(isNetworkAvailable(context)){
            return true;
        } else {
            noInternetConnectionHandler(context);
            return false;
        }
    }

    /** Handle no connection event  **/
    public static void noInternetConnectionHandler(Context context){

        ZovidoAlertMessageDialog zovidoAlertMessageDialog = new ZovidoAlertMessageDialog(
                context,
                "No Network",
                "Seems like you lost internet connection or connectivity is too poor to upload",
                "Cancel",
                "Ok"
        );
        zovidoAlertMessageDialog.show();
    }

    /** Check for internet connection **/
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
