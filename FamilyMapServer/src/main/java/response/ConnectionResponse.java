package response;

/**
 * Response used by RegisterService and LoginService.
 * Indicates a user that he/she has been connected (and registered).
 */
public class ConnectionResponse implements Response{
    
    private String token = null;
    private String userName = null;
    private String personID = null;
    private String error_message = null;
    
    public ConnectionResponse(){}

    public ConnectionResponse(String token, String user_name, String person_id) {
        this.token = token;
        this.userName = user_name;
        this.personID = person_id;
    }

    public ConnectionResponse(String error_message){
        this.error_message = error_message;
    }



}