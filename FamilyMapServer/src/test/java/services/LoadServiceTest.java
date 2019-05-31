package services;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

import dao.*;
import models.*;
import request.LoadRequest;
import response.ErrorResponse;
import response.Response;
import response.SuccessResponse;

public class LoadServiceTest {
    @Before
    public void setUp(){
        OperationDAO db = new OperationDAO();
        db.getPerson_dao().deletePersons();
        db.getEvent_dao().deleteEvents();
        db.getUser_dao().deleteUsers();
        db.commitAndCloseConnection(true);
    }

    @Test
    public void loadServicePass(){
        final int NUM_ROWS = 10;
        boolean success = false;

        try{
            ArrayList<Event> events = new ArrayList<>();
            ArrayList<Person> persons = new ArrayList<>();
            ArrayList<User> users = new ArrayList<>();

            // Fill Events - Persons - Users
            for(int i = 0; i < NUM_ROWS; i++) {
                events.add(new Event("test_event" + i, "test_user", "test_Event",
                        (float) 10.0, (float) 10.0, "country", "city", "type", 1993 ));
            }
            for(int i = 0; i < NUM_ROWS; i++) {
                persons.add(new Person(String.format("test" + i), "test_user", "this", "test", "m"));
            }
            for(int i = 0; i < NUM_ROWS; i++) {
                users.add(new User("test" + i, "password", "test@test.com", "this",
                        "test", "f", "test_id"));
            }

            // Create request
            LoadRequest request = new LoadRequest(users, persons, events);

            // Call to LoadService
            LoadService load_service = new LoadService();
            Response response = load_service.loadDataBase(request);

            if(response instanceof SuccessResponse){
                success = true;
            }

            final String expected_response = String.format("Successfully added %d user(s), %d person(s), and %d event(s) to the database.",
                                                NUM_ROWS, NUM_ROWS, NUM_ROWS);

            assertEquals(expected_response, response.toString());
        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        assertTrue(success);
    }

    @Test
    public void LoadServiceEmpty(){

        final int NUM_ROWS = 0;
        boolean success = false;

        try{
            ArrayList<Event> events = null;
            ArrayList<Person> persons = null;
            ArrayList<User> users = null;

            // Create request
            LoadRequest request = new LoadRequest(users, persons, events);

            // Call to LoadService
            LoadService load_service = new LoadService();
            Response response = load_service.loadDataBase(request);

            if(response instanceof ErrorResponse){
                success = true;
            }


        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        assertTrue(success);
    }
}
