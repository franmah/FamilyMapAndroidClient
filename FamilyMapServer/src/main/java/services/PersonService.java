package services;

import java.time.LocalTime;
import java.util.List;

import dao.*;
import models.Person;
import response.PersonAllResponse;
import response.Response;
import response.PersonResponse;
import response.ErrorResponse;

/**
 * Get one person using his/her id.
 * The person must be related to the user doing the request.
 * Handle DataBaseException.
 */
public class PersonService{

    
    public PersonService(){}
    
    /**
     * if user is logged in, get a person if that person is related to the user.
     * 
     * @param   token: user's AuthToken.
     * @param   person_id: person to return.
     * @return  PersonResponse with person's info,
     *          return ErrorResponse if an error occurs.
     */
    public Response getPerson(String token, String person_id){

        if(token == null || person_id == null){
            System.out.println(LocalTime.now() + " PersonService.getPerson(): Error: one of the parameters is null");
            return  new ErrorResponse("Missing element");
        }

        OperationDAO db = null;

        try{
            db = new OperationDAO();

            // Check if user is connected
            String user_name = db.getAutToken_dao().isConnected(token);
            if(user_name == null){
                System.out.println(LocalTime.now() + " PersonService.getPerson(): user not connected.");
                return new ErrorResponse("User not connected");
            }
            System.out.println(LocalTime.now() + " PersonService.getPerson(): user is connected. Fetching person...");

            // Get the person
            Person person = db.getPerson_dao().getPerson(person_id, user_name);

            if(person != null){
                return new PersonResponse(person.getAssociatedUsername(), person.getPersonId(), person.getFirstName(),
                        person.getLastName(), person.getGender(), person.getFatherId(), person.getMotherId(), person.getSpouseId());
            }
            else{
                System.out.println(LocalTime.now() + " PersonService.getPerson(): Person object came back null");
                return new ErrorResponse("Person not found");
            }

        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " PersonService.getPerson(): Error: " + message.toString());
            return new ErrorResponse(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " PersonService.getPerson(): Error: " + e.toString());
            e.printStackTrace();
            return new ErrorResponse("Internal Error: unable to retrieve requested person");
        }
        finally {
            db.commitAndCloseConnection(false);
        }
    }

    public Response getPersonAll(String token){
        if(token == null){
            System.out.println(LocalTime.now() + " PersonService.getPerson(): Error: token is null");
            return  new ErrorResponse("Missing authorization token.");
        }

        OperationDAO db = null;

        try {
            db = new OperationDAO();

            // Check if user is connected
            String user_name = db.getAutToken_dao().isConnected(token);
            if (user_name == null) {
                System.out.println(LocalTime.now() + " PersonService.getPersonAll(): user not connected.");
                return new ErrorResponse("User not connected");
            }
            System.out.println(LocalTime.now() + " PersonService.getPersonAll(): user \"" + user_name + "\"  is connected.");

            // Get list of people
            List<Person> people = db.getPerson_dao().getPersonAll(user_name);

            if(people == null){
                System.out.println(LocalTime.now() + " PersonService.getPersonAll(): The list of person came back null");
                return new ErrorResponse("Internal Error: unable to retrieve list of people");
            }
            else{
                System.out.println(LocalTime.now() + " PersonService.getPersonAll(): A list of people has been returned");
                return new PersonAllResponse(people);
            }

        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " PersonService.getPerson(): Error: " + message.toString());
            return new ErrorResponse(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " PersonService.getPerson(): Error: " + e.toString());
            e.printStackTrace();
            return new ErrorResponse("Internal Error: unable to retrieve requested person");
        }
        finally {
            db.commitAndCloseConnection(false);
        }

    }
}