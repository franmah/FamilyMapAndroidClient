package com.client.models;

import java.util.HashSet;
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
    }

    private String userToken = null;
    private String userPersonId = null;


    private Map<String, Person> people = null;
    private Map<String, Event> events = null; // Contain all events

    private Set<String> maleEvents = null;
    private Set<String> femaleEvents = null;

    private Set<String> eventMotherSide = null;
    private Set<String> eventFatherSide = null;

    private Set<String> eventMaleMotherSide = null;
    private Set<String> eventFemaleMotherSide = null;

    private Set<String> eventMaleFatherSide = null;
    private Set<String> eventFemaleFatherSide = null;

    private Set<String> malePeople = null;
    private Set<String> femalePeople = null;

    private Set<String> personMotherSide = null;
    private Set<String> personFatherSide = null;

    private Set<String> personMaleMotherSide = null;
    private Set<String> personFemaleMotherSide = null;

    private Set<String> personMaleFatherSide = null;
    private Set<String> personFemaleFatherSide = null;

    /** Store Filter preferences **/
    private Map<String, String> eventTypes = null; // Map<EventId, "t" or "f">

    /** Filled when a user click on a marker on the map. Used to draw the polylines */
    public Set<String> getPersonEvents(String personId){
        Set<String> currentEvents = getCurrentEvents();
        Set<String> result = new HashSet<>();
        for(String eventId : currentEvents){
            if(events.get(eventId).getPersonId().equals(personId)){
                result.add(eventId);
            }
        }
        return result;
    }

    public void updateEventType(String type, String value){
        eventTypes.put(type, value);
    }

    public Set<String> getCurrentEvents(){

        String fatherSide = eventTypes.get("Father's Side");
        String motherSide = eventTypes.get("Mother's Side");
        String maleOnly = eventTypes.get("Male");
        String femaleOnly = eventTypes.get("Female");

        if(maleOnly.equals("f") && femaleOnly.equals("f")){
            return null;
        }
        else if(fatherSide.equals("f") && motherSide.equals("f")){
            return getPersonEvents(userPersonId);
        }
        else if (fatherSide.equals("t") && motherSide.equals("t") &&
                maleOnly.equals("t") && femaleOnly.equals("t")){
            return events.keySet();
        }
        else if(fatherSide.equals("f") && motherSide.equals("t")){
            if(maleOnly.equals("t") && femaleOnly.equals("t")){
                return eventMotherSide;
            }
            else if(maleOnly.equals("t")){
                return eventMaleMotherSide;
            }
            else{
                return eventFemaleMotherSide;
            }
        }
        else if(fatherSide.equals("t") && motherSide.equals("f")){
            if(maleOnly.equals("t") && femaleOnly.equals("t")){
                return eventFatherSide;
            }
            else if(maleOnly.equals("t")){
                return eventMaleFatherSide;
            }
            else{
                return eventFemaleFatherSide;
            }
        }
        else if(fatherSide.equals("t") && motherSide.equals("t")){
            if(maleOnly.equals("t") && femaleOnly.equals("f")){
                return maleEvents;
            }
            else if(femaleOnly.equals("t") && maleOnly.equals("f")){
                return femaleEvents;
            }
        }
        return null;

    }

    /** GETTERS AND SETTERS **/
    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserPersonId() {
        return userPersonId;
    }

    public void setUserPersonId(String userPersonId) {
        this.userPersonId = userPersonId;
    }

    public static void setInstance(Model instance) {
        Model.instance = instance;
    }

    public Map<String, String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(Map<String, String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public void setPeople(Map<String, Person> people) {
        this.people = people;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }

    public Set<String> getMaleEvents() {
        return maleEvents;
    }

    public void setMaleEvents(Set<String> maleEvents) {
        this.maleEvents = maleEvents;
    }

    public Set<String> getFemaleEvents() {
        return femaleEvents;
    }

    public void setFemaleEvents(Set<String> femaleEvents) {
        this.femaleEvents = femaleEvents;
    }

    public Set<String> getMalePeople() {
        return malePeople;
    }

    public void setMalePeople(Set<String> malePeople) {
        this.malePeople = malePeople;
    }

    public Set<String> getFemalePeople() {
        return femalePeople;
    }

    public void setFemalePeople(Set<String> femalePeople) {
        this.femalePeople = femalePeople;
    }

    public Set<String> getEventMotherSide() {
        return eventMotherSide;
    }

    public void setEventMotherSide(Set<String> eventMotherSide) {
        this.eventMotherSide = eventMotherSide;
    }

    public Set<String> getEventFatherSide() {
        return eventFatherSide;
    }

    public void setEventFatherSide(Set<String> eventFatherSide) {
        this.eventFatherSide = eventFatherSide;
    }

    public Set<String> getPersonMotherSide() {
        return personMotherSide;
    }

    public void setPersonMotherSide(Set<String> personMotherSide) {
        this.personMotherSide = personMotherSide;
    }

    public Set<String> getPersonFatherSide() {
        return personFatherSide;
    }

    public void setPersonFatherSide(Set<String> personFatherSide) {
        this.personFatherSide = personFatherSide;
    }

    public Set<String> getEventMaleFatherSide() {
        return eventMaleFatherSide;
    }

    public void setEventMaleFatherSide(Set<String> eventMaleFatherSide) {
        this.eventMaleFatherSide = eventMaleFatherSide;
    }

    public Set<String> getEventFemaleFatherSide() {
        return eventFemaleFatherSide;
    }

    public void setEventFemaleFatherSide(Set<String> eventFemaleFatherSide) {
        this.eventFemaleFatherSide = eventFemaleFatherSide;
    }

    public Set<String> getEventMaleMotherSide() {
        return eventMaleMotherSide;
    }

    public void setEventMaleMotherSide(Set<String> eventMaleMotherSide) {
        this.eventMaleMotherSide = eventMaleMotherSide;
    }

    public Set<String> getEventFemaleMotherSide() {
        return eventFemaleMotherSide;
    }

    public void setEventFemaleMotherSide(Set<String> eventFemaleMotherSide) {
        this.eventFemaleMotherSide = eventFemaleMotherSide;
    }

    public Set<String> getPersonMaleMotherSide() {
        return personMaleMotherSide;
    }

    public void setPersonMaleMotherSide(Set<String> personMaleMotherSide) {
        this.personMaleMotherSide = personMaleMotherSide;
    }

    public Set<String> getPersonFemaleMotherSide() {
        return personFemaleMotherSide;
    }

    public void setPersonFemaleMotherSide(Set<String> personFemaleMotherSide) {
        this.personFemaleMotherSide = personFemaleMotherSide;
    }

    public Set<String> getPersonMaleFatherSide() {
        return personMaleFatherSide;
    }

    public void setPersonMaleFatherSide(Set<String> personMaleFatherSide) {
        this.personMaleFatherSide = personMaleFatherSide;
    }

    public Set<String> getPeronsFemaleFatherSide() {
        return personFemaleFatherSide;
    }

    public void setPersonFemaleFatherSide(Set<String> peronsFemaleFatherSide) {
        this.personFemaleFatherSide = peronsFemaleFatherSide;
    }
}

