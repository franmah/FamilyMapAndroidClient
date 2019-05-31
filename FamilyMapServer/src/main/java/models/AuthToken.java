package models;

import java.util.UUID;

/**
 * User tokens.
 */
public class AuthToken{
    
    private String token = null;
    private String userName = null;
    
    public AuthToken(){}
    
    /**
     * Create a Token object using a given String id.
     * @param   token: the String id of the token.
     */
    public AuthToken(String token, String user_name){
        this.token = token;
        this.userName = user_name;
    }
    
    /**
     * Generate a unique token for a given user
     */
    public AuthToken(String user_name){
        token = UUID.randomUUID().toString();
        this.userName = user_name;
    }
    
    public String getToken(){
        return token;
    }
    
    public String getUser(){
        return userName;
    }
    
    @Override
    public String toString(){
        return String.format(userName + " " + token);
    }
    
}