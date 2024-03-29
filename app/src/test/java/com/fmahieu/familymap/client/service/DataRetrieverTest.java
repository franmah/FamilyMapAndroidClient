package com.fmahieu.familymap.client.service;

import com.fmahieu.familymap.client.httpClient.ServerProxy;
import com.fmahieu.familymap.client.models.Model;
import com.fmahieu.familymap.client.request.RegisterRequest;
import com.fmahieu.familymap.client.response.ConnectionResponse;

import org.junit.Test;
import static org.junit.Assert.*;

public class DataRetrieverTest {
    private final String HOST_NAME = "localhost";
    private final String PORT_NUMBER = "8080";

    @Test
    public void pullDataPass(){
        boolean success = false;
        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);

            proxy.clear();
            RegisterRequest request = new RegisterRequest("user", "pass", "email", "first", "last", "m");
            ConnectionResponse response = proxy.register(request);
            if(response.getErrorMessage() == null){
                proxy = null;

                Model model = Model.getInstance();
                model.setUserPersonId(response.getPersonID());
                model.setUserToken(response.getToken());

                DataRetriever retriever = new DataRetriever();
                String message = retriever.pullData(HOST_NAME, PORT_NUMBER);
                if(message == null){
                    success = true;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        assertTrue(success );
    }
}
