package com.fmahieu.familymap.client.response;

import java.util.List;

import com.fmahieu.familymap.client.models.Event;

/**
 * Response used by EventAllService class.
 * Contain an array of Event.
 * Events can be added using an array and calling the constructor
 * or can be passed one by one using the add Event method.
 */
public class EventAllResponse {
    
    private List<Event> events = null;
    private String error_message = null;
    
    public EventAllResponse(){}
    
    public EventAllResponse(List<Event> events){
        this.events = events;
    }

    public EventAllResponse(String error_message){
        this.error_message = error_message;
    }

    public List<Event> getEvents() {
        return events;
    }

    public String getErrorMessage() { return error_message; }
}