package services;

import java.time.LocalTime;
import java.util.List;

import dao.DataBaseException;
import dao.OperationDAO;
import models.*;
import response.EventAllResponse;
import response.Response;
import response.ErrorResponse;
import response.EventResponse;

/**
 * Get a single event.
 * Handle DataBaseException.
 */
public class EventService{
    
    public EventService(){}
    
    /**
     * Get the requested event if the user is logged in.
     * @param   token: user's token.
     * @param   event_id: event to find.
     * @return  return an EventResponse if the event is fetched.
     *          Else return an ErrorResponse.
     */
    public Response getEvent(String token, String event_id){
        if(token == null || event_id == null){
            System.out.println(LocalTime.now() + " EventService.getEvent(): Error: one of the parameters is null");
        }

        OperationDAO db = null;

        try{
            db = new OperationDAO();

            //Check if user is connected
            String user_name = db.getAutToken_dao().isConnected(token);
            if(user_name == null){
                System.out.println(LocalTime.now() + " EventService.getEvent(): No connected user for this token: \"" + token + "\".");
                return new ErrorResponse("User not connected");
            }
            System.out.println(LocalTime.now() + " EventService.getEvent():  user \"" + user_name + "\" connected. Fetching event...");

            // Get event
            Event event = db.getEvent_dao().getEvent(event_id, user_name);

            if(event != null){
                return new EventResponse(event.getEventId(), event.getAssociatedUsername(), event.getPersonId(), event.getLatitude(),
                        event.getLongitude(), event.getCountry(), event.getCity(), event.getEventType(), event.getYear());
            }
            else{
                System.out.println(LocalTime.now() + " EventService.getEvent(): Event object came back null");
                return new ErrorResponse("The event was not found.");
            }
        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " EventService.getEvent(): Error: " + message.toString());
            return new ErrorResponse(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " EventService.getEvent(): Error: " + e.toString());
            e.printStackTrace();
            return new ErrorResponse("Internal error: unable to retrieve event");
        }
        finally {
            db.commitAndCloseConnection(false);
        }
    }

    public Response getEventAll(String token){
        if(token == null){
            System.out.println(LocalTime.now() + " EventService.Event(): Error: token is null");
            return  new ErrorResponse("Missing authorization token.");
        }

        OperationDAO db = null;

        try{
            db = new OperationDAO();

            // Check if user is connected
            String user_name = db.getAutToken_dao().isConnected(token);
            if (user_name == null) {
                System.out.println(LocalTime.now() + " EventService.getEventAll(): user not connected.");
                return new ErrorResponse("User not connected");
            }
            System.out.println(LocalTime.now() + " EventService.getEventAll(): user \"" + user_name +"\" is connected.");

            // Get list of events
            List<Event> events = db.getEvent_dao().getEventAll(user_name);

            if(events == null){
                System.out.println(LocalTime.now() + " EventService.getEventAll(): Error: list of events came back null.");
                return new ErrorResponse("Internal Error: Unable to retrieve events");
            }
            else{
                System.out.println(LocalTime.now() + " EventService.getEventAll(): a list of events has been returned.");
                return new EventAllResponse(events);
            }
        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " EventService.getEvent(): Error: " + message.toString());
            return new ErrorResponse(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " EventService.getEvent(): Error: " + e.toString());
            e.printStackTrace();
            return new ErrorResponse("Internal Error: unable to retrieve requested event");
        }
        finally {
            db.commitAndCloseConnection(false);
        }
    }
}
