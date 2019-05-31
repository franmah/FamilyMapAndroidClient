package services;

import org.junit.*;

import java.time.LocalTime;
import java.util.List;
import dao.*;
import models.*;
import response.PersonAllResponse;
import response.PersonResponse;
import response.ErrorResponse;
import response.Response;

import static org.junit.Assert.*;

public class PersonServiceTest {

    @Before
    public void setUp(){
        OperationDAO db = new OperationDAO();
        AuthTokenDAO atdao = db.getAutToken_dao();
        PersonDAO pdao = db.getPerson_dao();

        atdao.deleteTokens();
        pdao.deletePersons();

        atdao.addToken(new AuthToken("test_token", "test_user"));
        // (no need to add the user to connect it when using AuthTokenDAO (registration is checked by loginService)
        pdao.addPerson(new Person("test_person", "test_user", "test", "test",
                "f"));

        db.commitAndCloseConnection(true);
    }

    @After
    public  void tearDown(){
        OperationDAO db = new OperationDAO();
        db.getAutToken_dao().deleteTokens();
        db.getPerson_dao().deletePersons();
        db.commitAndCloseConnection(true);
    }

    @Test
    public void getPersonPass(){
        boolean success = false;
        try {
            // getPerson test:
            PersonService person_service = new PersonService();
            Response response = person_service.getPerson("test_token", "test_person");



            if (response instanceof PersonResponse) {
                success = true;
            } else {
                success = false;
            }
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void getPersonFailWrongID(){
        boolean success = false;

        try {
            PersonService person_service = new PersonService();
            Response response = person_service.getPerson("test_token", "wrong_person");

            if (response instanceof PersonResponse) {
                success = false;
            } else {
                success = true;
            }
        }
        catch (Exception e){
            success = true;
        }

        assertTrue(success);
    }


    @Test
    public void getPersonWrongToken(){
        boolean success = false;

        try {
            PersonService person_service = new PersonService();
            Response response = person_service.getPerson("wrong_token", "test_person");

            if (response instanceof PersonResponse) {
                success = false;
            } else {
                success = true;
            }
        }
        catch (Exception e){
            success = true;
        }

        assertTrue(success);
    }

    @Test
    public void getPersonAllPass(){
        boolean success = true;
        final int NUM_ROWS = 10;
        try{
            // Fill database with a list of people.
            OperationDAO db = new OperationDAO();
            PersonDAO pdao = db.getPerson_dao();

            for(int i = 0; i < NUM_ROWS; i++) {
                pdao.addPerson(new Person(String.format("test" + i), "test_user", "this", "test", "m"));
            }
            // Number of Person will be NUM_ROWS + 1 because a Person is added in @Before.

            db.commitAndCloseConnection(true);

            // Test PersonService
            PersonService person_service = new PersonService();

            Response response = person_service.getPersonAll("test_token");

            if(response instanceof PersonAllResponse){
                List<Person> people = ((PersonAllResponse) response).getPeople();
                System.out.println(LocalTime.now() + " PersonServiceTest.getPersonAllPass: right number of people returned");
                if(people.size() != NUM_ROWS + 1){
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
    public void  getPersonAllWrongUser(){
        boolean success = true;
        final int NUM_ROWS = 10;
        try{
            // Fill database with a list of people.
            OperationDAO db = new OperationDAO();
            PersonDAO pdao = db.getPerson_dao();

            for(int i = 0; i < NUM_ROWS; i++) {
                pdao.addPerson(new Person(String.format("test" + i), "wrong_user", "this", "test", "m"));
            }
            // Number of Person will be 1 because a Person is added in @Before.

            db.commitAndCloseConnection(true);

            // Test PersonService
            PersonService person_service = new PersonService();

            Response response = person_service.getPersonAll("test_token");

            if(response instanceof PersonAllResponse){
                List<Person> people = ((PersonAllResponse) response).getPeople();
                System.out.println(LocalTime.now() + " PersonServiceTest.getPersonAllPass: right number of people returned");
                if(people.size() != 1){
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
}
