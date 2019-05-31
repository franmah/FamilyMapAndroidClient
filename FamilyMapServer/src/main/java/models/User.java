package models;

import java.util.Objects;

/**
 * Client user. 
 * Users use their user_name and password to connect to the server.
 */
public class User{
    
    public String userName = null;
    public String password = null;
    public String email = null;
    public String firstName = null;
    public String lastName = null;
    public String gender = null;
    public String personID = null;
    
    public User(){}

    public User(String user_name, String password, String email,
                String first_name, String last_name,
                String gender, String person_id){
    
        this.userName = user_name;
        this.password = password;
        this.email = email;
        this.firstName = first_name;
        this.lastName = last_name;
        this.gender = gender;
        this.personID = person_id;
    }


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getPersonId() {
        return personID;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getEmail(), user.getEmail()) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(getGender(), user.getGender()) &&
                Objects.equals(personID, user.personID);
    }


    @Override
    public String toString(){
        return String.format(userName + " " + personID + " " + password + " " + email + " " + firstName + " " + lastName + " " + gender);
    }
}