package com.fmahieu.familymap;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
        if(fragment == null){
            fragment = new loginFragment();
            fragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, fragment).commit();
        }
        else{
            fragment = new loginFragment();
            fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, fragment).commit();
        }
    }

    public void switchToMapFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_fragment_container);
        if(fragment == null){
            fragment = new MainMapFragment();
            fragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, fragment).commit();
        }
        else{
            fragment = new MainMapFragment();
            fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, fragment).commit();
        }
    }

}
