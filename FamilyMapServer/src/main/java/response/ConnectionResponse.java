package response;

/**
 * Response used by RegisterService and LoginService.
 * Indicates a user that he/she has been connected (and registered).
 */
public class ConnectionResponse extends Response{
    
    private String token = null;
    private String userName = null;
    private String personID = null;
    
    public ConnectionResponse(){}

    public ConnectionResponse(String token, String user_name, String person_id) {
        this.token = token;
        this.userName = user_name;
        this.personID = person_id;
    }



}