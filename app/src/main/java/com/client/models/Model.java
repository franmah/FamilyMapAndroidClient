package com.client.models;

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
        personEvents = null;
        eventTypes = null;
        userPerson = null;
        maleAncestors = null;
        femaleAncestors = null;
    }


    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, List<Event>> personEvents;
    private Set<String> eventTypes;
    private Person userPerson;
    private Set<Person> maleAncestors;
    private Set<Person> femaleAncestors;

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

    public Map<String, List<Event>> getPersonEvents() {
        return personEvents;
    }

    public void setPersonEvents(Map<String, List<Event>> personEvents) {
        this.personEvents = personEvents;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(Set<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public Person getUser() {
        return userPerson;
    }

    public void setUserPerson(Person user) {
        this.userPerson = user;
    }

    public Set<Person> getMaleAncestors() {
        return maleAncestors;
    }

    public void setMaleAncestors(Set<Person> maleAncestors) {
        this.maleAncestors = maleAncestors;
    }

    public Set<Person> getFemaleAncestors() {
        return femaleAncestors;
    }

    public void setFemaleAncestors(Set<Person> femaleAncestors) {
        this.femaleAncestors = femaleAncestors;
    }
}
