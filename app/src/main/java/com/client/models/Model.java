package com.client.models;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Model {

    private static Model instance = null;

    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }

    private Model(){
        people = null;
        events = null;
        eventTypes = null;
        userPerson = null;
    }


    private Map<String, Person> people;
    private Map<String, Event> events;
    private Set<String> eventTypes;
    private Person userPerson;

    private String userToken = null;
    private String userPersonId = null;

    /** personEvents is filled when the MapFragment/PersonActivity need to know someone's events
     * every time getPersonEvents is called the map is modified
     */
    private Set<Event> personEvents = new HashSet<>();

    /**** METHODS ***/

    public Map<String, Person> getPeople() { return people; }
    public void setPeople(Map<String, Person> people) { this.people = people; }
    public Person getPerson(String personId){ return people.get(personId); }

    public Map<String, Event> getEvents() { return events; }
    public void setEvents(Map<String, Event> events) { this.events = events; }

    public Set<String> getEventTypes() { return eventTypes; }
    public void setEventTypes(Set<String> eventTypes) { this.eventTypes = eventTypes; }

    public Person getUserPerson() { return userPerson; }
    public void setUserPerson(Person user) { this.userPerson = user; }

    public String getUserToken() { return userToken; }
    public void setUserToken(String userToken) { this.userToken = userToken; }

    public String getUserPersonId() { return userPersonId; }
    public void setUserPersonId(String userPersonId) { this.userPersonId = userPersonId; }


    public Set<Event> getPersonEvents(String personId) {
        personEvents.clear();

        for (Map.Entry<String, Event> pair : events.entrySet()) {
            if(pair.getValue().getPersonId().equals(personId)){
                personEvents.add(pair.getValue());
            }
        }
        return personEvents;
    }
}
