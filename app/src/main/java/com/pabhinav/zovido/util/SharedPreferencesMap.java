package com.pabhinav.zovido.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.pabhinav.zovido.pojo.SavedLogsDataParcel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /** Saves the categoryList. categories include : product, purpose, sport **/
    public void saveCategoryList(String categoryPrefs, String categoryConstant, ArrayList<String> categoryList){
        SharedPreferences sharedPreferences = context.getSharedPreferences(categoryPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            Set<String> set = new HashSet<>();
            set.addAll(categoryList);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.remove(categoryConstant).commit();
                editor.putStringSet(categoryConstant, set);
                editor.commit();
            }
        }
    }

    /** Load arraylist, given category prefs and constant **/
    public ArrayList<String> loadCategoryList(String categoryPrefs, String categoryConstant) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(categoryPrefs, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            Set<String> set = sharedPreferences.getStringSet(categoryConstant, null);
            if(set == null){
                return null;
            } else {
                return new ArrayList<>(set);
            }
        } else {
            return null;
        }
    }

    public void saveProductList(ArrayList<String> productList){
        saveCategoryList(Constants.productCategoryPrefs, Constants.productKey, productList);
    }
    public void savePurposeList(ArrayList<String> purposeList){
        saveCategoryList(Constants.purposeCategoryPrefs, Constants.purposeKey, purposeList);
    }
    public void saveSportList(ArrayList<String> sportList){
        saveCategoryList(Constants.sportCategoryPrefs, Constants.sportKey, sportList);
    }

    public ArrayList<String> loadProductList(){
        ArrayList<String> productList = loadCategoryList(Constants.productCategoryPrefs, Constants.productKey);
        if(productList == null){
            return new ArrayList<>();
        }
        int index = productList.indexOf("Select Product related in Call");
        if(index != -1){
            String value = productList.get(index);
            productList.remove(value);
            productList.add(0, value);
        }
        return productList;
    }
    public ArrayList<String> loadPurposeList(){
        ArrayList<String> purposeList = loadCategoryList(Constants.purposeCategoryPrefs, Constants.purposeKey);
        if(purposeList == null){
            return new ArrayList<>();
        }
        int index = purposeList.indexOf("Select Purpose for Call");
        if(index != -1){
            String value = purposeList.get(index);
            purposeList.remove(value);
            purposeList.add(0, value);
        }
        return purposeList;
    }
    public ArrayList<String> loadSportList(){
        ArrayList<String> sportList = loadCategoryList(Constants.sportCategoryPrefs, Constants.sportKey);
        if(sportList == null){
            return new ArrayList<>();
        }
        int index = sportList.indexOf("Select Sport related in Call");
        if(index != -1){
            String value = sportList.get(index);
            sportList.remove(value);
            sportList.add(0, value);
        }
        return sportList;
    }
}
