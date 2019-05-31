package services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import java.time.LocalTime;
import java.util.List;
import dao.*;
import models.*;
import response.EventAllResponse;
import response.EventResponse;
import response.ErrorResponse;
import response.Response;


public class EventServiceTest {

    @Before
    public void setUp(){
        OperationDAO db = new OperationDAO();
        db.getAutToken_dao().deleteTokens();
        db.getEvent_dao().deleteEvents();
        db.getUser_dao().deleteUsers();

        db.getAutToken_dao().addToken(new AuthToken("test_token", "test_user"));
        db.getEvent_dao().addEvent(new Event("test_event", "test_user", "test_Event",
                (float) 10.0, (float) 10.0, "country", "city", "type", 1993 ));
        db.commitAndCloseConnection(true);
    }

    @After
    public void tearDown(){
        OperationDAO db = new OperationDAO();
        db.getAutToken_dao().deleteTokens();
        db.getEvent_dao().deleteEvents();
        db.getUser_dao().deleteUsers();
        db.commitAndCloseConnection(true);
    }

    @Test
    public void getEventPass(){

        boolean success = true;

        try{
            EventService event_service = new EventService();

            Response response = event_service.getEvent("test_token", "test_event");

            if(response instanceof EventResponse){
                success = true;
            }
            if(response instanceof ErrorResponse){
                success = false;
            }
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void getEventWrongID(){
        boolean success = false;

        try{
            EventService event_service = new EventService();

            Response response = event_service.getEvent("test_token", "wrong_event");

            if(response instanceof EventResponse){
                success = false;
            }
            if(response instanceof ErrorResponse){
                success = true;
            }
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void getEventWrongToken(){
        boolean success = false;

        try{
            EventService event_service = new EventService();

            Response response = event_service.getEvent("wrong_token", "test_event");

            if(response instanceof EventResponse){
                success = false;
            }
            if(response instanceof ErrorResponse){
                success = true;
            }
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void getEventAllPass(){
        boolean success = true;
        final int NUM_ROWS = 10;
        OperationDAO db = null;

        try{
            // Fill database with a list of people.
            db = new OperationDAO();
            EventDAO pdao = db.getEvent_dao();

            for(int i = 0; i < NUM_ROWS; i++) {
                pdao.addEvent(new Event("test_event" + i, "test_user", "test_Event",
                        (float) 10.0, (float) 10.0, "country", "city", "type", 1993 ));
            }
            // Number of Event will be NUM_ROWS + 1 because a Event is added in @Before.

            db.commitAndCloseConnection(true);

            // Test EventService
            EventService Event_service = new EventService();

            Response response = Event_service.getEventAll("test_token");

            if(response instanceof EventAllResponse){
                List<Event> events = ((EventAllResponse) response).getEvents();
                System.out.println(LocalTime.now() + " EventServiceTest.getEventAllPass: right number of people returned");
                if(events.size() != NUM_ROWS + 1){
                    success = false;
                }
            }
            else{
                success = false;
            }
        }catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void getEventAllWrongUser(){
        boolean success = true;
        final int NUM_ROWS = 10;
        OperationDAO db = null;

        try{
            // Fill database with a list of people.
            db = new OperationDAO();
            EventDAO pdao = db.getEvent_dao();

            for(int i = 0; i < NUM_ROWS; i++) {
                pdao.addEvent(new Event("test_event" + i, "test_user", "test_Event",
                        (float) 10.0, (float) 10.0, "country", "city", "type", 1993 ));
            }


            db.commitAndCloseConnection(true);

            // Test EventService
            EventService Event_service = new EventService();

            Response response = Event_service.getEventAll("wrong_token");

            if(response instanceof EventAllResponse){
                List<Event> events = ((EventAllResponse) response).getEvents();
                System.out.println(LocalTime.now() + " EventServiceTest.getEventAllPass: right number of people returned");
                if(events.size() != 1){
                    success = false;
                }
            }
            else{
                success = true;
            }
        }catch (Exception e){
            success = false;
        }

        assertTrue(success);

    }



}
