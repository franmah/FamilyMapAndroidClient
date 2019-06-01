package com.client.httpClient;

import com.client.models.Event;
import com.client.models.Person;
import com.client.request.LoginRequest;
import com.client.request.RegisterRequest;
import com.client.response.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;


public class serverProxyTest {

    private final String HOST_NAME = "localhost";
    private final String PORT_NUMBER = "8080";

    @Before
    public void setUp(){
        ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);
        proxy.clear();
    }

    @After
    public void tearDown(){
        setUp();
    }

    @Test
    public void registerPass(){

        String token = null;

        // Create request
        RegisterRequest request = new RegisterRequest("username", "password", "email", "first", "last", "m");

        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);
            proxy.clear();

            ConnectionResponse response = proxy.register(request);

            if(response.getErrorMessage() != null){
                System.out.println();
                assertTrue(false);
            }
            else{
                token = response.getToken();
            }

        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            assertTrue(false);
        }


        assertNotNull(token);
    }

    @Test
    public void registerAlreadyRegistered(){
        // Make sure database is cleared before testing

        String token = null;

        // Create request
        RegisterRequest request = new RegisterRequest("username", "password", "email", "first", "last", "m");

        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);
            proxy.register(request);
            ConnectionResponse response = proxy.register(request); // User should already be registered

            if(response.getErrorMessage() != null){
                System.out.println();
                assertTrue(true);
            }
            else{
                token = response.getToken();
            }

        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            assertTrue(false);
        }

        assertNull(token);
    }

    @Test
    public void loginPass(){
        // Make sure database is cleared before testing

        String token = null;

        // Create requests
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email", "first", "last", "m");
        LoginRequest loginRequest = new LoginRequest("username", "password");

        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);
            proxy.register(registerRequest);
            ConnectionResponse response = proxy.login(loginRequest); // User should already be registered

            if(response.getErrorMessage() != null){
                System.out.println();
                assertTrue(true);
            }
            else{
                token = response.getToken();
            }

        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            assertTrue(false);
        }


        assertNotNull(token);
    }

    @Test
    public void loginFail(){
        // Test for wrong password and wrong userName
        // Make sure database is cleared before testing

        boolean success = false;

        // Create requests
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email", "first", "last", "m");
        LoginRequest wrongPasswordRequest = new LoginRequest("username", "wrong");
        LoginRequest wrongUserRequest = new LoginRequest("wrong", "password");

        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);
            proxy.register(registerRequest);

            ConnectionResponse response = proxy.login(wrongPasswordRequest); // User should already be registered
            if(response.getErrorMessage() != null){
                assertTrue(true);
            }

            response = proxy.login(wrongUserRequest);
            if(response.getErrorMessage() != null){
                success = true;
            }

        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }


        assertTrue(success);
    }

    @Test
    public void getEventsPass(){
        final int NUM_EVENTS = 91;

        String token = null;

        // Create requests
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email", "first", "last", "m");

        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);
            proxy.clear();

            ConnectionResponse response = proxy.register(registerRequest);


            if(response.getErrorMessage() != null){
                System.out.println();
                assertTrue(false);
            }
            else{
                token = response.getToken();

                EventAllResponse event_response = proxy.getEvents(token);
                List<Event> events = event_response.getEvents();
                for(Event event: events){
                    System.out.println(event.toString());
                }
                assertEquals(events.size(), NUM_EVENTS);
            }

        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            assertTrue(false);
        }

    }

    @Test
    public void getEventsWrongToken() {
        String token = null;

        boolean success = false;

        // Create requests
        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);
            proxy.clear();

            EventAllResponse event_response = proxy.getEvents("wrong token");
            if(event_response.getErrorMessage() != null) {
                success = true;
            }


        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        assertTrue(success);

    }

    @Test
    public void getPeoplePass(){
        final int NUM_PEOPLE = 31;

        String token = null;

        // Create requests
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email", "first", "last", "m");

        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);

            ConnectionResponse response = proxy.register(registerRequest);


            if(response.getErrorMessage() != null){
                System.out.println();
                assertTrue(false);
            }
            else{
                token = response.getToken();

                PersonAllResponse person_response = proxy.getPeople(token);
                if(person_response.getErrorMessage() == null) {
                    List<Person> people = person_response.getPeople();

                    assertEquals(people.size(), NUM_PEOPLE);
                }
                else {
                    assertTrue(false);
                }
            }

        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            assertTrue(false);
        }

    }

    @Test
    public void getPeopleWrongToken(){

        try{
            ServerProxy proxy = new ServerProxy(HOST_NAME, PORT_NUMBER);
            proxy.clear();

            PersonAllResponse person_response = proxy.getPeople("wrong_token");

            if(person_response.getErrorMessage() == null){
                assertTrue(true);
            }


        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            assertTrue(false);
        }

    }
}
