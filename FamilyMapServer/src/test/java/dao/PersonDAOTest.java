package dao;

import org.junit.*;
import java.util.List;
import static org.junit.Assert.*;
import models.Person;


public class PersonDAOTest {
    private OperationDAO db= null;

    @Before
    public void setUp() throws Exception {
        db = new OperationDAO();
        db.getEvent_dao().deleteEvents();
        db.getUser_dao().deleteUsers();
        db.getPerson_dao().deletePersons();
    }

    @After
    public void tearDown() throws Exception {
        db.commitAndCloseConnection(false);
        db = null;
    }

    @Test
    public void deletePass() throws Exception{
        PersonDAO person_dao = db.getPerson_dao();
        boolean success = false;

        try {
            success = person_dao.deletePersons();
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void deleteMultiplePass(){
        PersonDAO person_dao = db.getPerson_dao();
        boolean success = false;

        final int NUM_ROWS = 10;

        for(int i = 0; i < NUM_ROWS; i++) {
            person_dao.addPerson(new Person(String.format("test" + i), "test", "this", "test", "m"));
        }
        try {
            success = person_dao.deletePersons();
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void addPass() throws Exception {
        PersonDAO person_dao = db.getPerson_dao();
        Person person = new Person("test", "test", "this", "test", "m");

        // insert without optional members (father_id...)
        boolean success = false;

        try {
            success = person_dao.addPerson(person);
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);

        // insert with optional members (father_id...)
        Person person2 = new Person("test2", "test", "this", "test", "m");
        person2.setFatherId("MR test");
        person2.setMotherId("Mrs test");
        person2.setSpouseId("Mrs testee");

        success = false;

        try {
            success = person_dao.addPerson(person2);
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void addFail() throws Exception {
        PersonDAO person_dao = db.getPerson_dao();
        Person person = new Person("test", "test", "this", "test", "m");

        // insert without optional members (father_id...)
        boolean success = true;

        try {
            person_dao.addPerson(person);

            // Insert person again: should fail!
            success = person_dao.addPerson(person);
        }
        catch (Exception e){
            success = false;
        }

        assertFalse(success);


    }

    @Test
    public void getPersonPass() throws  Exception{
        PersonDAO person_dao = db.getPerson_dao();
        person_dao.deletePersons();

        Person person = new Person("test_person", "test_user", "first", "last", "m");
        Person compare_person = null;

        try{
            person_dao.addPerson(person);
            compare_person = person_dao.getPerson("test_person", "test_user");
        }
        catch (Exception e){
            assertTrue(false);
        }

        assertEquals(person, compare_person);
    }

    @Test
    public void getPersonFail() throws Exception{
        PersonDAO person_dao = db.getPerson_dao();

        Person person = new Person("test", "test", "this", "test", "m");
        Person compare_person = null;

        try{
            person_dao.addPerson(person);
            // The names are different.
            compare_person = person_dao.getPerson("different_test", "test");
        }
        catch (Exception e){
            assertFalse(false); // This test should fail because getPerson will return an exception.
        }

        assertNotEquals(person, compare_person);
    }

    @Test
    public void getPersonWrongUserName(){
        PersonDAO person_dao = db.getPerson_dao();

        Person person = new Person("test", "test", "this", "test", "m");
        Person compare_person = null;

        try{
            person_dao.addPerson(person);
            // The names are different.
            compare_person = person_dao.getPerson("test", "wrong");
        }
        catch (Exception e){
            assertFalse(false); // This test should fail because getPerson will return an exception.
        }

        assertNotEquals(person, compare_person);
    }

    @Test
    public void getPersonAllPass() throws Exception{
        PersonDAO person_dao = db.getPerson_dao();

        final int NUM_ROWS = 10;

        for(int i = 0; i < NUM_ROWS; i++) {
            person_dao.addPerson(new Person(String.format("test" + i), "test", "this", "test", "m"));
        }

        boolean success = false;

        try{
            List<Person> people = person_dao.getPersonAll("test");
            if(people.size() == NUM_ROWS){
                success = true;
            }
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void getPersonAllWrongUserName(){

        PersonDAO person_dao = db.getPerson_dao();

        final int NUM_ROWS = 10;

        for(int i = 0; i < NUM_ROWS; i++) {
            person_dao.addPerson(new Person(String.format("test" + i), "test", "this", "test", "m"));
        }

        boolean success = false;

        try{
            List<Person> people = person_dao.getPersonAll("wrong");
            if(people.size() == 0){
                success = true;
            }
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void deleteUserFamilyEventsPass(){
        PersonDAO person_dao = db.getPerson_dao();
        boolean success = false;

        try {
            person_dao.deleteUserFamily("user");
            success = true;
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void deleteUserFamilyMultipleEventsPass(){
        PersonDAO person_dao = db.getPerson_dao();
        boolean success = false;

        final int NUM_ROWS = 10;

        for(int i = 0; i < NUM_ROWS; i++) {
            person_dao.addPerson(new Person(String.format("test" + i), "test", "this", "test", "m"));
        }

        try {
            person_dao.deleteUserFamily("user");
            success = true;
        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void updatePersonParentsPass(){
        try {
            db.getPerson_dao().deletePersons();

            Person person = new Person("test_person", "test_user", "first", "last", "f");

            db.getPerson_dao().addPerson(person);

            person.setFatherId("father");
            person.setMotherId("mother");

            db.getPerson_dao().updatePersonParents(person);

            Person updated_person = db.getPerson_dao().getPerson("test_person", "test_user");

            assertEquals(person.getFatherId(), updated_person.getFatherId());
            assertEquals(person.getMotherId(), updated_person.getMotherId());
        }
        catch (Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void updatePersonParentsFail(){
        boolean success = false;
        try {
            db.getPerson_dao().deletePersons();

            Person person = new Person("test_person", "test_user", "first", "last", "f");

            db.getPerson_dao().addPerson(person);

            person.setFatherId("father");
            person.setMotherId("mother");

            db.getPerson_dao().updatePersonParents(person);

            Person updated_person = db.getPerson_dao().getPerson("wrong_person", "test_user");

            assertEquals(person.getFatherId(), updated_person.getFatherId());
            assertEquals(person.getMotherId(), updated_person.getMotherId());
        }
        catch (Exception e){
            success = true;
        }

        assertTrue(success);
    }

    @Test
    public void updatePersonParentsWrongUser(){
        boolean success = false;
        try {
            db.getPerson_dao().deletePersons();

            Person person = new Person("test_person", "test_user", "first", "last", "f");

            db.getPerson_dao().addPerson(person);

            person.setFatherId("father");
            person.setMotherId("mother");

            db.getPerson_dao().updatePersonParents(person);

            Person updated_person = db.getPerson_dao().getPerson("test_person", "wrong_user");

            assertEquals(person.getFatherId(), updated_person.getFatherId());
            assertEquals(person.getMotherId(), updated_person.getMotherId());
        }
        catch (Exception e){
            success = true;
        }

        assertTrue(success);
    }
}
