package response;

/**
 * Response used by Services that only need a to display a message in case of success.
 */
public class SuccessResponse implements Response{
    
    private String message = null;
    
    public SuccessResponse(){}
    
    public SuccessResponse(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}