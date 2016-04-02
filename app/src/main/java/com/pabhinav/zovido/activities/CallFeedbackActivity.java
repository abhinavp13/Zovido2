package com.pabhinav.zovido.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.adpater.SpinnerAdapter;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.dao.SQLiteDb;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.util.Constants;
import com.pabhinav.zovido.util.Utils;
import com.pabhinav.zovido.util.ZLog;

import java.util.Arrays;
import java.util.List;

public class CallFeedbackActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String purpose = "Select Purpose for Call";
    private String product = "Select Product related in Call";
    private String sport = "Select Sport related in Call";
    private boolean customForm;
    private boolean modifyForm;
    private Long id;
    private int indexInSavedLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_feedback);

        /** Set up spinners for this activity **/
        setUpPurposeSpinnerAdapter();

        /** Fetching from bundle **/
        fetchDataFromBundle(savedInstanceState);

        /** Set the status bar color **/
        setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    /** Status bar color **/
    public void setStatusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    /** Back Clicked **/
    public void backClicked(View v){
        super.onBackPressed();
    }

    /** Fetches all data from bundle **/
    private void fetchDataFromBundle(Bundle savedInstanceState){

        /** Is this a custom form **/
        customForm = Utils.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.customForm, false);

        /** Is this a modify form request **/
        modifyForm = Utils.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.modifyForm, false);

        /** Database entry id **/
        id = Utils.getLongFromBundle(savedInstanceState, getIntent().getExtras(), Constants.id, -1L);

        /** Saved log index **/
        indexInSavedLog = Utils.getIntFromBundle(savedInstanceState, getIntent().getExtras(), Constants.indexInSavedLogs, -1);

        /** Agent Name  **/
        if(ZovidoApplication.getInstance() != null) {
            ((AppCompatEditText) findViewById(R.id.agent_edit_text))
                    .setText(ZovidoApplication.getInstance().getAgentName());
        }

        /** Phone Number **/
        ((AppCompatEditText) findViewById(R.id.phone_edit_text))
                .setText(Utils.getStringFromBundle(savedInstanceState,getIntent().getExtras(), Constants.phoneNumber, ""));

        /** Date **/
        ((AppCompatEditText) findViewById(R.id.date_edit_text))
                .setText(Utils.getStringFromBundle(savedInstanceState,getIntent().getExtras(), Constants.date, ""));

        /** Time **/
        ((AppCompatEditText) findViewById(R.id.time_edit_text))
                .setText(Utils.getStringFromBundle(savedInstanceState,getIntent().getExtras(), Constants.time, ""));

        /** Name of calling person **/
        ((AppCompatEditText) findViewById(R.id.name_edit_text))
                .setText(Utils.getStringFromBundle(savedInstanceState,getIntent().getExtras(), Constants.name, ""));

        /** Duration **/
        ((AppCompatEditText) findViewById(R.id.duration_edit_text))
                .setText(Utils.getStringFromBundle(savedInstanceState, getIntent().getExtras(), Constants.duration, ""));

        /** Purpose **/
        Spinner purposeSpinner = (Spinner) findViewById(R.id.purpose_spinner);
        purposeSpinner.setSelection(getIndex(
                purposeSpinner,
                Utils.getStringFromBundle(
                        savedInstanceState,
                        getIntent().getExtras(),
                        Constants.purpose,
                        "Select Purpose for Call")
                )
        );

        /** Product **/
        Spinner productSpinner = (Spinner) findViewById(R.id.product_spinner);
        productSpinner.setSelection(getIndex(
                productSpinner,
                Utils.getStringFromBundle(
                        savedInstanceState,
                        getIntent().getExtras(),
                        Constants.product,
                        "Select Product related in Call")
                )
        );

        /** Sport **/
        Spinner sportSpinner = (Spinner) findViewById(R.id.sport_spinner);
        sportSpinner.setSelection(getIndex(
                sportSpinner,
                Utils.getStringFromBundle(
                        savedInstanceState,
                        getIntent().getExtras(),
                        Constants.sport,
                        "Select Sport related in Call")
                )
        );

        /** Call Remarks **/
        ((AppCompatEditText) findViewById(R.id.call_remarks_edit_text))
                .setText(Utils.getStringFromBundle(savedInstanceState, getIntent().getExtras(), Constants.callRemarks, ""));

    }

    /** Get index in the spinner **/
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    /** Setup Spinners for this activity **/
    private void setUpPurposeSpinnerAdapter(){

        /** Purpose **/
        addSpinnerAdapter(R.id.purpose_spinner, ZovidoApplication.getInstance().getPurposeList());

        /** Product **/
        addSpinnerAdapter(R.id.product_spinner, ZovidoApplication.getInstance().getProductList());

        /** Sport **/
        addSpinnerAdapter(R.id.sport_spinner, ZovidoApplication.getInstance().getSportList());
    }

    /** Add Spinners for this activity **/
    private void addSpinnerAdapter(int purposeSpinnerId, List<String> items){
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(
                this,
                R.layout.spinner_item_dropdown,
                R.layout.spinner_item_non_dropdown
        );
        spinnerAdapter.addItems(items);

        Spinner spinner = (Spinner) findViewById(purposeSpinnerId);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(this);
    }

    /** Tick clicked **/
    public void tickClicked(View v){

        /** Invalidate Drop Down entries **/
        invalidateDropDownCategory();

        /** Request came from a custom form **/
        if(customForm){
            cameFromCustomForm();

            /** finish this activity **/
            finish();

            return;
        }

        /** Request came to modify form **/
        if(modifyForm){
            cameToModifyForm();

            /** finish this activity **/
            finish();

            return;
        }

        /** Request came from call logs **/
        cameFromCallLogs();

        /** finish this activity **/
        finish();
    }

    /** Request came from a custom form **/
    private void cameFromCustomForm(){

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Get te data pojo **/
        SavedLogsDataParcel savedLogsDataParcel = getDataPojo();

        /** Insert into database **/
        Long id = ZovidoApplication.getInstance().getSqliteDb().insertSavedLogData(savedLogsDataParcel);
        if(id == -1){
            return;
        }
        savedLogsDataParcel.setId(id);

        /** Add globally in saved logs **/
        ZovidoApplication.getInstance().addSavedLogsDataParcel(savedLogsDataParcel);

    }

    /** Request came to modify form **/
    private void cameToModifyForm(){

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Get te data pojo **/
        SavedLogsDataParcel savedLogsDataParcel = getDataPojo();
        savedLogsDataParcel.setId(id);

        /** Update saved logs in database **/
        int rowsAffected = ZovidoApplication.getInstance().getSqliteDb().updateSavedLogData(savedLogsDataParcel);
        if(rowsAffected == 0){
            return;
        }

        /** update globally in saved logs **/
        ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().set(indexInSavedLog, savedLogsDataParcel);
    }

    /** Request came from call logs **/
    private void cameFromCallLogs(){

        if(ZovidoApplication.getInstance() == null){
            return;
        }

        /** Get te data pojo **/
        SavedLogsDataParcel savedLogsDataParcel = getDataPojo();

        /** Insert into database **/
        Long id = ZovidoApplication.getInstance().getSqliteDb().insertSavedLogData(savedLogsDataParcel);
        if(id == -1){
            return;
        }
        savedLogsDataParcel.setId(id);

        /** Add globally in saved logs **/
        ZovidoApplication.getInstance().addSavedLogsDataParcel(savedLogsDataParcel);

        /** Update tick in call logs **/
        for(int i = 0; i<ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size(); i++){
            if(Utils.equalsCallAndSavedLog(ZovidoApplication.getInstance()
                    .getCallLogsDataParcelArrayListInstance().get(i),
                    savedLogsDataParcel
                    )){

                CallLogsDataParcel callLogsDataParcel = ZovidoApplication.getInstance()
                                                                .getCallLogsDataParcelArrayListInstance().get(i);
                callLogsDataParcel.setShowTick(true);

                ZovidoApplication.getInstance().updateCallLogsDataParcelIfExists(callLogsDataParcel);

                break;
            }
        }
    }

    /** Fill in pojo **/
    private SavedLogsDataParcel getDataPojo(){

        SavedLogsDataParcel pojo = new SavedLogsDataParcel();
        pojo.setName(((AppCompatEditText)findViewById(R.id.name_edit_text)).getText().toString());
        pojo.setAgentName(((AppCompatEditText) findViewById(R.id.agent_edit_text)).getText().toString());
        pojo.setDate(((AppCompatEditText) findViewById(R.id.date_edit_text)).getText().toString());
        pojo.setTime(((AppCompatEditText) findViewById(R.id.time_edit_text)).getText().toString());
        pojo.setDuration(((AppCompatEditText) findViewById(R.id.duration_edit_text)).getText().toString());
        pojo.setPhoneNumber(((AppCompatEditText) findViewById(R.id.phone_edit_text)).getText().toString());
        pojo.setPurpose(purpose);
        pojo.setProduct(product);
        pojo.setSport(sport);
        pojo.setCallRemarks(((AppCompatEditText) findViewById(R.id.call_remarks_edit_text)).getText().toString());

        return pojo;
    }
    
    /** Invalidates Drop Down Categories **/
    private void invalidateDropDownCategory(){
        
        if(purpose.startsWith("Select")){
            purpose = "";
        }
        if(product.startsWith("Select")){
            product = "";
        }
        if(sport.startsWith("Select")){
            sport = "";
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent != null && parent.getItemAtPosition(position) != null) {
            updateCategory(parent.getItemAtPosition(position).toString());
        } else {

            if(ZovidoApplication.getInstance() != null) {
                ZovidoApplication.getInstance().trackEvent(
                        "Error",
                        "Menu Item Selection",
                        "AdapterView is null or getItemAtPosition : " + position + " is null !!!"
                );
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    /** Updates the value for each category **/
    private void updateCategory(String value){
        List<String> purposeList = ZovidoApplication.getInstance().getPurposeList();
        List<String> productList = ZovidoApplication.getInstance().getProductList();
        List<String> sportList = ZovidoApplication.getInstance().getSportList();

        if(purposeList.indexOf(value) != -1){
            purpose = value;
        }
        if(productList.indexOf(value) != -1){
            product = value;
        }
        if(sportList.indexOf(value) != -1){
            sport = value;
        }
    }
}
