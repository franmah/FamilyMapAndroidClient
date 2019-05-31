package services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import models.*;

import dao.OperationDAO;
import response.ErrorResponse;
import response.Response;
import response.SuccessResponse;

import static org.junit.Assert.*;

public class FillServiceTest {

    @Before
    public void setUp(){
        OperationDAO db = new OperationDAO();
        db.getUser_dao().deleteUsers();
        db.getEvent_dao().deleteEvents();
        db.getPerson_dao().deletePersons();
        db.getAutToken_dao().deleteTokens();
        db.commitAndCloseConnection(true);
    }
    @After
    public void tearDown(){
        OperationDAO db = new OperationDAO();
        db.getEvent_dao().deleteEvents();
        db.getPerson_dao().deletePersons();
        db.getUser_dao().deleteUsers();
        db.commitAndCloseConnection(true);
    }

    @Test
    public void fillTreeCompleteTest(){
        boolean success = true;
        final String ANSWER = "Successfully generated 31 people and 91 event(s)";
        try {
            OperationDAO db = new OperationDAO();
            db.getEvent_dao().deleteEvents();
            db.getPerson_dao().deletePersons();
            db.getUser_dao().deleteUsers();

            User user = new User("test_user", "password", "email", "first", "last", "f", "test_person");
            Person person = new Person("test_person", "test_user", "first", "last", "f");

            db.getUser_dao().addUser(user);
            db.getPerson_dao().addPerson(person);
            db.commitAndCloseConnection(true);

            FillService service = new FillService();
            Response response = service.fillUserTree("test_user", 4);

            if(response instanceof SuccessResponse){
                success = true;
                assertEquals(ANSWER, response.toString());
            }

            if(response instanceof ErrorResponse){
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
    public void fillTreeWrongUser(){
        boolean success = true;
        final String ANSWER = "Successfully generated 31 people and 91 event(s)";
        try {
            OperationDAO db = new OperationDAO();
            db.getEvent_dao().deleteEvents();
            db.getPerson_dao().deletePersons();
            db.getUser_dao().deleteUsers();

            User user = new User("test_user", "password", "email", "first", "last", "f", "test_person");
            Person person = new Person("test_person", "test_user", "first", "last", "f");

            db.getUser_dao().addUser(user);
            db.getPerson_dao().addPerson(person);
            db.commitAndCloseConnection(true);

            FillService service = new FillService();
            Response response = service.fillUserTree("wrong_user", 4);

            if(response instanceof SuccessResponse){
                success = false;
                assertEquals(ANSWER, response.toString());
            }

            if(response instanceof ErrorResponse){
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
