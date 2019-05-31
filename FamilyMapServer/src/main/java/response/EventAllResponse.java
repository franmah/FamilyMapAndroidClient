package response;

import java.util.List;

import models.Event;

/**
 * Response used by EventAllService class.
 * Contain an array of Event.
 * Events can be added using an array and calling the constructor
 * or can be passed one by one using the add Event method.
 */
public class EventAllResponse extends Response{
    
    private List<Event> events = null;
    
    public EventAllResponse(){}
    
    public EventAllResponse(List<Event> events){
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}