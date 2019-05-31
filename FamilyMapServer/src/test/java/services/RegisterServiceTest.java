package services;

import org.junit.*;
import org.junit.runner.Request;

import static org.junit.Assert.*;
import java.time.LocalTime;

import dao.OperationDAO;
import request.RegisterRequest;
import response.ErrorResponse;
import response.Response;
import response.ConnectionResponse;

public class RegisterServiceTest {

    OperationDAO db = null;

    @Before
    public void setUp(){
        OperationDAO db = new OperationDAO();
        db.getPerson_dao().deletePersons();
        db.getUser_dao().deleteUsers();
        db.getEvent_dao().deleteEvents();
        db.getAutToken_dao().deleteTokens();
        db.commitAndCloseConnection(true);
        db = null;}


    @After
    public void tearDown(){
        setUp();
    }

    @Test
    public void RegisterTestPass(){
        RegisterRequest req = new RegisterRequest("test_user", "password", "email",
                "first", "last", "m");

        boolean success = false;

        try{
            RegisterService register_service = new RegisterService();

            Response response = register_service.registerNewUser(req);

            if(response instanceof ConnectionResponse){
                success = true;
            }

            if(response instanceof  ErrorResponse){
                success = false;
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void RegisterTestFail(){
        // User is already registered.

        RegisterRequest req = new RegisterRequest("test_user", "password", "email",
                "first", "last", "m");

        boolean success = false;

        try{
            RegisterService register_service = new RegisterService();

            register_service.registerNewUser(req);
            Response response = register_service.registerNewUser(req);

            if(response instanceof ConnectionResponse){
                success = false;
            }

            if(response instanceof  ErrorResponse){
                success = true;
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            success = true;
        }

        assertTrue(success);
    }
}
