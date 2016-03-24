package com.pabhinav.zovido.dao;

import android.content.ContentValues;

import com.pabhinav.zovido.pojo.SavedLogsDataParcel;

/**
 * @author pabhinav
 */
public class SavedLogTable {

    private static final String TABLE_NAME = "savedLogTable";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_AG_NAME = "agent_name";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_PRP = "purpose";
    private static final String KEY_PRT = "product";
    private static final String KEY_SPT = "sport";
    private static final String KEY_REMARKS = "call_remarks";

    public static String getKeyId(){
        return KEY_ID;
    }

    public static String getTableName(){
        return TABLE_NAME;
    }

    public static String getCreateQuery(){

        return "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT,"
                + KEY_AG_NAME + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_DURATION + " TEXT,"
                + KEY_PRP + " TEXT,"
                + KEY_PRT + " TEXT,"
                + KEY_SPT + " TEXT,"
                + KEY_REMARKS + " TEXT"
                + ")";
    }

    public static ContentValues getContentValues(SavedLogsDataParcel dataParcel){
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, dataParcel.getName());
        values.put(KEY_PH_NO, dataParcel.getPhoneNumber());
        values.put(KEY_AG_NAME , dataParcel.getAgentName());
        values.put(KEY_DATE , dataParcel.getDate());
        values.put(KEY_TIME , dataParcel.getTime());
        values.put(KEY_DURATION ,dataParcel.getDuration());
        values.put(KEY_PRP , dataParcel.getPurpose());
        values.put(KEY_PRT , dataParcel.getProduct());
        values.put(KEY_SPT, dataParcel.getSport());
        values.put(KEY_REMARKS, dataParcel.getCallRemarks());

        return values;
    }
}
