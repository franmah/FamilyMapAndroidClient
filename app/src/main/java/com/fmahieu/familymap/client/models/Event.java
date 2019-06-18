package com.fmahieu.familymap.client.models;

import java.util.UUID;

/**
 * Holds info for event objects.
 */
public class Event {

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