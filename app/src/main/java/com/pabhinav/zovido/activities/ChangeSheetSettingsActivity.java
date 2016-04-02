package com.pabhinav.zovido.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.util.Utils;

public class ChangeSheetSettingsActivity extends AppCompatActivity {

    SpreadsheetKeyHandler spreadsheetKeyHandler;
    WorksheetHandler worksheetHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sheet_settings);

        /** Set the status bar color **/
        setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        spreadsheetKeyHandler = new SpreadsheetKeyHandler(this);
        worksheetHandler = new WorksheetHandler(this);

        ((TextView) findViewById(R.id.spreadsheet_dummy_text)).setText(ZovidoApplication.getInstance().getSpreadsheetKey());
        ((TextView) findViewById(R.id.worksheet_dummy_text)).setText(ZovidoApplication.getInstance().getWorksheetName());
    }

    /**
     * Sets the status bar color, if possible.
     *
     * @param color to be rendered on status bar.
     */
    public void setStatusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    /**
     * Back clicked by user.
     *
     * @param v view for back clicking
     */
    public void backClicked(View v){
        super.onBackPressed();
    }

    /**
     * Spread sheet key changes handler
     */
    class SpreadsheetKeyHandler implements TextWatcher{

        boolean isSettingsIcon;
        EditText spreadsheetEditText;
        ChangeSheetSettingsActivity changeSheetSettingsActivity;
        View underline;
        ImageView icon;
        TextView spreadSheetDummy;

        public SpreadsheetKeyHandler(ChangeSheetSettingsActivity changeSheetSettingsActivity){
            isSettingsIcon = true;
            this.changeSheetSettingsActivity = changeSheetSettingsActivity;
            spreadsheetEditText = (EditText) changeSheetSettingsActivity.findViewById(R.id.spreadsheet_key_edit_text);
            underline = changeSheetSettingsActivity.findViewById(R.id.underline_spreadsheet_key);
            icon = (ImageView) changeSheetSettingsActivity.findViewById(R.id.spreadsheet_edit_button);
            spreadSheetDummy = (TextView) changeSheetSettingsActivity.findViewById(R.id.spreadsheet_dummy_text);

            icon.setImageDrawable(changeSheetSettingsActivity.getResources().getDrawable(R.drawable.ic_settings_black_24dp));
            disableEditText(spreadsheetEditText);
            underline.setVisibility(View.INVISIBLE);

            spreadsheetEditText.addTextChangedListener(this);
        }

        public void handleClick(View v){

            if(icon == null || underline == null){
                return;
            }

            if(isSettingsIcon){
                icon.setImageDrawable(changeSheetSettingsActivity.getResources().getDrawable(R.drawable.ic_done_blue_24dp));
                underline.setVisibility(View.VISIBLE);
                enableEditText(spreadsheetEditText);
            } else {

                if(spreadsheetEditText.getText() == null || spreadsheetEditText.getText().toString().length() == 0){
                    return;
                }

                icon.setImageDrawable(changeSheetSettingsActivity.getResources().getDrawable(R.drawable.ic_settings_black_24dp));
                underline.setVisibility(View.INVISIBLE);
                disableEditText(spreadsheetEditText);

                ZovidoApplication.getInstance().setSpreadsheetKey(spreadsheetEditText.getText().toString());
            }
            isSettingsIcon = !isSettingsIcon;
        }

        private void disableEditText(EditText editText) {
            Utils.hideKeyboard(changeSheetSettingsActivity);
            editText.setVisibility(View.INVISIBLE);
            spreadSheetDummy.setText(editText.getText().toString());
            spreadSheetDummy.setVisibility(View.VISIBLE);
        }

        private void enableEditText(EditText editText){
            editText.setText(spreadSheetDummy.getText());
            editText.setVisibility(View.VISIBLE);
            spreadSheetDummy.setVisibility(View.INVISIBLE);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s == null || s.length() == 0) {
                underline.setBackgroundColor(changeSheetSettingsActivity.getResources().getColor(android.R.color.holo_red_light));
            } else {
                underline.setBackgroundColor(changeSheetSettingsActivity.getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    /**
     * Spread sheet key changes handler
     */
    class WorksheetHandler implements TextWatcher{

        boolean isSettingsIcon;
        EditText worksheetEditText;
        ChangeSheetSettingsActivity changeSheetSettingsActivity;
        View underline;
        ImageView icon;
        TextView workSheetDummy;

        public WorksheetHandler(ChangeSheetSettingsActivity changeSheetSettingsActivity){
            isSettingsIcon = true;
            this.changeSheetSettingsActivity = changeSheetSettingsActivity;
            worksheetEditText = (EditText) changeSheetSettingsActivity.findViewById(R.id.worksheet_edit_text);
            underline = changeSheetSettingsActivity.findViewById(R.id.underline_worksheet_name);
            icon = (ImageView) changeSheetSettingsActivity.findViewById(R.id.worksheet_edit_button);
            workSheetDummy = (TextView) changeSheetSettingsActivity.findViewById(R.id.worksheet_dummy_text);

            icon.setImageDrawable(changeSheetSettingsActivity.getResources().getDrawable(R.drawable.ic_settings_black_24dp));
            disableEditText(worksheetEditText);
            underline.setVisibility(View.INVISIBLE);

            worksheetEditText.addTextChangedListener(this);
        }

        public void handleClick(View v){

            if(icon == null || underline == null){
                return;
            }

            if(isSettingsIcon){
                icon.setImageDrawable(changeSheetSettingsActivity.getResources().getDrawable(R.drawable.ic_done_blue_24dp));
                underline.setVisibility(View.VISIBLE);
                enableEditText(worksheetEditText);
            } else {

                if(worksheetEditText.getText() == null || worksheetEditText.getText().toString().length() == 0){
                    return;
                }

                icon.setImageDrawable(changeSheetSettingsActivity.getResources().getDrawable(R.drawable.ic_settings_black_24dp));
                underline.setVisibility(View.INVISIBLE);
                disableEditText(worksheetEditText);

                ZovidoApplication.getInstance().setWorksheetName(worksheetEditText.getText().toString());
            }
            isSettingsIcon = !isSettingsIcon;
        }

        private void disableEditText(EditText editText) {
            Utils.hideKeyboard(changeSheetSettingsActivity);
            editText.setVisibility(View.INVISIBLE);
            workSheetDummy.setText(editText.getText().toString());
            workSheetDummy.setVisibility(View.VISIBLE);
        }

        private void enableEditText(EditText editText){
            editText.setText(workSheetDummy.getText());
            editText.setVisibility(View.VISIBLE);
            workSheetDummy.setVisibility(View.INVISIBLE);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s == null || s.length() == 0) {
                underline.setBackgroundColor(changeSheetSettingsActivity.getResources().getColor(android.R.color.holo_red_light));
            } else {
                underline.setBackgroundColor(changeSheetSettingsActivity.getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    

    public void spreadsheetEditOrDoneClicked(View v){
        spreadsheetKeyHandler.handleClick(v);
    }

    public void worksheetEditOrDoneClicked(View v){
        worksheetHandler.handleClick(v);
    }
}
