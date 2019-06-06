package com.client.request;

public class LoginTaskRequest {

    private String hostName = null;
    private String portNumber = null;
    private String userName = null;
    private String password = null;


    public LoginTaskRequest(String hostName, String portNumber, String userName, String password) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.userName = userName;
        this.password = password;
    }


    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
