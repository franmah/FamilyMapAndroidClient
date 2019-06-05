package com.fmahieu.familymap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment mainMapFragment = fragmentManager.findFragmentById(R.id.google_map_container);

        if(mainMapFragment == null){
            mainMapFragment = new MainMapFragment();
            fragmentManager.beginTransaction().add(R.id.google_map_container, mainMapFragment).commit();
        }

        return view;
    }

}
