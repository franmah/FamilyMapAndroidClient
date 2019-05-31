package response;

import models.Person;

/**
 * Response used by PersonService class.
 * Contains the information of the requested person.
 */
public class PersonResponse extends Response{
    
    private String user_name = null;
    private String person_id = null;
    private String first_name = null;
    private String last_name = null;
    private String gender = null;
    private String father_id = null;
    private String mother_id = null;
    private String spouse_id = null;
    
    public PersonResponse(){}

    public PersonResponse(String user_name, String person_id, String first_name, String last_name,
                          String gender, String father_id, String mother_id, String spouse_id) {
        this.user_name = user_name;
        this.person_id = person_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.father_id = father_id;
        this.mother_id = mother_id;
        this.spouse_id = spouse_id;
    }
}