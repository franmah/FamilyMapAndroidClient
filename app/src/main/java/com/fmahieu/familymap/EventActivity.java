package com.fmahieu.familymap;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.client.models.Model;

public class EventActivity extends AppCompatActivity {

    private final String TAG = "EventActivity";
    private static final String EVENT_ID = "com.fmahieu.familymap.evenId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        Log.i(TAG, "retrieving event id");
        String eventId = getIntent().getStringExtra(EVENT_ID);
        if(eventId == null){
            Log.i(TAG, "event id is null, finishing...");
            finish();
        }

        getMapFragment(eventId);

    }

    public static Intent newIntent(Context context, String eventId){
        Log.i("EventActivity", "creating new Intent..");
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra(EVENT_ID, eventId);
        return intent;
    }

    private void getMapFragment(String eventId) {
        Log.i(TAG, "getting map fragment");

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.event_activity_map_fragment_container);

        if (fragment == null) {
            fragment = MainMapFragment.newInstance(eventId);
            fragmentManager.beginTransaction().add(R.id.event_activity_map_fragment_container, fragment).commit();
        } else {
            fragment = MainMapFragment.newInstance(eventId);
            fragmentManager.beginTransaction().replace(R.id.event_activity_map_fragment_container, fragment).commit();
        }
    }
}
