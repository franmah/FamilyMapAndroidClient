package dao;


/**
 * DataBaseException:
 * Return a message when an error occurs while using the database.
   */
public class DataBaseException extends RuntimeException{
    
    public DataBaseException(){
        super();
    }
    
    public DataBaseException(String message){
        super(message);
    }
    
    
}