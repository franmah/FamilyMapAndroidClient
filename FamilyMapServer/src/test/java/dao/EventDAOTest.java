package dao;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;

import models.Event;


public class EventDAOTest {
    private OperationDAO db = null;

    @Before
    public void setUp(){
        db = new OperationDAO();
    }

    @After
    public void tearDown(){
        db.commitAndCloseConnection(false);
        db = null;
    }

    @Test
    public void deletePass(){
        EventDAO event_dao = db.getEvent_dao();
        boolean success = false;

        try{
            success = event_dao.deleteEvents();
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void deleteMultiplePass(){
        EventDAO event_dao = db.getEvent_dao();
        boolean success = false;

        try{
            final int NUM_ROWS = 10;

            for(int i = 0; i < NUM_ROWS; i++){
                event_dao.addEvent(new Event("test" + i, "test_name", "test_person", 12.5f,
                        12.5f, "USA", "provo", "birth", 2019));
            }

            success = event_dao.deleteEvents();
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void addPass(){
        EventDAO event_dao = db.getEvent_dao();
        Event event = new Event("test", "test", "test", 12.5f,
                                12.5f, "USA", "provo", "birth", 2019);

        boolean success = false;

        try{
            success = event_dao.addEvent(event);
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void addFail(){

        EventDAO event_dao = db.getEvent_dao();
        Event event = new Event("test", "test", "test", 12.5f,
                12.5f, "USA", "provo", "birth", 2019);

        boolean success = true;

        try{
            event_dao.addEvent(event);
            success = event_dao.addEvent(event); // Event should already be in table.
        }
        catch (Exception e){
            success = false;
        }

        assertFalse(success);
    }

    @Test
    public void getEventPass(){
        EventDAO event_dao = db.getEvent_dao();
        Event event = new Event("test", "test", "test", 12.5f,
                12.5f, "USA", "provo", "birth", 2019);

        Event tmp_event = null;

        try{
            event_dao.addEvent(event);
            tmp_event = event_dao.getEvent(event.getEventId(), event.getAssociatedUsername());
        }
        catch (Exception e){
            assertTrue(false);
        }

        assertEquals(tmp_event, event);
    }


    @Test
    public void getEventNotFound(){
        EventDAO event_dao = db.getEvent_dao();
        boolean success = true;

        try{
            // Table events should be empty.
            Event event = event_dao.getEvent("test", "test");
            if(event == null){
                success = false;
            }
        }
        catch (Exception e){
            success = false;
        }

        assertFalse(success);
    }

    @Test
    public void getEventWrongUserName(){

    }
    @Test
    public void getEventAllPass(){
        EventDAO event_dao = db.getEvent_dao();

        final int NUM_ROWS = 10;

        for(int i = 0; i < NUM_ROWS; i++){
            event_dao.addEvent(new Event("test" + i, "test_name", "test_person", 12.5f,
                    12.5f, "USA", "provo", "birth", 2019));
        }

        boolean success = false;

        try{
            List<Event> events = event_dao.getEventAll("test_name");
            System.out.println(events.size());
            if(events.size() == NUM_ROWS){
                success = true;
            }
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);

    }

    @Test
    public void getEventAllEmptyDBPass(){
        boolean success = false;

        try{
            EventDAO event_dao = db.getEvent_dao();
            event_dao.deleteEvents();

            List<Event> events = event_dao.getEventAll("test_name");
            System.out.println(events.size());
            success = true;
        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        assertTrue(success);
    }

    @Test
    public void getBirthEventPass(){

        EventDAO event_dao = db.getEvent_dao();
        Event event = new Event("test", "test", "test", 12.5f,
                12.5f, "USA", "provo", "birth", 2019);

        Event tmp_event = null;

        try{
            event_dao.deleteEvents();

            event_dao.addEvent(event);
            tmp_event = event_dao.getBirthEvent(event.getPersonId(), event.getAssociatedUsername());
        }
        catch (Exception e){
            assertTrue(false);
        }

        assertEquals(tmp_event, event);
    }

    @Test
    public void getBirthEventFail(){

        EventDAO event_dao = db.getEvent_dao();
        Event event = new Event("test", "test", "test", 12.5f,
                12.5f, "USA", "provo", "birth", 2019);

        Event tmp_event = null;

        try{
            event_dao.deleteEvents();

            event_dao.addEvent(event);
            tmp_event = event_dao.getBirthEvent("wrong_person", event.getAssociatedUsername());
        }
        catch (Exception e){
            assertTrue(false);
        }

        boolean success = false;

        if(tmp_event == null){
            success = true;
        }

        assertTrue(success);
    }

    @Test
    public void deleteUserFamilyEventsPass(){
        EventDAO event_dao = db.getEvent_dao();
        boolean success = false;

        try{
            event_dao.deleteUserFamilyEvents("user");
            success = true;
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);

    }

    @Test
    public void deleteUserFamilyMultipleEventsPass(){
        EventDAO event_dao = db.getEvent_dao();
        boolean success = false;

        final int NUM_ROWS = 10;

        try{

            for(int i = 0; i < NUM_ROWS; i++){
                event_dao.addEvent(new Event("test" + i, "test_name", "test_person", 12.5f,
                        12.5f, "USA", "provo", "birth", 2019));
            }

            event_dao.deleteUserFamilyEvents("test_name");
            success = true;
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

}
