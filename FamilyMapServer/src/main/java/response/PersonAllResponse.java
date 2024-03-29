package response;

import java.util.List;

import models.Person;

/**
 * Response used by PersonAllService class.
 * Contain an array of Person.
 * People can be added using an array and calling the constructor
 * or can be passed one by one using the addPerson method.
 */
public class PersonAllResponse implements Response{
    
    private List<Person> people = null;
    private String error_message = null;
    
    public PersonAllResponse(){}

    public PersonAllResponse(String error_message){
        this.error_message = error_message;
    }
    
    public PersonAllResponse(List<Person> people){
        this.people = people;
    }

    public List<Person> getPeople() {
        return people;
    }

    public String getErrorMessage(){ return error_message; }
}