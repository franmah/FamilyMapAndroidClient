package response;

import java.util.List;

import models.Person;

/**
 * Response used by PersonAllService class.
 * Contain an array of Person.
 * People can be added using an array and calling the constructor
 * or can be passed one by one using the addPerson method.
 */
public class PersonAllResponse extends Response{
    
    private List<Person> people = null;
    
    public PersonAllResponse(){}
    
    public PersonAllResponse(List<Person> people){
        this.people = people;
    }

    public List<Person> getPeople() {
        return people;
    }
}