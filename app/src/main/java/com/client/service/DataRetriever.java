package com.client.service;

import android.os.AsyncTask;
import android.util.Log;

import com.client.httpClient.ServerProxy;
import com.client.models.*;
import com.client.response.EventAllResponse;
import com.client.response.PersonAllResponse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DataRetriever {
    private final String TAG = "DataRetriever";

    private ServerProxy proxy = null;
    private Model model = null;

    private Set<String> familySide = new HashSet<>();
    private Set<String> familySideMale = new HashSet<>();
    private Set<String> familySideFemale = new HashSet<>();

    private Set<String> eventFamilySide = new HashSet<>();
    private Set<String> eventFemaleFamilySide = new HashSet<>();
    private Set<String> eventMaleFamilySide = new HashSet<>();

    private String userPersonId = null;
    private Map<String, Person> people = null;

    /** Use the proxy client to get the data from the server
     *
     * @param hostName
     * @param portNumber
     * @return null if no error, else: return the error message received from the server
     */
    public String pullData(String hostName, String portNumber){
        Log.i(TAG, "Pulling data from server...");
        try{
            proxy = new ServerProxy(hostName, portNumber);
            model = Model.getInstance();
            String token = model.getUserToken();
            userPersonId = model.getUserPersonId();


            // Retrieve People
            System.out.println("DataRetriever.pullData(): sending request for people...");
            String message = getPeople(token, userPersonId);
            if(message != null){
                return message;
            }

            // Retrieve events
            System.out.println("DataRetriever.pullData(): sending request for events...");
            message = getEvents(token);

            // check if there was an error:
            if(message != null){
                return message;
            }

            Log.i(TAG, "Starting aSyncTask to create family side people/events for filter");
            new createFatherMotherSide().execute();

            Log.i(TAG, "Data received and stored");
            return null;
        }
        catch (Exception e){
            System.out.println("DataRetriever.pullData(): error: " +e.toString());
            e.printStackTrace();
            return "Error while retrieving events and people";
        }
    }

    private String getEvents(String token) throws Exception{
        EventAllResponse response = proxy.getEvents(token);

        if(response.getErrorMessage() != null){
            System.out.println("DataRetriever.getEvents(): events response came back with an error");
            return response.getErrorMessage();
        }

        System.out.println("DataRetriever.getEvents(): events response came back with array of events");
        Map<String, Event> result = new TreeMap<>();
        Map<String, String> eventTypes = new HashMap<>();
        Set<String> maleEvents = new HashSet<>();
        Set<String> femaleEvents = new HashSet<>();
        Set<String> userEvents = new HashSet<>();

        for(Event event : response.getEvents()){
            assert event.getEventType().length() > 0;

            // Set type to lowerCase and the first letter to upperCase
            String type = event.getEventType().toLowerCase();
            String typeEntry = type.substring(0,1).toUpperCase() + type.substring(1);

            if(!eventTypes.containsKey(typeEntry)) {
                eventTypes.put(typeEntry, "t");
            }

            if(model.getPeople().get(event.getPersonId()).getGender().equals("m")){
                maleEvents.add(event.getEventId());
            }
            else{
                femaleEvents.add(event.getEventId());
            }

            if(event.getPersonId() == userPersonId){
                userEvents.add(event.getEventId());
            }

            event.setEventType(typeEntry); // Update the event type. (avoid lower/upper cases differences)
            result.put(event.getEventId(), event);
        }

        // Add Gender/Side events
        eventTypes.put("Father's Side", "t");
        eventTypes.put("Mother's Side", "t");
        eventTypes.put("Male", "t");
        eventTypes.put("Female", "t");

        model.setEvents(result);
        model.setEventTypes(eventTypes);
        model.setMaleEvents(maleEvents);
        model.setFemaleEvents(femaleEvents);

        return null;
    }

    private String getPeople(String token, String userPersonId) throws Exception{
        PersonAllResponse response = proxy.getPeople(token);

        if(response.getErrorMessage() != null){
            return response.getErrorMessage();
        }

        people = new TreeMap<>();
        Set<String> maleAncestors = new HashSet<>();
        Set<String> femaleAncestors = new HashSet<>();

        for(Person person : response.getPeople()){
            people.put(person.getPersonId(), person);

            if(person.getGender().equals("f")){
                femaleAncestors.add(person.getPersonId());
            }
            else{
                maleAncestors.add(person.getPersonId());
            }
        }

        model.setPeople(people);
        model.setMalePeople(maleAncestors);
        model.setFemalePeople(femaleAncestors);
        return null;
    }

    private class createFatherMotherSide extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params){
            Log.i(TAG, "createFatherMotherSide(): generating family side events and people");

            if(model == null){
                model = Model.getInstance();
            }

            // Set mother side of the family.
            familySide.clear();
            familySideFemale.clear();
            familySideMale.clear();
            eventFamilySide.clear();
            eventMaleFamilySide.clear();
            eventFemaleFamilySide.clear();

            Log.i(TAG, "createFatherMotherSide(): creating mother side");
            createFamilySide(model.getPeople().get(userPersonId).getMotherId());
            model.setPersonMotherSide(familySide);
            model.setPersonMaleMotherSide(familySideMale);
            model.setPersonFemaleMotherSide(familySideFemale);
            model.setEventMotherSide(eventFamilySide);
            model.setEventFemaleMotherSide(eventFemaleFamilySide);
            model.setEventMaleMotherSide(eventMaleFamilySide);

            // Set father side of the family.

            familySide.clear();
            familySideFemale.clear();
            familySideMale.clear();
            eventFamilySide.clear();
            eventMaleFamilySide.clear();
            eventFemaleFamilySide.clear();

            Log.i(TAG, "createFatherMotherSide(): creating father side");
            createFamilySide(model.getPeople().get(userPersonId).getFatherId());
            model.setPersonFatherSide(familySide);
            model.setPersonMaleFatherSide(familySideMale);
            model.setPersonFemaleFatherSide(familySideMale);
            model.setEventFatherSide(eventFamilySide);
            model.setEventMaleFatherSide(eventMaleFamilySide);
            model.setEventFemaleFatherSide(eventFemaleFamilySide);


            Log.i(TAG, "createFatherMotherSide(): both sides have been successfully created, returning...");
            return null;
        }
    }

    public void createFamilySide(String personID){
        if(personID == null){
            return;
        }

        boolean isMale = true;
        if(people.get(personID).getGender().equals("f")){
            isMale = false;
        }

        familySide.add(personID);

        // Add to male/female side of family
        if(isMale){
            familySideMale.add(personID);
        }
        else{
            familySideFemale.add(personID);
        }

        // Add events to family side + Male/Female family side
        List<String> eventIds = model.getPersonEvents(personID);
        for(String eventId : eventIds){
            eventFamilySide.add(eventId);
            if(isMale){
                eventMaleFamilySide.add(eventId);
            }
            else{
                eventFemaleFamilySide.add(eventId);
            }
        }

        // Mother side:
        createFamilySide(model.getPeople().get(personID).getMotherId());

        // Father side:
        createFamilySide(model.getPeople().get(personID).getFatherId());


    }




}
