package com.fmahieu.familymap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainMapFragment extends Fragment {
    private final String TAG = "MainMapFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Log.i(TAG, "creating view...");


        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment mainMapFragment = fragmentManager.findFragmentById(R.id.google_map_container);

        if(mainMapFragment == null){
            mainMapFragment = new GoogleMapFragment();
            fragmentManager.beginTransaction().add(R.id.google_map_container, mainMapFragment).commit();
            Log.i(TAG, "getting google map fragment");
        }



        return view;
    }

}
