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
import com.client.response.ConnectionResponse;

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

    private boolean hostNameFilled = false;
    private boolean portNumberFilled = false;
    private boolean userNameFilled = false;
    private boolean passwordFilled = false;
    private boolean firstNameFilled = false;
    private boolean lastNameFilled =false;
    private boolean emailFilled = false;

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
                    // Register the user
                }
                else{
                    Toast.makeText(getContext(), R.string.incorrect_info_register_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private class LoginUser extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params){
            Log.i(TAG, "in LoginUser, starting login request...");
            Toast.makeText(getContext(), "TEST8080", Toast.LENGTH_SHORT).show();
            ServerProxy proxy = new ServerProxy(mHostNameText.getText().toString(), mPortNumberText.getText().toString());
            ConnectionResponse response = proxy.login(new LoginRequest(mUserNameText.getText().toString(), mPasswordText.getText().toString()));

            if(response.getErrorMessage() == null){
                Model model = Model.getInstance();
                model.setUserToken(response.getToken());
                model.setUserPersonId(response.getPersonID());
            }
            else{
                Log.e(TAG,response.getErrorMessage());
                Toast.makeText(getContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }

            return null;
        }
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
