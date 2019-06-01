package com.client.service;

import com.client.httpClient.ServerProxy;
import com.client.models.*;
import com.client.response.EventAllResponse;
import com.client.response.PersonAllResponse;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataRetriever {

    private ServerProxy proxy = null;
    private Model model = null;

    /** Use the proxy client to get the data from the server
     *
     * @param hostName
     * @param portNumber
     * @param token
     * @param userPersonId
     * @return null if no error, else: return the error message received from the server
     */
    public String pullData(String hostName, String portNumber, String token, String userPersonId){

        try{
            proxy = new ServerProxy(hostName, portNumber);
            model = Model.getInstance();

            // Retrieve events
            String message = getEvents(token);
            // check if there was an error:
            if(message != null){
                return message;
            }

            // Retrieve People
            message = getPeople(token, userPersonId);
            if(message != null){
                return message;
            }

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
            return response.getErrorMessage();
        }

        Map<String, Event> result = new TreeMap<>();
        Set<String> eventTypes = new TreeSet<>();

        for(Event event : response.getEvents()){
            result.put(event.getEventId(), event);
            eventTypes.add(event.getEventType());
        }

        model.setEvents(result);
        model.setEventTypes(eventTypes);
        return null;
    }

    private String getPeople(String token, String userPersonId) throws Exception{
        PersonAllResponse response = proxy.getPeople(token);

        if(response.getErrorMessage() != null){
            return response.getErrorMessage();
        }

        Map<String, Person> result = new TreeMap<>();
        Set<Person> maleAncestors = new TreeSet<>();
        Set<Person> femaleAncestors = new TreeSet<>();

        for(Person person : response.getPeople()){
            result.put(person.getPersonId(), person);

            if(person.getGender().equals("f")){
                femaleAncestors.add(person);
            }
            else{
                maleAncestors.add(person);
            }

            if(person.getPersonId().equals(userPersonId)){
                model.setUserPerson(person);
            }
        }

        model.setPeople(result);
        model.setFemaleAncestors(femaleAncestors);
        model.setMaleAncestors(maleAncestors);
        return null;
    }

}
