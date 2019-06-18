package com.fmahieu.familymap.ui;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.fmahieu.familymap.R;
import com.fmahieu.familymap.client.models.Event;
import com.fmahieu.familymap.client.models.Model;
import com.fmahieu.familymap.client.models.Person;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class MainMapFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "MainMapFragment";

    private static final String ARG_EVENT_ID = "com.fmahieu.familymap.eventIdArgument";
    private static final String SAVED_EVENT_ID = "com.fmahieu.familymap.savedEvent";

    private final int DEFAULT_LINE_WIDTH = 10;
    private final float ZOOM_LEVEL = 5;

    private String eventIdFromEventActivity = null;
    private String savedEventId = null;
    private String currentEventId = null;

    private ImageView mIcon;
    private TextView mPersonName;
    private TextView mEventInfo;

    private GoogleMap map;
    private Model model = Model.getInstance();

    private List<Polyline> lines = new ArrayList<>();
    private Map<Marker, String> markerToEvent = new HashMap<>(); // Events to show according to filters

    private Map<String, String> eventTypes = model.getEventTypes();
    private Map<String, String> eventTypeColors = model.getEventTypeColors();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainMapFragment started");

        eventIdFromEventActivity = (String) getArguments().getString(ARG_EVENT_ID);

        if (eventIdFromEventActivity == null) {
            setHasOptionsMenu(true);
        }

        if(savedInstanceState != null){
            savedEventId = savedInstanceState.getString(SAVED_EVENT_ID);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Log.i(TAG, "onCreateView(): Loading/drawing map ...");
        initWidgets(view);
        getMap();

        return view;
    }

    public static MainMapFragment newInstance(String eventId) {
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        MainMapFragment fragment = new MainMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /** MENU **/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
        switch (item.getItemId()) {
            case R.id.search_item:
                Log.i(TAG, "onOptionsItemSelected(): Starting SearchActivity");
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.filter_item:
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

    /** WIDGETS **/

    private void initWidgets(View view) {
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

        // Set listener
        mIcon.setOnClickListener(this);
        mPersonName.setOnClickListener(this);
        mEventInfo.setOnClickListener(this);
    }

    /** MAP **/

    private void getMap() {
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

    private void initMap() {
        map.clear();
        setMarkers();
        setMarkerListener();

        Log.i("TESTING", "before setting up savedEventId");
        if (eventIdFromEventActivity != null) {
            updateMapUi(eventIdFromEventActivity);
        }
        else if(currentEventId != null){
            Log.i("TESTING","ZOOMING ON  SAVED ID");
            updateMapUi(currentEventId);
            savedEventId = null;
        }
    }

    /** MARKERS **/

    private void setMarkers() {
        assert map != null;
        Log.i(TAG, "placing markers");

        map.clear();
        markerToEvent.clear();

        Set<String> mapEvents = model.getCurrentEvents();
        if (mapEvents == null) {
            return; // No events to show because of Filters.
        }

        for (String eventId : mapEvents) {
            Event event = model.getEvents().get(eventId);

            if (eventTypes.get(event.getEventType()).equals("t")) {
                float color = Float.parseFloat(eventTypeColors.get(event.getEventType()));

                LatLng position = new LatLng(event.getLatitude(), event.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(position)
                                .icon(BitmapDescriptorFactory
                                .defaultMarker(color));

                Marker marker = map.addMarker(markerOptions);
                marker.setTag(event.getEventId());

                markerToEvent.put(marker, eventId);
            }

        }

        Log.i(TAG, "Markers have been placed");
    }

    private void setMarkerListener() {
        Log.i(TAG, "setMarkerListener(): Creating listener for map markers");

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG, "Marker has been clicked");
                String eventClicked = markerToEvent.get(marker);
                updateMapUi(eventClicked);
                return false;
            }
        });
    }

    private void updateMapUi(String eventId) {
        Log.i(TAG, "updating map ...");
        Event event = model.getEvents().get(eventId);
        LatLng position = new LatLng(event.getLatitude(), event.getLongitude());

        currentEventId = eventId;

        centerAndZoomMap(position);
        drawLines(event);

        // Update icon + text
        Person person = model.getPeople().get(event.getPersonId());
        mPersonName.setText(person.printName());
        mEventInfo.setText(event.printEventInfo());

        FontAwesomeIcons iconTmp = FontAwesomeIcons.fa_male;
        int colorTmp = R.color.maleIconColor;

        if (person.getGender().equals("f")) {
            iconTmp = FontAwesomeIcons.fa_female;
            colorTmp = R.color.femaleIconColor;
        }

        Drawable genderIcon = new IconDrawable(getActivity(), iconTmp).colorRes(colorTmp).sizeDp(40);
        mIcon.setImageDrawable(genderIcon);
    }

    private void centerAndZoomMap(LatLng position) {
        Log.i(TAG, "centerAndZoomMap(): Centering and zooming on map...");

        // Move camera
        CameraUpdate update = CameraUpdateFactory.newLatLng(position);
        map.moveCamera(update);

        // Zoom on map
        update = CameraUpdateFactory.zoomTo(ZOOM_LEVEL);
        map.animateCamera(update);

    }

    /** EVENT LINES **/

    private void drawLines(Event clickedEvent) {

        if (markerToEvent == null || markerToEvent.size() == 0) {
            return;
        }

        Log.i(TAG, "drawLines(): removing previous lines if any");
        // Remove previous lines
        for (Polyline line : lines) {
            line.remove();
        }
        lines.clear();

        drawLifeStoryLines(clickedEvent);
        drawSpouseLines(clickedEvent);
        drawFamilyLines(clickedEvent);

    }

    private void drawLifeStoryLines(Event clickedEvent) {
        Log.i(TAG, "draLifeStoryLines(): drawing new lines for event: " + clickedEvent.getEventId());

        if (!model.isLifeStoryLineOn()) {
            return;
        }

        int lineColor = model.getLifeStoryColorId();
        List<String> personEvents = model.getPersonEvents(clickedEvent.getPersonId());
        Event baseEvent = null;

        for (String eventId : personEvents) {
            String eventType = model.getEvents().get(eventId).getEventType();

            if (eventTypes.get(eventType).equals("t")) {
                Event event = model.getEvents().get(eventId);

                if (baseEvent != null) {Polyline line = map.addPolyline(new PolylineOptions().add(
                        new LatLng(baseEvent.getLatitude(), baseEvent.getLongitude()),
                        new LatLng(event.getLatitude(), event.getLongitude()))
                        .width(DEFAULT_LINE_WIDTH)
                        .color(lineColor));
                    lines.add(line);

                    baseEvent = event;
                }
                else {
                    //Event to start from
                    baseEvent = event;
                }
            }
        }
    }

    private void drawSpouseLines(Event clickedEvent) {
        if (!model.isSpouseLineOn()) {
            return;
        }

        String spouseId = model.getPeople().get(clickedEvent.getPersonId()).getSpouseId();
        int lineColor = model.getSpouseLineColorId();

        drawUniqueLineToPersonEvent(spouseId, clickedEvent, lineColor, DEFAULT_LINE_WIDTH);
    }

    private void drawFamilyLines(Event clickedEvent) {
        if (!model.isFamilyTreeLineOn()) {
            return;
        }

        int lineColor = model.getFamilyTreeColorId();
        drawFamilyLinesHelper(clickedEvent.getPersonId(), clickedEvent, lineColor, DEFAULT_LINE_WIDTH);
    }

    private void drawFamilyLinesHelper(String personId, Event baseEvent, int lineColor, int lineWidth) {
        assert personId != null;

        Log.i(TAG, "drawFamilyLineHelper(): drawing lines for the next generation");

        if (lineWidth < 1) {
            lineWidth = 1;
        }

        Person person = model.getPeople().get(personId);

        if (person.getMotherId() != null) {
            Event newBaseEvent = drawUniqueLineToPersonEvent(person.getMotherId(), baseEvent, lineColor, lineWidth);
            if (newBaseEvent != null) {
                drawFamilyLinesHelper(person.getMotherId(), newBaseEvent, lineColor, lineWidth - 2);
            }
        }

        if (person.getFatherId() != null) {
            Event newBaseEvent = drawUniqueLineToPersonEvent(person.getFatherId(), baseEvent, lineColor, lineWidth);
            if (newBaseEvent != null) {
                drawFamilyLinesHelper(person.getFatherId(), newBaseEvent, lineColor, lineWidth - 2);
            }
        }

    }

    private Event drawUniqueLineToPersonEvent(String personId, Event childEvent, int lineColor, int lineWidth) {

        List<String> personEvents = model.getPersonEvents(personId);

        for (String eventId : personEvents) {
            String eventType = model.getEvents().get(eventId).getEventType();
            if (markerToEvent.containsValue(eventId) && model.getEventTypes().get(eventType).equals("t")) {
                Event event = model.getEvents().get(eventId);
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
    public void onClick(View v) {
        Log.i(TAG, "person widget has been clicked");
        if (currentEventId != null) {
            Intent intent = PersonActivity.newIntent(getContext(), model.getEvents().get(currentEventId).getPersonId());

            Log.i(TAG, "starting person activity");
            startActivity(intent);
            // TODO: change it to get a result back
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "activity resuming");
        super.onResume();
        model = Model.getInstance();
        getMap();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState: saving event id");
        Log.i("TESTING", "onSaveInstanceCalled");
        if(currentEventId != null) {
            Log.i("TESTING", "currentEventId is not null, saving");
            savedInstanceState.putString(SAVED_EVENT_ID, currentEventId);
        }
    }
}


