package request;

import java.util.ArrayList;

import models.*;

/**
 * Contains arrays of User, Person and Event.
 * Arrays can be passed using constructor or can be added one by one using add methods.
 */
public class LoadRequest{
    
    private ArrayList<User> users = null;
    private ArrayList<Person> persons = null;
    private ArrayList<Event> events = null;
    
    public LoadRequest(){}
    
    public LoadRequest(ArrayList<User> users, ArrayList<Person> persons, ArrayList<Event> events){
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Person> getpersons() {
        return persons;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void setpersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}