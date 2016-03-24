package com.pabhinav.zovido.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pabhinav.zovido.BuildConfig;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.pojo.UploadedLogsDataParcel;
import com.pabhinav.zovido.util.ZLog;

import java.util.ArrayList;

/**
 * @author pabhinav
 */
public class SQLiteDb extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = Integer.parseInt(BuildConfig.DATABASE_VERSION);
    public static final String DATABASE_NAME = BuildConfig.DATABASE_NAME;


    public SQLiteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SavedLogTable.getCreateQuery());
        db.execSQL(UploadedLogTable.getCreateQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SavedLogTable.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + UploadedLogTable.getTableName());

        onCreate(db);
    }



    /**************************************************************************************
     ********************************** Saved logs Items **********************************
     **************************************************************************************/


    /** Insert Saved Lag Data **/
    public Long insertSavedLogData(SavedLogsDataParcel savedLogsDataParcel){
        SQLiteDatabase db = this.getWritableDatabase();

        ZLog.d(SQLiteDb.this, "Insert Saved Log called");

        ContentValues contentValues = SavedLogTable.getContentValues(savedLogsDataParcel);
        return db.insert(SavedLogTable.getTableName(), null, contentValues);

        /** TODO : very important to capture inserted id and set it to id for saved log **/
    }

    /** Update Saved Lag Data **/
    public int updateSavedLogData(SavedLogsDataParcel savedLogsDataParcel){
        SQLiteDatabase db = this.getWritableDatabase();

        ZLog.d(SQLiteDb.this, "Update Saved Log called");

        ContentValues contentValues = SavedLogTable.getContentValues(savedLogsDataParcel);
        return db.update(
                SavedLogTable.getTableName(),
                contentValues,
                SavedLogTable.getKeyId() + " = ?",
                new String[]{String.valueOf(savedLogsDataParcel.getId())}
        );
    }

    /** Delete Saved Log Data **/
    public int deleteSavedLogData(SavedLogsDataParcel savedLogsDataParcel){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                SavedLogTable.getTableName(),
                SavedLogTable.getKeyId() + " = ?",
                new String[]{String.valueOf(savedLogsDataParcel.getId())}
        );
    }

    /** Fetch all saved logs **/
    public void getAllSavedLogsData(){

        String selectQuery = "SELECT  * FROM " + SavedLogTable.getTableName();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor == null){
            if(ZovidoApplication.getInstance() != null){
                ZovidoApplication.getInstance().trackEvent("Error","Null cursor","null cursor while fetching readable database for saved log table");
            }
            return;
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SavedLogsDataParcel dataParcel = new SavedLogsDataParcel();
                dataParcel.setId(Long.parseLong(cursor.getString(0)));
                dataParcel.setName(cursor.getString(1));
                dataParcel.setPhoneNumber(cursor.getString(2));
                dataParcel.setAgentName(cursor.getString(3));
                dataParcel.setDate(cursor.getString(4));
                dataParcel.setTime(cursor.getString(5));
                dataParcel.setDuration(cursor.getString(6));
                dataParcel.setPurpose(cursor.getString(7));
                dataParcel.setProduct(cursor.getString(8));
                dataParcel.setSport(cursor.getString(9));
                dataParcel.setCallRemarks(cursor.getString(10));

                // Adding contact to list
                if(ZovidoApplication.getInstance() != null) {
                    ZovidoApplication.getInstance().addSavedLogsDataParcel(dataParcel);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
    }


//    /** print all saved logs **/
//    public void printAllSavedLogsData(){
//
//        String selectQuery = "SELECT  * FROM " + SavedLogTable.getTableName();
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                SavedLogsDataParcel dataParcel = new SavedLogsDataParcel();
//                dataParcel.setId(Long.parseLong(cursor.getString(0)));
//                dataParcel.setName(cursor.getString(1));
//                dataParcel.setPhoneNumber(cursor.getString(2));
//                dataParcel.setAgentName(cursor.getString(3));
//                dataParcel.setDate(cursor.getString(4));
//                dataParcel.setTime(cursor.getString(5));
//                dataParcel.setDuration(cursor.getString(6));
//                dataParcel.setPurpose(cursor.getString(7));
//                dataParcel.setProduct(cursor.getString(8));
//                dataParcel.setSport(cursor.getString(9));
//                dataParcel.setCallRemarks(cursor.getString(10));
//
//                ZLog.d(SQLiteDb.this, dataParcel.toString());
//
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//    }

    /**************************************************************************************
     ********************************** Uploaded Items ************************************
     **************************************************************************************/


    /** Insert Saved Lag Data **/
    public Long insertUploadedLogData(UploadedLogsDataParcel uploadedLogsDataParcel){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = UploadedLogTable.getContentValues(uploadedLogsDataParcel);
        return db.insert(UploadedLogTable.getTableName(), null, contentValues);
    }

    /** Fetch all uploaded logs **/
    public void getAllUploadedLogsData(){

        String selectQuery = "SELECT  * FROM " + UploadedLogTable.getTableName();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor == null){
            if(ZovidoApplication.getInstance() != null){
                ZovidoApplication.getInstance().trackEvent("Error","Null cursor","null cursor while fetching readable database for saved log table");
            }
            return;
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UploadedLogsDataParcel dataParcel = new UploadedLogsDataParcel();
                dataParcel.setId(Long.parseLong(cursor.getString(0)));
                dataParcel.setName(cursor.getString(1));
                dataParcel.setPhoneNumber(cursor.getString(2));
                dataParcel.setAgentName(cursor.getString(3));
                dataParcel.setDate(cursor.getString(4));
                dataParcel.setTime(cursor.getString(5));
                dataParcel.setDuration(cursor.getString(6));
                dataParcel.setPurpose(cursor.getString(7));
                dataParcel.setProduct(cursor.getString(8));
                dataParcel.setSport(cursor.getString(9));
                dataParcel.setCallRemarks(cursor.getString(10));

                // Adding contact to list
                if(ZovidoApplication.getInstance() != null) {
                    ZovidoApplication.getInstance().addUploadedLogsDataParcel(dataParcel);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

}
