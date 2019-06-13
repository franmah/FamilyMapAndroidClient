package com.fmahieu.familymap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.models.Event;
import com.client.models.Model;
import com.client.models.Person;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "PersonActivity";
    private static final String PERSON_ID = "com.fmahieu.familymap.personId";
    private Person person = null;

    private TextView mFirstName;
    private TextView mLastName;
    private TextView mGender;
    private TextView mLifeEventsText;
    private TextView mFamilyText;

    private ImageView mLifeEventsIcon;
    private ImageView mFamilyIcon;

    private boolean isLifeEventsRecyclerVisible = false;
    private boolean isFamilyRecyclerVisible = false;

    private RecyclerView mLifeEventsRecycler;
    private LifeEventsAdapter mLifeEventAdapter;

    private RecyclerView mFamilyRecycler;
    private FamilyAdapter mFamilyAdapter;

    private Model model = Model.getInstance();
    private final Map<String, Event> allEvents = model.getEvents();
    private final Map<String, Person> allPeople = model.getPeople();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "activity started");
        setContentView(R.layout.person_activity);

        Iconify.with(new FontAwesomeModule());
        // TODO: add icons

        retrievePerson();
        initPersonInfoWidgets();
        initRecyclerWidgets();
    }

    public static Intent newIntent(Context context, String personId){
        Log.i("PersonActivity", "creating new Intent..");
        Intent intent = new Intent(context, PersonActivity.class);
        intent.putExtra(PERSON_ID, personId);
        return intent;
    }

    private void retrievePerson(){
        String personId = getIntent().getStringExtra(PERSON_ID);
        if(personId != null){
            person = model.getPeople().get(personId);
            Log.i(TAG, "showing info for : " + person.getFirstName() + " " + person.getLastName());
        }
        else{
            Log.e(TAG, "personId is null, returning...");
            finish();
        }
    }

    private void initPersonInfoWidgets(){
        mFirstName = (TextView) findViewById(R.id.first_name_textView);
        mLastName = (TextView) findViewById(R.id.last_name_textView);
        mGender = (TextView) findViewById(R.id.gender_textView);

        mFirstName.setText(person.getFirstName());
        mLastName.setText(person.getLastName());

        String gender = "Male";
        if(person.getLastName().equals("f")){
            gender = "Female";
        }
        mGender.setText(gender);
    }

    private void initRecyclerWidgets(){
        Log.i(TAG, "initRecyclerWidgets(): setting up recycler widgets.");

        // Initialize LifeEvents & Family texts/icons
        mLifeEventsText = (TextView) findViewById(R.id.life_events_textView);
        mFamilyText = (TextView) findViewById(R.id.family_textView);

        mLifeEventsText.setOnClickListener(this);
        mFamilyText.setOnClickListener(this);

        // Initialize recyclers
        mLifeEventsRecycler = (RecyclerView) findViewById(R.id.life_events_recycler);
        mLifeEventsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mLifeEventAdapter = new LifeEventsAdapter();
        mLifeEventsRecycler.setAdapter(mLifeEventAdapter);

        mFamilyRecycler = (RecyclerView) findViewById(R.id.family_recycler);
        mFamilyRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFamilyAdapter = new FamilyAdapter();
        mFamilyRecycler.setAdapter(mFamilyAdapter);

    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: A recycler text/icon has been clicked");
        switch(v.getId()){
            case R.id.life_events_textView:
                if(isLifeEventsRecyclerVisible) {
                    mLifeEventsRecycler.setVisibility(View.GONE);
                    isLifeEventsRecyclerVisible = false;
                }
                else{
                    mLifeEventsRecycler.setVisibility(View.VISIBLE);
                    isLifeEventsRecyclerVisible = true;
                }
                break;

            case R.id.family_textView:
                if(isFamilyRecyclerVisible){
                    mFamilyRecycler.setVisibility(View.GONE);
                    isFamilyRecyclerVisible = false;
                }
                else{
                    mFamilyRecycler.setVisibility(View.VISIBLE);
                    isFamilyRecyclerVisible = true;
                }
                break;

            default:
                break;
        }
    }

    /** LifeEventRecycler Holder & Adapter **/
    private class LifeEventsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mEventIcon;
        private TextView mEventInfo = null;
        private TextView mEventPersonName;

        public LifeEventsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.person_event_holder, parent, false));
            Log.i(TAG, "LifeEventsHolder(): inflating view holder");

            mEventIcon = (ImageView) itemView.findViewById(R.id.holder_event_icon);
            mEventInfo = (TextView) itemView.findViewById(R.id.holder_event_type);
            mEventPersonName = (TextView) itemView.findViewById(R.id.holder_person_name);
        }

        public void bind(String eventId){
            Log.i(TAG, "LifeEventsHolder.bind(): binding, creating view for event");
            // TODO: ? store the id to call the event activity?

            mEventIcon.setImageDrawable(new IconDrawable(PersonActivity.this,
                    FontAwesomeIcons.fa_map_marker)
                    .colorRes(android.R.color.darker_gray)
                    .sizeDp(40));


            Event event = allEvents.get(eventId);

            String type = event.getEventType().toLowerCase();
            String city = event.getCity();
            String country = event.getCountry();
            String year = String.valueOf(event.getYear());
            String personName =  person.getFirstName() + " " + person.getLastName();

            mEventInfo.setText(String.format("%s : %s, %s (%s)",type, city, country, year));
            mEventPersonName.setText(personName);

            mEventIcon.setOnClickListener(this);
            mEventInfo.setOnClickListener(this);
            mEventPersonName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String str = String.format("Event: " + mEventInfo.getText().toString() + " clicked");
            Toast.makeText(PersonActivity.this, str , Toast.LENGTH_SHORT).show();

            //START EVENT ACTIVITY
        }
    }

    private class LifeEventsAdapter extends RecyclerView.Adapter<LifeEventsHolder>{
        private List<String> eventIds;


        public LifeEventsAdapter() {
            Log.i(TAG, "LifeEventsAdapter(): creating new adapter");

            // TODO: should getPersonEvents return only filtered events ?
            // would be useful to avoid all that overhead. How about making another method called getPersonFilteredEvents() ?
            eventIds = new ArrayList<>();
            List<String> personEvents = model.getPersonEvents(person.getPersonId());
            for(String eventId : personEvents) {
                String eventType = model.getEvents().get(eventId).getEventType();
                if (model.getEventTypes().get(eventType).equals("t")) {
                    eventIds.add(eventId);
                }
            }

        }

        @Override
        public LifeEventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(PersonActivity.this);
            return new LifeEventsHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(LifeEventsHolder holder, int position) {
            Log.i(TAG,"LifeEventsAdapter.onBindViewHolder: binding event to holder");
            holder.bind(eventIds.get(position));
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "LifeEventsAdapter.getItemCount(): returning #" + eventIds.size() + " event(s)");
            return eventIds.size();
        }
    }


    /** FamilyRecycler Holder & Adapter **/
    private class FamilyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPersonIcon;
        private TextView mPersonName;
        private TextView mPersonRelationship;

        public FamilyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.person_family_holder, parent, false));
            Log.i(TAG, "FamilyHolder(): inflating view holder");

            mPersonIcon = (ImageView) itemView.findViewById(R.id.holder_gender_icon);
            mPersonName = (TextView) itemView.findViewById(R.id.holder_person_name);
            mPersonRelationship = (TextView) itemView.findViewById(R.id.holder_person_relation);

            mPersonIcon.setOnClickListener(this);
            mPersonName.setOnClickListener(this);
            mPersonRelationship.setOnClickListener(this);
        }

        public void bind(String personId, String relationship){
            Log.i(TAG, "FamilyHolder.bind(): binding, creating view for family member");

            Person person = allPeople.get(personId);

            FontAwesomeIcons iconImage = FontAwesomeIcons.fa_female;
            int iconColor = R.color.femaleIconColor;
            if(person.getGender().equals("m")){
                iconImage = FontAwesomeIcons.fa_male;
                iconColor = R.color.maleIconColor;
            }

            mPersonIcon.setImageDrawable(new IconDrawable(PersonActivity.this,
                    iconImage)
                    .colorRes(iconColor)
                    .sizeDp(40));

            String personName = person.getFirstName() + " " + person.getLastName();
            mPersonName.setText(personName);

            mPersonRelationship.setText(relationship);

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(PersonActivity.this, "Family member clicked", Toast.LENGTH_SHORT).show();
            // TODO: call person activity
        }
    }

    private class FamilyAdapter extends RecyclerView.Adapter<FamilyHolder>{

        private Map<String, String> familyMemberRelationships; //<PersonId, relationship>
        private List<String> familyMember;


        public FamilyAdapter() {
            Log.i(TAG, "FamilyAdapter(): creating new adapter");

            familyMemberRelationships = model.getPersonFamily(person.getPersonId());
            familyMember = new ArrayList<>(familyMemberRelationships.keySet());
        }

        @Override
        public FamilyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(PersonActivity.this);
            return new FamilyHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FamilyHolder holder, int position) {
            holder.bind(familyMember.get(position),
                            familyMemberRelationships.get(familyMember.get(position)));
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "FamilyAdapter.getItemCount(): returning #" +
                                familyMember.size() + " person(s)");

            return familyMember.size();
        }
    }
}
