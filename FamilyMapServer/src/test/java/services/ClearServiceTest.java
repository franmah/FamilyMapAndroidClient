package services;

import org.junit.*;
import static org.junit.Assert.*;
import java.time.LocalTime;

import dao.OperationDAO;
import models.Event;
import models.Person;
import models.User;
import response.ErrorResponse;
import response.Response;
import response.SuccessResponse;



public class ClearServiceTest {

    @Test
    public void clearPass(){
        ClearService clear_service = new ClearService();

        boolean success = false;

        Response response = clear_service.clearDataBase();

        if(response instanceof ErrorResponse){
            System.out.println(LocalTime.now() + " ClearService.clearPass(): response came back as an ErrorResponse");
            success = false;
        }
        if(response instanceof SuccessResponse){
            System.out.println(LocalTime.now() + " ClearService.clearPass(): response came back as a SuccessResponse");
            success = true;
        }

        assertTrue(success);
    }

    @Test
    public void clearNonEmptyPass(){

        final int NUM_ROWS = 10;
        OperationDAO db = new OperationDAO();
        // Fill database:
        for(int i = 0; i < NUM_ROWS; i++) {
            db.getUser_dao().addUser(new User("test" + i, "password", "test@test.com", "this",
                    "test", "f", "test_id"));
        }

        for(int i = 0; i < NUM_ROWS; i++) {
            db.getPerson_dao().addPerson(new Person(String.format("test" + i), "test", "this", "test", "m"));
        }

        for(int i = 0; i < NUM_ROWS; i++){
            db.getEvent_dao().addEvent(new Event("test" + i, "test_name", "test_person", 12.5f,
                    12.5f, "USA", "provo", "birth", 2019));
        }

        db.commitAndCloseConnection(true);

        ClearService clear_service = new ClearService();

        boolean success = false;

        Response response = clear_service.clearDataBase();

        if(response instanceof ErrorResponse){
            System.out.println(LocalTime.now() + " ClearService.clearPass(): response came back as an ErrorResponse");
            success = false;
        }
        if(response instanceof SuccessResponse){
            System.out.println(LocalTime.now() + " ClearService.clearPass(): response came back as a SuccessResponse");
            success = true;
        }

        assertTrue(success);
    }
}
