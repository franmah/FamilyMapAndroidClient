package com.fmahieu.familymap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.client.httpClient.ServerProxy;
import com.client.models.Model;
import com.client.service.DataRetriever;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final String TAG = "SettingActivity";

    private Spinner mLifeStorySpinner;
    private Spinner mFamilyTreeSpinner;
    private Spinner mSpouseLinesSpinner;
    private Spinner mMapTypeSpinner;

    private Switch mLifeStorySwitch;
    private Switch mFamilyTreeSwitch;
    private Switch mSpouseLinesSwitch;

    private LinearLayout mResynDataLayout;
    private LinearLayout mLogoutLayout;


    private Model model = Model.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "setting activity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        initSpinnerWidgets();
        initSwitchWidgets();
        initTextLayoutWidgets();

    }

    /** SPINNNERS **/
    private void initSpinnerWidgets(){
        Log.i(TAG, "Setting up spinner widgets");

        mLifeStorySpinner = (Spinner) findViewById(R.id.life_story_spinner);
        mFamilyTreeSpinner = (Spinner) findViewById(R.id.family_tree_spinner);
        mSpouseLinesSpinner = (Spinner) findViewById(R.id.spouse_lines_spinner);
        mMapTypeSpinner = (Spinner) findViewById(R.id.map_type_spinner);


        mLifeStorySpinner.setOnItemSelectedListener(this);
        mFamilyTreeSpinner.setOnItemSelectedListener(this);
        mSpouseLinesSpinner.setOnItemSelectedListener(this);
        mMapTypeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> lineAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_color_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> mapAdapter = ArrayAdapter.createFromResource(this, R.array.map_type_array, android.R.layout.simple_spinner_dropdown_item);

        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mLifeStorySpinner.setAdapter(lineAdapter);
        mFamilyTreeSpinner.setAdapter(lineAdapter);
        mSpouseLinesSpinner.setAdapter(lineAdapter);
        mMapTypeSpinner.setAdapter(mapAdapter);

        mLifeStorySpinner.setSelection(model.getLifeStoryColorPos());
        mFamilyTreeSpinner.setSelection(model.getFamilyTreeColorPos());
        mSpouseLinesSpinner.setSelection(model.getSpouseLineColorPos());
        mMapTypeSpinner.setSelection(model.getMapTypePos());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        Log.i(TAG, "Spinner changed");
        switch (parent.getId()){
            case R.id.life_story_spinner:
                model.setLifeStoryColor(pos);
                break;
            case R.id.family_tree_spinner:
                model.setFamilyTreeColor(pos);
                break;
            case R.id.spouse_lines_spinner:
                model.setSpouseLineColor(pos);
                break;
            case R.id.map_type_spinner:
                model.setMapTypePos(pos);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /** SWITCHES **/
    private void initSwitchWidgets(){
        Log.i(TAG, "Setting up switch widgets");

        mLifeStorySwitch = (Switch) findViewById(R.id.life_story_switch);
        mFamilyTreeSwitch = (Switch) findViewById(R.id.family_tree_switch);
        mSpouseLinesSwitch = (Switch) findViewById(R.id.spouse_lines_switch);

        mLifeStorySwitch.setChecked(model.isLifeStoryLineOn());
        mFamilyTreeSwitch.setChecked(model.isFamilyTreeLineOn());
        mSpouseLinesSwitch.setChecked(model.isSpouseLineOn());

        mLifeStorySwitch.setOnCheckedChangeListener(this);
        mFamilyTreeSwitch.setOnCheckedChangeListener(this);
        mSpouseLinesSwitch.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.life_story_switch:
                model.setLifeStoryLineOn(isChecked);
                break;
            case R.id.family_tree_switch:
                model.setFamilyTreeLineOn(isChecked);
                break;
            case R.id.spouse_lines_switch:
                model.setSpouseLineOn(isChecked);
                break;
            default:
                break;
        }
    }

    /** TEXTVIEWS **/
    private void initTextLayoutWidgets(){
        mResynDataLayout = (LinearLayout) findViewById(R.id.resync_data_layout);
        mLogoutLayout = (LinearLayout) findViewById(R.id.logout_layout);

        mResynDataLayout.setOnClickListener(this);
        mLogoutLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.resync_data_layout:
                // create async task, which will pretty much retrieve the people.
                new ReSyncData().execute();
                break;
            case R.id.logout_layout:
                model.setUserLoggedIn(false);
                model.resetModelToDefault();
                finish();
                break;
            default:
                break;
        }

    }


    private class ReSyncData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params){
            Log.i(TAG, "reSyncData(): starting data request...");

            DataRetriever dataRetriever = new DataRetriever();
            String response = dataRetriever.pullData(model.getHostName(), model.getPortNumber());

            return response;
        }

        @Override
        protected void onPostExecute(String response){
            if(response != null){
                Log.i(TAG, "reSyncData, response from DataRetriever came back not null, an error occurred.");
                makeToast(response);
            }
            else {
                Log.i(TAG, "in RetrieveData, response from DataRetriever came back null, data has been successfully loaded");
                finish();
            }
        }
    }

    private void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
