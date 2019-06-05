package com.fmahieu.familymap;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.client.httpClient.ServerProxy;
import com.client.models.Model;
import com.client.request.LoginRequest;
import com.client.request.RegisterRequest;
import com.client.response.ConnectionResponse;
import com.client.service.DataRetriever;

public class loginFragment extends Fragment {

    private final String TAG = "LoginFragment";

    private EditText mHostNameText;
    private EditText mPortNumberText;
    private EditText mUserNameText;
    private EditText mPasswordText;
    private EditText mFirstNameText;
    private EditText mLastNameText;
    private EditText mEmailText;
    private RadioButton mMaleButton;
    private RadioButton mFemaleButton;
    private Button mSignInButton;
    private Button mRegisterButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.login_fragment, container, false);

        mHostNameText = (EditText)view.findViewById(R.id.host_editText);
        mPortNumberText = (EditText) view.findViewById(R.id.port_editText);
        mUserNameText = (EditText) view.findViewById(R.id.username_editText);
        mPasswordText = (EditText) view.findViewById(R.id.password_editText);
        mFirstNameText = (EditText) view.findViewById(R.id.first_name_editText);
        mLastNameText = (EditText) view.findViewById(R.id.last_name_editText);
        mEmailText = (EditText) view.findViewById(R.id.email_editText);
        mMaleButton = (RadioButton) view.findViewById(R.id.male_radioButton);
        mFemaleButton = (RadioButton) view.findViewById(R.id.femail_radioButton);
        mSignInButton = (Button) view.findViewById(R.id.sign_button);
        mRegisterButton = (Button) view.findViewById(R.id.register_button);

        mSignInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isLoginTextFilled()){
                    Log.i(TAG, "Login button pressed, moving to http proxy...");
                    new LoginUser().execute();

                }
                else{
                    Toast.makeText(getContext(), R.string.incorrect_info_login_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isRegisterTextFilled()){
                    Log.i(TAG, "Register button pressed, moving to http proxy...");
                    new RegisterUser().execute();
                }
                else{
                    Toast.makeText(getContext(), R.string.incorrect_info_register_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void test(){
        new RetrieveData().execute();
    }

    private class LoginUser extends AsyncTask<Void, Void, ConnectionResponse>{
        @Override
        protected ConnectionResponse doInBackground(Void... params){
            Log.i(TAG, "in LoginUser, starting login request...");
            ServerProxy proxy = new ServerProxy(mHostNameText.getText().toString(), mPortNumberText.getText().toString());
            ConnectionResponse response = proxy.login(new LoginRequest(mUserNameText.getText().toString(), mPasswordText.getText().toString()));

            return response;
        }

        @Override
        protected void onPostExecute(ConnectionResponse response){

            if(response.getErrorMessage() == null){
                Model model = Model.getInstance();
                model.setUserToken(response.getToken());
                model.setUserPersonId(response.getPersonID());

                Log.i(TAG, "RegisterAsync.onPostExecute : Starting retrieveData aSyncTask");
                //new RetrieveData().execute();
                test();

            }
            else{
                Log.e(TAG,response.getErrorMessage());
                makeToast(response.getErrorMessage());
            }
        }
    }

    private class RegisterUser extends AsyncTask<Void, Void, ConnectionResponse>{
        @Override
        protected ConnectionResponse doInBackground(Void... params){
            Log.i(TAG, "in RegisterUser, starting register request...");
            ServerProxy proxy = new ServerProxy(mHostNameText.getText().toString(), mPortNumberText.getText().toString());

            String gender = "m";
            if(mFemaleButton.isChecked()){
                gender = "f";
            }

            ConnectionResponse response = proxy.register(new RegisterRequest(mUserNameText.getText().toString(),
                    mPasswordText.getText().toString(), mEmailText.getText().toString(), mFirstNameText.getText().toString(),
                    mLastNameText.getText().toString(), gender));

            return response;
        }

        @Override
        protected void onPostExecute(ConnectionResponse response){

            if(response.getErrorMessage() == null){
                Model model = Model.getInstance();
                model.setUserToken(response.getToken());
                model.setUserPersonId(response.getPersonID());

                Log.i(TAG, "RegisterAsync.onPostExecute : Starting retrieveData aSyncTask");
                new RetrieveData().execute();

            }
            else{
                Log.e(TAG,response.getErrorMessage());
                makeToast(response.getErrorMessage());
            }
        }
    }

    private class RetrieveData extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params){
            Log.i(TAG, "in RetrieveData, starting data request...");
            DataRetriever dataRetriever = new DataRetriever();
            String response = dataRetriever.pullData(mHostNameText.getText().toString(), mPortNumberText.getText().toString());

            return response;
        }

        @Override
        protected void onPostExecute(String response){
            if(response != null){
                Log.i(TAG, "in RetrieveData, response from DataRetriever came back not null, an error occurred.");
                makeToast(response);
            }
            else {
                Log.i(TAG, "in RetrieveData, response from DataRetriever came back null, data has been successfully loaded");

                // TEST:
                Model model = Model.getInstance();
                makeToast("Connected: " + model.getUserPerson().getFirstName() + " " + model.getUserPerson().getLastName());
                Log.i("TESTING", "THIS IS A TEST: " + model.getPeople().toString());
            }
        }
    }

    private void makeToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean isLoginTextFilled(){
        return (!mHostNameText.getText().toString().equals("") &&
                !mPortNumberText.getText().toString().equals("") &&
                !mUserNameText.getText().toString().equals("") &&
                !mPasswordText.getText().toString().equals(""));
    }

    private boolean isRegisterTextFilled(){
        return (isLoginTextFilled() &&
                !mFirstNameText.getText().toString().equals("") &&
                !mLastNameText.getText().toString().equals("") &&
                !mEmailText.getText().toString().equals(""));
    }
}