package com.fmahieu.familymap.client.request;

public class RetrieveDataRequest {
    private String hostName;
    private String portNumber;

    public RetrieveDataRequest(String hostName, String portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
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
}
