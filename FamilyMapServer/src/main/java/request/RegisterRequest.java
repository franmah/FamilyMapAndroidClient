package request;

/**
 * Request used by RegisterService class.
 */
public class RegisterRequest{
    
    private String userName = null;
    private String password = null;
    private String email = null;
    private String firstName = null;
    private String lastName = null;
    private String gender = null;

    public RegisterRequest(){}
    
    public RegisterRequest(String user_name, String password, String email, String first_name,
        String last_name, String gender){
            
        this.userName = user_name;
        this.password = password;
        this.email = email;
        this.firstName = first_name;
        this.lastName = last_name;
        this.gender = gender;
            
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "user_name='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", first_name='" + firstName + '\'' +
                ", last_name='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public String getUser_name() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return firstName;
    }

    public String getLast_name() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }
}