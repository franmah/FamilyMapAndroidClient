package response;

/**
 * Response used by every Service class in case an error occur.
 * This response contains a message explaning the error.
 */
public class ErrorResponse implements Response{
    
    private String message = null;
    
    public ErrorResponse(){}
    
    public ErrorResponse(String message){
        this.message = message;
    }
    
    
}