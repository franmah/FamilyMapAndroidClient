package com.fmahieu.familymap.client.models;

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

    public String printName(){
        return getFirstName() + " " + getLastName();
    }
}