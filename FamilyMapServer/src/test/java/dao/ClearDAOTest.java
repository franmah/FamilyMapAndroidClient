package dao;

import org.junit.*;
import java.time.LocalTime;

import models.Event;
import models.Person;
import models.User;

import static org.junit.Assert.*;

public class ClearDAOTest {

    @Before
    public void setUp(){}

    @After
    public void tearDown(){
    }

    @Test
    public void clearDBPass(){
        ClearDAO clear_dao = new ClearDAO();
        boolean success = false;

        try{
            success = clear_dao.clearDataBase();
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " ClearDAOTest: Exception caught: test fails");
            success = false;
        }

        assertTrue(success);

    }

    @Test
    public void clearDBEntryPass(){

        boolean success = false;

        try{
            // Add users
            OperationDAO db = new OperationDAO();
            db.getUser_dao().addUser(new User("test", "password", "test@test.com", "this",
                    "test", "f", "test_id"));
            db.getPerson_dao().addPerson(new Person("test", "test", "this", "test", "m"));
            db.getEvent_dao().addEvent(new Event("test", "test", "test", 12.5f,
                    12.5f, "USA", "provo", "birth", 2019));
            db.commitAndCloseConnection(true);

            ClearDAO clear_dao = new ClearDAO();
            success = clear_dao.clearDataBase();
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " ClearDAOTest: Exception caught: test fails");
            success = false;
        }

        assertTrue(success);
    }
}
