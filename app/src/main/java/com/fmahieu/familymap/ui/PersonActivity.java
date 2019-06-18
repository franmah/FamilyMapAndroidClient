package com.fmahieu.familymap.ui;

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

import com.fmahieu.familymap.R;
import com.fmahieu.familymap.client.models.Event;
import com.fmahieu.familymap.client.models.Model;
import com.fmahieu.familymap.client.models.Person;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
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
        Log.i(TAG, "person activity started");

        setContentView(R.layout.person_activity);

        Iconify.with(new FontAwesomeModule());

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
            Log.i(TAG, "RetrievePerson: showing info for : " + person.printName());
        }
        else{
            Log.e(TAG, "RetrievePerson(): personId is null, returning...");
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
        private TextView mEventInfo;
        private TextView mEventPersonName;
        private String eventId;

        public LifeEventsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.person_event_holder, parent, false));
            Log.i(TAG, "LifeEventsHolder(): inflating view holder");

            mEventIcon = (ImageView) itemView.findViewById(R.id.holder_event_icon);
            mEventInfo = (TextView) itemView.findViewById(R.id.holder_event_type);
            mEventPersonName = (TextView) itemView.findViewById(R.id.holder_person_name);
        }

        public void bind(String eventId){
            Log.i(TAG, "LifeEventsHolder.bind(): binding, creating view for event");

            this.eventId = eventId;

            mEventIcon.setImageDrawable(new IconDrawable(PersonActivity.this,
                    FontAwesomeIcons.fa_map_marker)
                    .colorRes(android.R.color.darker_gray)
                    .sizeDp(40));

            Event event = allEvents.get(eventId);

            String type = event.getEventType().toLowerCase();
            String city = event.getCity();
            String country = event.getCountry();
            String year = String.valueOf(event.getYear());

            mEventInfo.setText(String.format("%s : %s, %s (%s)",type, city, country, year));
            mEventPersonName.setText(person.printName());

            mEventIcon.setOnClickListener(this);
            mEventInfo.setOnClickListener(this);
            mEventPersonName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "starting EventActivity with event: " + eventId);
            Intent intent = EventActivity.newIntent(PersonActivity.this, eventId);
            startActivity(intent);
        }
    }

    private class LifeEventsAdapter extends RecyclerView.Adapter<LifeEventsHolder>{
        private List<String> eventIds;


        public LifeEventsAdapter() {
            Log.i(TAG, "LifeEventsAdapter(): creating new adapter");

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
        private String familyMemberId;

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

            familyMemberId = personId;
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
                    .sizeDp(40));;

            mPersonName.setText(person.printName());
            mPersonRelationship.setText(relationship);

        }

        @Override
        public void onClick(View v) {
            Intent intent = PersonActivity.newIntent(PersonActivity.this, familyMemberId);
            startActivity(intent);
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
