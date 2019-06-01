package com.client.response;

/**
 * Response used by RegisterService and LoginService.
 * Indicates a user that he/she has been connected (and registered).
 */
public class ConnectionResponse{
    
    private String token = null;
    private String userName = null;
    private String personID = null;
    public String error_message = null;

    public ConnectionResponse(){}

    public ConnectionResponse(String token, String user_name, String person_id) {
        this.token = token;
        this.userName = user_name;
        this.personID = person_id;
    }

    public ConnectionResponse(String error_message){
        this.error_message = error_message;
    }

    public String getToken() {
        return token;
    }

    public String getUserName() {
        return userName;
    }

    public String getPersonID() {
        return personID;
    }

    public String getErrorMessage(){ return error_message;}

    @Override
    public String toString() {
        return "ConnectionResponse{" +
                "token='" + token + '\'' +
                ", userName='" + userName + '\'' +
                ", personID='" + personID + '\'' +
                ", error_message='" + error_message + '\'' +
                '}';
    }
}