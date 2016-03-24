package com.pabhinav.zovido.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author pabhinav
 */
public class SharedPreferencesMap {

    private Context context;

    public SharedPreferencesMap(Context context){
        this.context = context.getApplicationContext();
    }

    public void saveAcraMap(Map<String,String> inputMap){

        SharedPreferences pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            if(editor != null) {
                editor.remove(Constants.acraMap).commit();
                editor.putString(Constants.acraMap, jsonString);
                editor.commit();
            }
        }
    }

    public Map<String,String> loadAcraMap(){

        Map<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString(Constants.acraMap, (new JSONObject()).toString());
                if(jsonString != null && jsonString.length() >0) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = (String)(jsonObject.get(key));
                        outputMap.put(key, value);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

        return outputMap;
    }


    public void saveFeedback(String feedback){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor!=null) {
                editor.remove(Constants.feedbackMap).commit();
                editor.putString(Constants.feedbackMap, feedback);
                editor.commit();
            }
        }
    }

    public String loadFeedback(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.feedbackMap, null);
        } else {
            return null;
        }
    }

    public void removeFeedback(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor!=null) {
                editor.remove(Constants.feedbackMap);
                editor.commit();
            }
        }
    }

    public void removeAcraMap(){
        SharedPreferences pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE);
        if (pSharedPref != null){
            SharedPreferences.Editor editor = pSharedPref.edit();
            if(editor!=null) {
                editor.remove(Constants.acraMap);
                editor.commit();
            }
        }
    }
    
    public void saveAgentName(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.agentPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor != null){
                editor.remove(Constants.agentName).commit();
                editor.putString(Constants.agentName, name);
                editor.commit();
            }
        }
    }

    public String loadAgentName(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.agentPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.agentName, null);
        } else {
            return null;
        }
    }

    public void saveSpreadsheetKey(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.spreadsheetKeyPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor != null){
                editor.remove(Constants.spreadsheetKey).commit();
                editor.putString(Constants.spreadsheetKey, name);
                editor.commit();
            }
        }
    }

    public String loadSpreadsheetKey(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.spreadsheetKeyPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.spreadsheetKey, null);
        } else {
            return null;
        }
    }

    public void saveWorksheetName(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.worksheetNamePrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(editor != null){
                editor.remove(Constants.worksheetName).commit();
                editor.putString(Constants.worksheetName, name);
                editor.commit();
            }
        }
    }

    public String loadWorksheetName(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.worksheetNamePrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return sharedPreferences.getString(Constants.worksheetName, null);
        } else {
            return null;
        }
    }
}
