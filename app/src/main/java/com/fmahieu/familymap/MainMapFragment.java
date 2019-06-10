package com.fmahieu.familymap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.models.Event;
import com.client.models.Model;
import com.client.models.Person;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainMapFragment extends Fragment {
    private final String TAG = "MainMapFragment";

    private ImageView mIcon;
    private TextView mPersonName;
    private TextView mEventInfo;

    private GoogleMap map;
    private Model model = Model.getInstance();

    private List<Polyline> lines = new ArrayList<>();
    private Map<Marker, Event> eventMarkers = new HashMap<>();
    private Map<String, String> eventTypes = model.getEventTypes();
    private Map<String, Event>  allEvents = model.getEvents();

    private final float ZOOM_LEVEL = 5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // String currentEventId = (String) getArguments().getCharSequence(ARG_EVENT_ID);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Log.i(TAG, "Loading/drawing map ...");
        initWidgets(view);
        getMap();

        return view;
    }

    private void getMap(){
        // Get map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map_fragment);

        Log.i(TAG, "getting MapAsync...");
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                Log.i(TAG, "map found, drawing events...");
                initMap();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_activity_map_menu, menu);

        menu.findItem(R.id.search_item).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                .colorRes(R.color.menuItem)
                .actionBarSize());

        menu.findItem(R.id.filter_item).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_filter)
                .colorRes(R.color.menuItem)
                .actionBarSize());

        menu.findItem(R.id.settings_item).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                .colorRes(R.color.menuItem)
                .actionBarSize());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.search_item:
                // call the search activity
                return true;
            case R.id.filter_item:
                // call filter activity
                Log.i(TAG, "onOptionsItemSelected(): Starting FilterActivity");
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                startActivity(intent);

                // need to implement on resume to update the map.
                return true;
            case R.id.settings_item:
                // call settings activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initWidgets(View view){
        // iconify
        Iconify.with(new FontAwesomeModule());

        //Wire up widgets
        mIcon = (ImageView) view.findViewById(R.id.icon_ImageView);
        mPersonName = (TextView) view.findViewById(R.id.person_name_view);
        mEventInfo = (TextView) view.findViewById(R.id.event_info_view);

        Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android)
                .colorRes(R.color.androidIconColor).sizeDp(40);
        mIcon.setImageDrawable(genderIcon);

        mPersonName.setText(R.string.basic_upper_text);
        mEventInfo.setText(R.string.basic_lower_text);

    }

    private void initMap(){
        map.clear();
        setMarkers();
        setMarkerListener();

    }

    private void setMarkers() {
        assert map != null;
        Log.i(TAG, "placing markers");

        map.clear();
        eventMarkers.clear();

        Set<String> mapEvents = model.getCurrentEvents();
        if(mapEvents == null) {
            return; // No events to show because of Filters.
        }


        for(String eventId : mapEvents){
            Event event = allEvents.get(eventId);

            if(eventTypes.get(event.getEventType()).equals("t")) {
                LatLng position = new LatLng(event.getLatitude(), event.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(position);

                Marker marker = map.addMarker(markerOptions);
                marker.setTag(event.getEventId());

                eventMarkers.put(marker, event);
            }

        }

        Log.i(TAG, "Markers have been placed");
    }

    private void setMarkerListener(){
        Log.i(TAG, "setMarkerListener(): Creating listener for map markers");
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Event eventClicked = eventMarkers.get(marker);
                LatLng position = new LatLng(eventClicked.getLatitude(), eventClicked.getLongitude());

                centerAndZoomMap(position);
                drawLines(eventClicked);

                // Update icon + text
                Person person = model.getPeople().get(eventClicked.getPersonId());
                mPersonName.setText(person.printName());
                mEventInfo.setText(eventClicked.printEventInfo());

                if(person.getGender().equals("f")){
                    Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                            .colorRes(R.color.femaleIconColor).sizeDp(40);
                    mIcon.setImageDrawable(genderIcon);
                }
                else{
                    Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                            .colorRes(R.color.maleIconColor).sizeDp(40);
                    mIcon.setImageDrawable(genderIcon);
                }

                return false;
            }
        });
    }

    private void centerAndZoomMap(LatLng position){
        Log.i(TAG,"centerAndZoomMap(): Centering and zooming on map...");
        // Move camera
        CameraUpdate update = CameraUpdateFactory.newLatLng(position);
        map.moveCamera(update);

        // Zoom on map
        update = CameraUpdateFactory.zoomTo(ZOOM_LEVEL);
        map.animateCamera(update);

        Log.i(TAG, "centerAndZoomMap(): camera has been updated");
    }

    private void drawLines(Event centralEvent){

        if(eventMarkers == null){
            return;
        }

        Log.i(TAG, "drawLines(): removing previous lines if any");
        // Remove previous lines
        for(Polyline line : lines){
            line.remove();
        }
        lines.clear();

        Log.i(TAG, "drawing new lines for event: " + centralEvent.getEventId());
        Set<String> personEvents = model.getPersonEvents(centralEvent.getPersonId());

        for(String eventId : personEvents){
            Event event = allEvents.get(eventId);

            if(model.getEventTypes().get(event.getEventType()).equals("t")) {

                Polyline line = map.addPolyline(new PolylineOptions().add(
                        new LatLng(centralEvent.getLatitude(), centralEvent.getLongitude()),
                        new LatLng(event.getLatitude(), event.getLongitude()))
                        .width(5)
                        .color(Color.RED));
                lines.add(line);
            }
        }

        Log.i(TAG, "drawLines(): lines have been drawn");
    }

    @Override
    public void onResume() {
        super.onResume();
        getMap();
    }
}



/* FUTURE CODE FOR CALLING THE FRAGMENT FROM AN ACTIVITY

       private static final String ATG_EVENT_ID = "event_id_call";

       public static MainMapFragment newInstance(String eventId){
            Bundle args = new Bundle();
            args.putCharSequence(ARG_EVENT_ID, eventId);
            MainMapFragment fragment = new MainMapFragment();
            // Should it be new MainMapFragment(String eventId), and then have a constructor ?
            // Doesn't need it if the onCreateInitialize the string
            // Test it without using the Bundle/argument, just a constructor that set the event_id
            return fragment;
       }

       in the activity calling it:
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        // The layout/container which will hold the fragment

        if(fragment == null)
            fragment = MainMapFragment.newInstance(the event);
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

     */