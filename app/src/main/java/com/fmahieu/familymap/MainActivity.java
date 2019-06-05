package com.fmahieu.familymap;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String USER_IS_LOGGED_IN =  "com.fmahieu.familymap.user_is_logged_in";
    private boolean isUserLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isUserLoggedIn = getIntent().getBooleanExtra(USER_IS_LOGGED_IN, false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);

        if(isUserLoggedIn){
            // get Map fragment:
            if(fragment == null){
                fragment = new MainMapFragment();
                fragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, fragment).commit();
            }
            else{
                fragment = new MainMapFragment();
                fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, fragment).commit();
            }
        }
        else{
            if(fragment == null){
                fragment = new loginFragment();
                fragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, fragment).commit();
            }
            else{
                fragment = new loginFragment();
                fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, fragment).commit();
            }
        }
        /*
        Fragment connectionFragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);

        if(connectionFragment == null){
            connectionFragment = new loginFragment();
            fragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, connectionFragment).commit();
        }
        */
    }

    public void switchToMapFragment(){

    }

    public static Intent newIntent(Context context, boolean isLoggedIn) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_IS_LOGGED_IN, isLoggedIn);
        return intent;
    }
}
