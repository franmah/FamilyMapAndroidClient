package com.client.request;


/**
 * Request used by LoginService class.
 */
public class LoginRequest{
    
    private String userName = null;
    private String password = null;
    
    public LoginRequest(){}
    
    public LoginRequest(String user_name, String password){
        this.userName = user_name;
        this.password = password;
    }
    
    public String getUserName(){return userName;}
    public String getPassword(){return password;}
    
    public void setUserName(String user_name){
        this.userName = user_name;
    }
    public void setPassword(String password){
        this.password = password;
    }
}