package response;

import models.Event;

/**
 * Response used by EventService class.
 * Return the info for a single event.
 */
public class EventResponse extends Response{



    private String event_id = null;
    private String user_name = null;
    private String person_id = null;
    private float latitude = 0;
    private float longitude = 0;
    private String country = null;
    private String city = null;
    private String type = null;
    private int year = 0;

    public EventResponse(){}

    public EventResponse(String event_id, String user_name, String person_id, float latitude,
                         float longitude, String country, String city, String type, int year) {

        this.event_id = event_id;
        this.user_name = user_name;
        this.person_id = person_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.type = type;
        this.year = year;
    }


}