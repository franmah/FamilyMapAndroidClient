package com.client.httpClient;

import com.client.request.LoginRequest;
import com.client.request.RegisterRequest;
import com.client.response.ConnectionResponse;
import com.client.response.EventAllResponse;
import com.client.response.PersonAllResponse;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;

public class ServerProxy {

    private String hostName = null;
    private String portNumber = null;

    public ServerProxy(String hostName, String portNumber){
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public ConnectionResponse register(RegisterRequest request){
        assert hostName != null;
        assert portNumber != null;

        System.out.println("ServerProxy.register(): sending request to server...");

        try{
            URL url = new URL("http://" + hostName + ":" + portNumber + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            // Send request in Json format
            Gson gson = new Gson();
            String json_request = gson.toJson(request);

            OutputStream reqBody = http.getOutputStream();
            writeToStream(json_request, reqBody);
            reqBody.close();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                System.out.println("ServerProxy.register(): response came back OK (200) ");
                InputStream respBody = http.getInputStream();
                String response_json = readFromStream(respBody);

                return gson.fromJson(response_json, ConnectionResponse.class);
            }
            else {
                System.out.println("ServerProxy.register(): Error during registration: " +
                                        http.getResponseMessage());
                return new ConnectionResponse("Internal Error: unable to connect to server");
            }
        }
        catch (Exception e){
            System.out.println("ServerProxy.register(): something went wrong while registering user: " + e.toString());
            e.printStackTrace();
            return new ConnectionResponse("Internal Error: unable to connect to server");
        }
    }

    public ConnectionResponse login(LoginRequest request){
        assert hostName != null;
        assert portNumber != null;

        System.out.println("ServerProxy.login(): sending request to server...");

        try{
            URL url = new URL("http://" + hostName + ":" + portNumber + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            // Send request in Json format
            Gson gson = new Gson();
            String json_request = gson.toJson(request);

            OutputStream reqBody = http.getOutputStream();
            writeToStream(json_request, reqBody);
            reqBody.close();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                System.out.println("ServerProxy.login(): response came back OK (200) ");
                InputStream respBody = http.getInputStream();
                String response_json = readFromStream(respBody);

                return gson.fromJson(response_json, ConnectionResponse.class);
            }
            else {
                System.out.println("ServerProxy.login(): Error during registration: " +
                        http.getResponseMessage());
                return new ConnectionResponse("Internal Error: unable to connect to server");
            }
        }
        catch (Exception e){
            System.out.println("ServerProxy.login(): something went wrong while registering user: " + e.toString());
            e.printStackTrace();
            return new ConnectionResponse("Internal Error: unable to connect to server");
        }
    }

    public EventAllResponse getEvents(String token){
        assert portNumber != null;
        assert hostName != null;
        assert token != null;

        System.out.println("ServerProxy.getEvents(): sending request to server...");

        try{
            URL url = new URL("http://" + hostName + ":" + portNumber + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);

            // Add token to the request
            http.addRequestProperty("Authorization", token);

            http.connect();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                System.out.println("ServerProxy.getEvents(): response came back OK (200) ");
                InputStream respBody = http.getInputStream();
                String response_json = readFromStream(respBody);

                Gson gson = new Gson();
                return gson.fromJson(response_json, EventAllResponse.class);
            }
            else {
                System.out.println("ServerProxy.getEvents(): Error while trying to retrieve events " +
                        http.getResponseMessage());
                return new EventAllResponse("Internal Error: unable to connect to server");
            }
        }
        catch (Exception e){
            System.out.println("ServerProxy.getEvents(): something went wrong while retrieving events: " + e.toString());
            e.printStackTrace();
            return new EventAllResponse("Internal Error: unable to connect to server");
        }
    }

    public PersonAllResponse getPeople(String token){
        assert portNumber != null;
        assert hostName != null;
        assert token != null;

        System.out.println("ServerProxy.getPeople(): sending request to server...");

        try{
            URL url = new URL("http://" + hostName + ":" + portNumber + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);

            // Add token to the request
            http.addRequestProperty("Authorization", token);

            http.connect();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                System.out.println("ServerProxy.getPeople(): response came back OK (200) ");
                InputStream respBody = http.getInputStream();
                String response_json = readFromStream(respBody);

                Gson gson = new Gson();
                return gson.fromJson(response_json, PersonAllResponse.class);
            }
            else {
                System.out.println("ServerProxy.getPeople(): Error while trying to retrieve people " +
                        http.getResponseMessage());
                return new PersonAllResponse("Internal Error: unable to connect to server");
            }
        }
        catch (Exception e){
            System.out.println("ServerProxy.getPeople(): something went wrong while retrieving people: " + e.toString());
            e.printStackTrace();
            return new PersonAllResponse("Internal Error: unable to connect to server");
        }
    }

    /** Only used by tests
     *
     */
    public void clear(){

        System.out.println("ServerProxy.login(): sending request to server...");

        try{
            URL url = new URL("http://" + hostName + ":" + portNumber + "/clear");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            OutputStream reqBody = http.getOutputStream();
            reqBody.close();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                System.out.println("ServerProxy.clear(): response came back OK (200), database has been cleared");
            }
            else {
                System.out.println("ServerProxy.login(): Error during registration: " +
                        http.getResponseMessage());
            }
        }
        catch (Exception e){
            System.out.println("ServerProxy.login(): something went wrong while registering user: " + e.toString());
            e.printStackTrace();
        }
    }


    private static String readFromStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private void writeToStream(String json_request, OutputStream os) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writer.write(json_request);
        writer.flush();
    }
}
