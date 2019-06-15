package com.fmahieu.familymap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.client.models.Event;
import com.client.models.Model;
import com.client.models.Person;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private final String TAG = "SearchActivity";

    private Map<String, String> cities = new HashMap<>(); // <City, Event>
    private Map<String, String> countries = new HashMap<>(); // <Country, Event>
    private Map<String, String> types = new HashMap<>();
    private Map<String, String> years = new HashMap<>();
    private Map<String, String> firstNames = new HashMap<>();
    private Map<String, String> lastNames = new HashMap<>();

    Model model = Model.getInstance();
    private final Map<String, Event> allEvents = model.getEvents();
    private final Map<String, Person> people = model.getPeople();
    private Map<String, String> eventTypes = model.getEventTypes();

    private List<String> searchResult = new ArrayList<>();

    private SearchView mSearchView;
    private RecyclerView mSearchRecycler;
    private SearchAdapter mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        Log.i(TAG, "activity started");

        Iconify.with(new FontAwesomeModule());

        initMaps();

        mSearchView = (SearchView) findViewById(R.id.search_view);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "Search: " + query);
                // Search is dynamic, it updates every time the user change the text.
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "Search: " + newText);

                newText = newText.toLowerCase();
                searchThroughMaps(newText);

                Log.i(TAG, "Setting up recycler and adapter");
                // reset adapter
                mSearchRecycler = (RecyclerView) findViewById(R.id.search_recycler_view);
                mSearchRecycler.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                mSearchAdapter = new SearchAdapter();
                mSearchRecycler.setAdapter(mSearchAdapter);


                return false;
            }
        });

    }

    // TODO: turn it into an async task?
    private void initMaps() {
        Log.i(TAG, "initializing maps with current events and people...");

        cities.clear();
        countries.clear();
        years.clear();
        types.clear();
        firstNames.clear();
        lastNames.clear();

        // Events
        final Set<String> currentEvents = model.getCurrentEvents();
        for (String eventId : currentEvents) {
            Event event = allEvents.get(eventId);
            if (eventTypes.get(event.getEventType()).equals("t")) {
                cities.put(event.getCity().toLowerCase(), eventId);
                countries.put(event.getCountry().toLowerCase(), eventId);
                years.put(String.valueOf(event.getYear()), eventId);
                types.put(event.getEventType().toLowerCase(), eventId);
            }
        }

        // people
        for (Map.Entry<String, Person> pair : people.entrySet()) {
            firstNames.put(pair.getValue().getFirstName().toLowerCase(), pair.getKey());
            lastNames.put(pair.getValue().getLastName().toLowerCase(), pair.getKey());
        }
    }

    private void searchThroughMaps(String text) {
        Log.i(TAG, "Searching through the maps");

        searchResult.clear();



        if (text.equals("")) {
            return;
        }

        Set<String> eventsFound = new HashSet<>(); // Contains people that match the search
        Set<String> peopleFound = new HashSet<>();

        // Search for people
        for (Map.Entry<String, String> pair : firstNames.entrySet()) {
            if (pair.getKey().contains(text)) {
                peopleFound.add(pair.getValue());
            }
        }
        for (Map.Entry<String, String> pair : lastNames.entrySet()) {
            if (pair.getKey().contains(text)) {
                peopleFound.add(pair.getValue());
            }
        }


        // Search for events
        for (Map.Entry<String, String> pair : cities.entrySet()) {
            if (pair.getKey().contains(text)) {
                eventsFound.add(pair.getValue());
            }
        }

        for (Map.Entry<String, String> pair : countries.entrySet()) {
            if (pair.getKey().contains(text)) {
                eventsFound.add(pair.getValue());
            }
        }

        for (Map.Entry<String, String> pair : types.entrySet()) {
            if (pair.getKey().contains(text)) {
                eventsFound.add(pair.getValue());
            }
        }

        for (Map.Entry<String, String> pair : years.entrySet()) {
            if (pair.getKey().contains(text)) {
                eventsFound.add(pair.getValue());
            }
        }

        searchResult = new ArrayList<>(peopleFound);
        searchResult.addAll(eventsFound);
    }

    private class SearchElementHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIcon;
        private TextView mUpperText;
        private TextView mLowerText;
        private String objectId;
        private boolean isPerson;

        public SearchElementHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.search_holder_view, parent, false));
            Log.i(TAG, "Creating person Holder");

            mIcon = (ImageView) itemView.findViewById(R.id.search_element_icon_view);
            mUpperText = (TextView) itemView.findViewById(R.id.search_element_upper_view);
            mLowerText = (TextView) itemView.findViewById(R.id.search_element_lower_view);
        }

        public void bind(String objectId) {
            Log.i(TAG, "Binding holder");

            this.objectId = objectId;

            FontAwesomeIcons icon = FontAwesomeIcons.fa_map_marker;
            int colorId = android.R.color.darker_gray;
            String upperText = "";
            String lowerText = "";

            if (allEvents.containsKey(objectId)) {
                // object is an event
                isPerson = false;

                Event event = allEvents.get(objectId);
                Person person = people.get(event.getPersonId());

                upperText = String.format("%s: %s, %s (%d)", event.getEventType(), event.getCity(),
                                            event.getCountry(), event.getYear());

                lowerText = person.getFirstName() + " " + person.getLastName();
            }
            else{
                // object is a person
                isPerson = true;

                Person person = people.get(objectId);

                icon = FontAwesomeIcons.fa_female;
                colorId = R.color.femaleIconColor;

                if (person.getGender().equals("m")) {
                    icon = FontAwesomeIcons.fa_male;
                    colorId = R.color.maleIconColor;
                }
                upperText = person.getFirstName() + " " + person.getLastName();
            }

            Drawable genderIcon = new IconDrawable(SearchActivity.this, icon)
                    .colorRes(colorId).sizeDp(30);

            mIcon.setImageDrawable(genderIcon);
            mUpperText.setText(upperText);
            mLowerText.setText(lowerText);

            mIcon.setOnClickListener(this);
            mUpperText.setOnClickListener(this);
            mLowerText.setOnClickListener(this);

            // TODO: setListener
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Element has been clicked");
            Intent intent;
            if(isPerson){
                intent = PersonActivity.newIntent(SearchActivity.this, objectId);
            }
            else{
                intent = EventActivity.newIntent(SearchActivity.this, objectId);
            }
            startActivity(intent);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchElementHolder> {
        public SearchAdapter() {
            Log.i(TAG, "Creating adapter");
        }

        @Override
        public SearchElementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(SearchActivity.this);
            return new SearchElementHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(SearchElementHolder holder, int position) {
            holder.bind(searchResult.get(position));
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "adapter returned #" + searchResult.size());
            return searchResult.size();
        }
    }

}
