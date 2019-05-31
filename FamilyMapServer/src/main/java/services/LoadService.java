package services;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;

import dao.DataBaseException;
import dao.EventDAO;
import dao.OperationDAO;
import dao.PersonDAO;
import dao.UserDAO;
import models.*;
import response.Response;
import response.SuccessResponse;
import response.ErrorResponse;
import request.LoadRequest;

/**
 * Add multiple users, people and events
 * Handle DataBaseException.
 */
 
public class LoadService{

    private OperationDAO db = null;
    
    public LoadService(){}
    
    /**
     * Add each user, person and event in the request.
     * 
     * @param   req: request with user, person and event arrays.
     * @return  SuccessResponse or ErrorResponse with explanation message.
     */
    public Response loadDataBase(LoadRequest req){
        if(req == null) {
            return new ErrorResponse("request not valid");
        }

        boolean commit = false;

        try{
            // Clear database
            ClearService clear_service = new ClearService();
            clear_service.clearDataBase();

            db = new OperationDAO();
            UserDAO user_dao = db.getUser_dao();
            PersonDAO person_dao = db.getPerson_dao();
            EventDAO event_dao = db.getEvent_dao();

            // Retrieve info from the request
            ArrayList<User> users = req.getUsers();
            ArrayList<Person> persons = req.getpersons();
            ArrayList<Event> events = req.getEvents();

            if(users == null || persons == null || events == null){
                return new ErrorResponse("Error: empty data");
            }

            // Fill the database
            for (User user : users){
                if(user.getPersonId() == null) {
                    throw new DataBaseException("The user \"" + user.getUserName() + "\" does not have a person id.");
                }
                user_dao.addUser(user);
            }
            for (Person person : persons){
                person_dao.addPerson(person);
            }
            for (Event event : events){
                event_dao.addEvent(event);
            }

            commit = true;

            return new SuccessResponse(String.format("Successfully added %d user(s), %d person(s), and %d event(s) to the database.",
                                            users.size(), persons.size(), events.size()));

        }catch (DataBaseException message){
            System.out.println(LocalTime.now() + " LoadService.LoadDatabase(): Error: " + message.toString());
            return new ErrorResponse(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " LoadService.LoadDatabase(): Error: " + e.toString());
            e.printStackTrace();
            return new ErrorResponse("Internal Error while trying to the database.");
        }
        finally {
            if(db != null) {
                db.commitAndCloseConnection(commit);
                db = null;
            }
        }
    }
}