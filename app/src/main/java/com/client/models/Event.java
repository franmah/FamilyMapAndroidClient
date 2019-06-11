package com.client.models;

import java.util.Objects;
import java.util.UUID;

/**
 * Holds info for event objects.
 */
public class Event{

    private String eventID = null;
    private String associatedUsername = null;
    private String personID = null;
    private float latitude = 0;
    private float longitude = 0;
    private String country = null;
    private String city = null;
    private String eventType = null;
    private int year = 0;

    public Event(){}

    /**
     * Create an Event with given info
     */
    public Event(String user_name, String person_id, float latitude,
                 float longitude, String country, String city, String type, int year){
        this.eventID = UUID.randomUUID().toString();
        this.associatedUsername = user_name;
        this.personID = person_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = type;
        this.year = year;
    }

    public Event(String event_id, String user_name, String person_id, float latitude,
                 float longitude, String country, String city, String event_type, int year){
        this.eventID = event_id;
        this.associatedUsername = user_name;
        this.personID = person_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = event_type;
        this.year = year;
    }

    public String getEventId(){return eventID;}
    public String getAssociatedUsername(){return associatedUsername;}
    public String getPersonId(){return personID;}
    public float    getLatitude(){return latitude;}
    public float    getLongitude(){return longitude;}
    public String getCountry(){return country;}
    public String getCity(){return city;}
    public String getEventType(){return eventType;}
    public int getYear(){return year;}

    public void setEventType(String type){this.eventType = type;}

    @Override
    public String toString() {
        return "Event{" +
                "eventID='" + eventID + '\'' +
                ", associatedUsername='" + associatedUsername + '\'' +
                ", personID='" + personID + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", eventType='" + eventType + '\'' +
                ", year=" + year +
                '}';
    }

    public String printEventInfo(){
        return getEventType() + " " + getCity() +", " + getCountry() + " (" +getYear() + ")";
    }

}