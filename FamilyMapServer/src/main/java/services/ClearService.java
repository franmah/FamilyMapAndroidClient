package services;


import java.time.LocalTime;

import dao.ClearDAO;
import dao.DataBaseException;
import response.Response;
import response.SuccessResponse;
import response.ErrorResponse;

/**
 * Clear the data base.
 * Handle DataBaseException.
 */
public class ClearService{
    
    /**
     * Clear the database.
     * @return  SuccessResponse of ErrorResponse with information.
     */
    public Response clearDataBase(){
        ClearDAO clear_dao = new ClearDAO();

        try{
            if(clear_dao.clearDataBase()){
                System.out.println(LocalTime.now() + " ClearService: the database has been cleared.");
                return new SuccessResponse("The database has been cleared!");
            }
        }
        catch (DataBaseException message){
            return new ErrorResponse(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " ClearService: Error: " + e.toString());
            return  new ErrorResponse("Internal error: unable to clear database.");
        }
        
        return null;
    }    
}
