package com.fmahieu.familymap.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fmahieu.familymap.R;
import com.fmahieu.familymap.client.models.Model;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity{
    private final String TAG = "MainActivity";
    private final int ERROR_DIALOG_REQUEST = 9001;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private boolean isMapFragmentOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isGoogleServicesOK()){
            Log.e(TAG, "Google services unavailable");
        }

        getFragment();
    }

    public void getFragment(){
        Log.i(TAG, "getting fragment");
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
        if(fragment == null) {
            if (!Model.getInstance().isUserLoggedIn()) {
                fragment = new LoginFragment();
                isMapFragmentOn = false;
            } else {
                fragment = MainMapFragment.newInstance(null);
                isMapFragmentOn = true;
            }

            fragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, fragment).commit();
        }
        else{
            if (!Model.getInstance().isUserLoggedIn()) {
                fragment = new LoginFragment();
                isMapFragmentOn = false;
            } else if(!isMapFragmentOn) {
                fragment = MainMapFragment.newInstance(null);
                isMapFragmentOn = true;
            }

            fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, fragment).commit();
        }
    }

    public boolean isGoogleServicesOK(){
        Log.i(TAG, "isGoogleServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable((MainActivity.this));
        if(available == ConnectionResult.SUCCESS){
            Log.i(TAG, "isGoogleServices: google services work!");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.w(TAG, "isGoogleServices: there is an error but can be fixed by the user");
            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST).show();
        }
        else{
            Log.e(TAG, "isGoogleServiceOK: unsolvable error");
            Toast.makeText(this, "Google services do not work on this phone", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFragment();
    }
}
