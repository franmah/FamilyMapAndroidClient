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
import java.util.SortedSet;

public class MainMapFragment extends Fragment {
    private final String TAG = "MainMapFragment";

    private ImageView mIcon;
    private TextView mPersonName;
    private TextView mEventInfo;

    private GoogleMap map;

    private Model model = Model.getInstance();

    private List<Polyline> lines = new ArrayList<>();
    private Map<Marker, Event> eventMarkers = new HashMap<>(); // Events to show according to filters

    private Map<String, String> eventTypes = model.getEventTypes();
    private Map<String, Event>  allEvents = model.getEvents();

    private final int BASE_LINE_WIDTH = 10;

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
                map.setMapType(model.getMapTypeId());

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
        Intent intent;
        switch(item.getItemId()){
            case R.id.search_item:
                // call the search activity
                return true;
            case R.id.filter_item:
                // call filter activity
                Log.i(TAG, "onOptionsItemSelected(): Starting FilterActivity");
                intent = new Intent(getActivity(), FilterActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings_item:
                Log.i(TAG, "onOptionsItemSelected(): Starting SettingActivity");
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
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

    private void drawLines(Event clickedEvent){

        if(eventMarkers == null || eventMarkers.size() == 0){
            return;
        }

        Log.i(TAG, "drawLines(): removing previous lines if any");
        // Remove previous lines
        for(Polyline line : lines){
            line.remove();
        }
        lines.clear();

        drawLifeStoryLines(clickedEvent);
        drawSpouseLines(clickedEvent);
        drawFamilyLines(clickedEvent);

        Log.i(TAG, "drawLines(): lines have been drawn");
    }

    private void drawLifeStoryLines(Event clickedEvent){
        Log.i(TAG, "draLifeStoryLines(): drawing new lines for event: " + clickedEvent.getEventId());

        if(!model.isLifeStoryLineOn()){
            Log.i("TESTING", "LINE NOT SHOWING");
            return;
        }

        List<String> personEvents = model.getPersonEvents(clickedEvent.getPersonId());

        int lineColor = model.getLifeStoryColorId();

        for(String eventId : personEvents){
            String eventType = allEvents.get(eventId).getEventType();

            if(model.getEventTypes().get(eventType).equals("t")) {

                Polyline line = map.addPolyline(new PolylineOptions().add(
                        new LatLng(clickedEvent.getLatitude(), clickedEvent.getLongitude()),
                        new LatLng(allEvents.get(eventId).getLatitude(), allEvents.get(eventId).getLongitude()))
                        .width(BASE_LINE_WIDTH)
                        .color(lineColor));
                lines.add(line);
            }
        }
    }

    private void drawSpouseLines(Event clickedEvent){
        if(!model.isSpouseLineOn()){
            return;
        }

        String spouseId = model.getPeople().get(clickedEvent.getPersonId()).getSpouseId();
        int lineColor = model.getSpouseLineColorId();

        drawUniqueLineToPersonEvent(spouseId, clickedEvent, lineColor, BASE_LINE_WIDTH);

    }

    private void drawFamilyLines(Event clickedEvent){
        if(!model.isFamilyTreeLineOn()){
            return;
        }

        int lineColor = model.getFamilyTreeColorId();
        drawFamilyLinesHelper(clickedEvent.getPersonId(), clickedEvent, lineColor, BASE_LINE_WIDTH);
    }

    private void drawFamilyLinesHelper(String personId, Event baseEvent, int lineColor, int lineWidth){
        assert personId != null;

        Log.i(TAG, "drawFamilyLineHelper(): drawing lines for the next generation");

        if(lineWidth < 1){
            lineWidth = 1;
        }

        Person person = model.getPeople().get(personId);

        if(person.getMotherId() != null){
            Event newBaseEvent = drawUniqueLineToPersonEvent(person.getMotherId(), baseEvent, lineColor, lineWidth);
            if(newBaseEvent != null){
                drawFamilyLinesHelper(person.getMotherId(), newBaseEvent, lineColor, lineWidth - 2);
            }
        }

        if(person.getFatherId() != null){
            Event newBaseEvent = drawUniqueLineToPersonEvent(person.getFatherId(), baseEvent, lineColor, lineWidth);
            if(newBaseEvent != null){
                drawFamilyLinesHelper(person.getFatherId(), newBaseEvent, lineColor, lineWidth - 2);
            }
        }

    }

    private Event drawUniqueLineToPersonEvent(String personId, Event childEvent, int lineColor, int lineWidth){

        List<String> personEvents = model.getPersonEvents(personId);

        for(String eventId : personEvents) {
            String eventType = allEvents.get(eventId).getEventType();

            if (model.getEventTypes().get(eventType).equals("t")) {
                Event event = allEvents.get(eventId);
                Polyline line = map.addPolyline(new PolylineOptions().add(
                        new LatLng(childEvent.getLatitude(), childEvent.getLongitude()),
                        new LatLng(event.getLatitude(), event.getLongitude()))
                        .width(lineWidth)
                        .color(lineColor));
                lines.add(line);

                return event;
            }
        }
        return null;
    }


    @Override
    public void onResume() {
        super.onResume();
        getMap();
    }
}


