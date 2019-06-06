package com.fmahieu.familymap;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import com.client.models.Event;
import com.client.models.Model;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class GoogleMapFragment extends SupportMapFragment {
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private final String TAG = "GoogleMapFragment";

    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private GoogleMap map;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Creating map");
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                updateMap();
            }
        });

        if(map == null){
            Log.e(TAG, "MAP IS NULL");
        }


    }

    /** Redraw the map
     *
     */
    public void updateMap(){
        Log.i(TAG, "updating map...");
        Model model = Model.getInstance();

        map.clear();

        Map<String, Event> mapEvents = model.getEvents();
        for(Map.Entry<String, Event> pair : mapEvents.entrySet()){
            LatLng position = new LatLng(pair.getValue().getLatitude(), pair.getValue().getLongitude());
            MarkerOptions marker = new MarkerOptions().position(position);
            map.addMarker(marker);
        }




    }



}
