package com.fmahieu.familymap.client.models;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ROSE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;


public class Model {
    private final String TAG = "Model";

    private static Model instance = null;

    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }

    private Model(){
    }

    private String hostName = null;
    private String portNumber = null;

    private String userToken = null;
    private String userPersonId = null;

    private Map<String, Person> people = null;
    private Map<String, Event> events = null; // Contain all events

    private Set<String> userEvents = null;

    private Set<String> maleEvents = null;
    private Set<String> femaleEvents = null;

    private Set<String> eventMotherSide = null;
    private Set<String> eventFatherSide = null;

    private Set<String> eventMaleMotherSide = null;
    private Set<String> eventFemaleMotherSide = null;

    private Set<String> eventMaleFatherSide = null;
    private Set<String> eventFemaleFatherSide = null;

    /** COLORS FOR MAKERS **/
    private Map<String, String> eventTypeColors = null;

    public void setEventTypeColors() {
        eventTypeColors = new HashMap<>();

        float[] colors = { HUE_BLUE, HUE_AZURE, HUE_CYAN, HUE_GREEN, HUE_MAGENTA, HUE_ORANGE, HUE_RED, HUE_ROSE, HUE_VIOLET, HUE_YELLOW };
        int colorPosition = 0;
        int sizeColorArray = colors.length - 1;

        for(String type : getEventTypes().keySet()){
            eventTypeColors.put(type, String.valueOf(colors[colorPosition]));
            colorPosition = colorPosition + 1 ;
            if(colorPosition > sizeColorArray){
                colorPosition = 0;
            }
        }
    }

    public Map<String, String> getEventTypeColors() {return eventTypeColors; }

    /** Get the events of a person's life, events are taken from the set currently used (defined by filters) */
    public List<String> getPersonEvents(String personId){
        //Set<String> currentEvents = getEvents().keySet();
        Set<String> currentEvents = getCurrentEvents();
        List<String> result = new ArrayList<>();
        for(String eventId : currentEvents){
            if(events.get(eventId).getPersonId().equals(personId)){
                result.add(eventId);
            }
        }
        // Sort List:
        int personEventsSize = result.size();
        for(int i = 0; i < personEventsSize; i++){
            for(int j = i; j < personEventsSize; j++){
                if(events.get(result.get(i)).getYear() > events.get(result.get(j)).getYear()){
                    String tmp = result.get(i);
                    result.set(i, result.get(j));
                    result.set(j, tmp);
                }
            }
        }

        return result;
    }

    /** Get the people related to the person. Store the person id and it's relationship to the person
     * format: <personId, relationship>
     */
    public Map<String, String> getPersonFamily(String personId){
        Map<String, String> familyMembers = new HashMap<>();

        // find child:
        String child = getPersonChild(personId, userPersonId, null);
        if(child != null) { familyMembers.put(child, "Child"); }

        Person person = people.get(personId);

        if(person.getFatherId() != null) { familyMembers.put(person.getFatherId(), "Father"); }
        if(person.getMotherId() != null) { familyMembers.put(person.getMotherId(), "Mother"); }
        if(person.getSpouseId() != null) { familyMembers.put(person.getSpouseId(), "Spouse"); }

        return familyMembers;
    }

    public String getPersonChild(String personId, String currentPersonId, String previousPersonId){
        if(currentPersonId == null){
            return null;
        }

        if(currentPersonId.equals(personId)){
            return previousPersonId;
        }

        String childId = null;

        // Search on mother side
        childId = getPersonChild(personId, people.get(currentPersonId).getMotherId(), currentPersonId);
        if(childId != null) { return childId; }

        // Search on father side
        return getPersonChild(personId, people.get(currentPersonId).getFatherId() , currentPersonId);

    }

    /** Store Filter preferences **/
    private Map<String, String> eventTypes = null; // Map<EventId, "t" or "f">

    public void updateEventType(String type, String value){
        eventTypes.put(type, value);
    }

    public Set<String> getCurrentEvents(){
        try {
            String fatherSide = eventTypes.get("Father's Side");
            String motherSide = eventTypes.get("Mother's Side");
            String maleOnly = eventTypes.get("Male");
            String femaleOnly = eventTypes.get("Female");


            if (maleOnly.equals("f") && femaleOnly.equals("f")) {
                return null;
            }
            else if (fatherSide.equals("f") && motherSide.equals("f")) {
                return getUserEvents();
            }
            else if (fatherSide.equals("t") && motherSide.equals("t") &&
                    maleOnly.equals("t") && femaleOnly.equals("t")) {
                return getEvents().keySet();
            }
            else if (fatherSide.equals("f") && motherSide.equals("t")) {
                if (maleOnly.equals("t") && femaleOnly.equals("t")) {
                    return getEventMotherSide();
                }
                else if (maleOnly.equals("t")) {
                    return getEventMaleMotherSide();
                }
                else {
                    return getEventFemaleMotherSide();
                }
            }
            else if (fatherSide.equals("t") && motherSide.equals("f")) {
                if (maleOnly.equals("t") && femaleOnly.equals("t")) {
                    return getEventFatherSide();
                }
                else if (maleOnly.equals("t")) {
                    return getEventMaleFatherSide();
                }
                else {
                    return getEventFemaleFatherSide();
                }
            }
            else if (fatherSide.equals("t") && motherSide.equals("t")) {
                if (maleOnly.equals("t") && femaleOnly.equals("f")) {
                    return getMaleEvents();
                }
                else if (femaleOnly.equals("t") && maleOnly.equals("f")) {
                    return getFemaleEvents();
                }
            }
            return null;
        }
        catch (NullPointerException e){
            Log.e(TAG, "Error: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /** SETTINGS - LINES**/
    // Numbers come from the string-array color in "strings.xml"
    private final int BLUE = 0, RED = 1, GREEN = 2;
    private int lifeStoryColor = BLUE;
    private int familyTreeColor = RED;
    private int spouseLineColor = GREEN;

    private boolean isLifeStoryLineOn = true;
    private boolean isFamilyTreeLineOn = true;
    private boolean isSpouseLineOn = true;


    /** Called by the color setters. Set colors to their id
     *
     * @param colorPos
     * @return
     */
    public int getLineColorId(int colorPos){
        switch(colorPos){
            case BLUE:
                return Color.BLUE;
            case RED:
                return Color.RED;
            case GREEN:
                return Color.GREEN;
            default:
                return 0;
        }
    }

    // Return the position of the color in the color array
    public int getLifeStoryColorPos() { return lifeStoryColor; }
    // return the id of the color (the actual color to show, not its name)
    public int getLifeStoryColorId() { return getLineColorId(getLifeStoryColorPos());}
    public void setLifeStoryColor(int lifeStoryColor) { this.lifeStoryColor = lifeStoryColor; }

    public int getFamilyTreeColorPos() { return familyTreeColor; }
    public int getFamilyTreeColorId() { return getLineColorId(getFamilyTreeColorPos()); }
    public void setFamilyTreeColor(int familyTreeColor) { this.familyTreeColor = familyTreeColor; }

    public int getSpouseLineColorPos() { return spouseLineColor; }
    public int getSpouseLineColorId(){ return getLineColorId(getSpouseLineColorPos()); }
    public void setSpouseLineColor(int spouseLineColor) { this.spouseLineColor = spouseLineColor; }

    public boolean isLifeStoryLineOn() { return isLifeStoryLineOn; }
    public void setLifeStoryLineOn(boolean lifeStoryLineOn) { isLifeStoryLineOn = lifeStoryLineOn; }

    public boolean isFamilyTreeLineOn() { return isFamilyTreeLineOn; }
    public void setFamilyTreeLineOn(boolean familyTreeLineOn) { isFamilyTreeLineOn = familyTreeLineOn; }

    public boolean isSpouseLineOn() { return isSpouseLineOn; }
    public void setSpouseLineOn(boolean spouseLineOn) { isSpouseLineOn = spouseLineOn; }

    /** SETTINGS - MAP TYPE **/
    private int[] mapTypes = {MAP_TYPE_NORMAL, MAP_TYPE_HYBRID, MAP_TYPE_SATELLITE, MAP_TYPE_TERRAIN};
    // the order comes from the items listed in "string.xml" map_type_array
    private int mapType = 0;

    public int getMapTypePos() { return mapType; }
    public int getMapTypeId(){ return mapTypes[getMapTypePos()]; }
    public void setMapTypePos(int position){ mapType = position; }

    /** SETTING - RE-SYNC & LOGOUT **/
    private boolean isUserLoggedIn = false;

    public boolean isUserLoggedIn() { return isUserLoggedIn; }
    public void setUserLoggedIn(boolean userLoggedIn) { isUserLoggedIn = userLoggedIn; }

    /** Clear family data and reset settings & filters **/
    public void resetModelToDefault(){
        // set the instance to a new one, the previous data is not accessible
        instance = new Model();
        setUserLoggedIn(false);
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

    public Map<String, String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(Map<String, String> eventTypes) {
        this.eventTypes = new HashMap<>(eventTypes);
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public void setPeople(Map<String, Person> people) {
        this.people = new HashMap<>(people);
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = new HashMap<>(events);
    }

    public Set<String> getMaleEvents() {
        return maleEvents;
    }

    public void setMaleEvents(Set<String> maleEvents) {this.maleEvents = new HashSet<>(maleEvents);}

    public Set<String> getFemaleEvents() {
        return femaleEvents;
    }

    public void setFemaleEvents(Set<String> femaleEvents) {
        this.femaleEvents = new HashSet<>(femaleEvents);
    }

    public Set<String> getEventMotherSide() {
        return eventMotherSide;
    }

    public void setEventMotherSide(Set<String> eventMotherSide) {
        this.eventMotherSide = new HashSet<>(eventMotherSide);
    }

    public Set<String> getEventFatherSide() {
        return eventFatherSide;
    }

    public void setEventFatherSide(Set<String> eventFatherSide) {
        this.eventFatherSide = new HashSet<>(eventFatherSide);
    }

    public Set<String> getEventMaleFatherSide() {
        return eventMaleFatherSide;
    }

    public void setEventMaleFatherSide(Set<String> eventMaleFatherSide) {
        this.eventMaleFatherSide = new HashSet<>(eventMaleFatherSide);
    }

    public Set<String> getEventFemaleFatherSide() {
        return eventFemaleFatherSide;
    }

    public void setEventFemaleFatherSide(Set<String> eventFemaleFatherSide) {
        this.eventFemaleFatherSide = new HashSet<>(eventFemaleFatherSide);
    }

    public Set<String> getEventMaleMotherSide() {
        return eventMaleMotherSide;
    }

    public void setEventMaleMotherSide(Set<String> eventMaleMotherSide) {
        this.eventMaleMotherSide = new HashSet<>(eventMaleMotherSide);
    }

    public Set<String> getEventFemaleMotherSide() {
        return eventFemaleMotherSide;
    }

    public void setEventFemaleMotherSide(Set<String> eventFemaleMotherSide) {
        this.eventFemaleMotherSide = new HashSet<>(eventFemaleMotherSide);
    }

    public Set<String> getUserEvents() {
        return userEvents;
    }

    public void setUserEvents(Set<String> userEvents) {this.userEvents = new HashSet<>(userEvents);
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

}

