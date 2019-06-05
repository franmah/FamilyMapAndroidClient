package com.client.models;

import java.util.Objects;
import java.util.UUID;

/**
 * Person object.
 * Each object is linked to a user.
 */
public class Person{

    private String personID = null;
    private String associatedUsername = null;
    private String firstName = null;
    private String lastName = null;
    private String gender = null;
    private String fatherID = null;
    private String motherID = null;
    private String spouseID = null;


    public Person(){}

    public Person(String user_name, String first_name, String last_name,
                  String gender){
        this.personID = UUID.randomUUID().toString();
        this.associatedUsername = user_name;
        this.firstName = first_name;
        this.lastName = last_name;
        this.gender = gender;
    }

    public Person(String person_id, String user_name, String first_name, String last_name,
                  String gender){
        this.personID = person_id;
        this.associatedUsername = user_name;
        this.firstName = first_name;
        this.lastName = last_name;
        this.gender = gender;
    }

    public void setFatherId(String father_id){
        this.fatherID = father_id;
    }
    public void setMotherId(String mother_id){
        this.motherID = mother_id;
    }
    public void setSpouseId(String spouse_id){
        this.spouseID = spouse_id;
    }

    public String getPersonId(){return personID;}
    public String getAssociatedUsername(){return associatedUsername;}
    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public String getGender(){return gender;}
    public String getFatherId(){return fatherID;}
    public String getMotherId(){return motherID;}
    public String getSpouseId(){return spouseID;}


    @Override
    public String toString(){
        StringBuilder final_string = new StringBuilder(personID + " " + associatedUsername + " " + firstName + " " + lastName + " " + gender);

        if(fatherID != null) { final_string.append(" " + fatherID); }
        if(motherID != null) { final_string.append(" " + motherID); }
        if(spouseID != null) { final_string.append(" " + spouseID); }

        return final_string.toString();
    }


    @Override
    public int hashCode() {
        return  personID.length() & getAssociatedUsername().length() ^ getFirstName().length() &
                getLastName().length() ^ getGender().length();
    }
}