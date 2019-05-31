package services;

import org.junit.*;

import dao.OperationDAO;
import dao.UserDAO;
import models.User;
import request.LoginRequest;
import response.ConnectionResponse;
import response.ErrorResponse;
import response.Response;

import static org.junit.Assert.*;

public class LoginServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void loginUserPass(){

        OperationDAO db = null;

        User user = new User("test", "password", "test@test.com", "this",
                "test", "f", "test_id");

        boolean success = false;
        try {
            System.out.println("testing...");

            // register the user using UserDAO
            db = new OperationDAO();
            UserDAO udao = db.getUser_dao();
            udao.deleteUsers();
            udao.addUser(user);
            db.commitAndCloseConnection(true); // Connection need to be closed in order to use LoginService.

            // Create Request
            LoginRequest req = new LoginRequest("test", "password");

            // Test loginService
            LoginService login_service = new LoginService();
            Response response = login_service.LoginUser(req);

            // Check response type:
            if(response instanceof ErrorResponse){
                System.out.println("ErrorResponse returned");
                success = false;
            }
            else if (response instanceof ConnectionResponse){
                System.out.println("ConnectionResponse returned");
                success = true;
            }

        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void loginUserFail(){
        OperationDAO db = null;

        boolean success = false;
        try {

            // Clear Data Base
            db = new OperationDAO();
            UserDAO udao = db.getUser_dao();
            udao.deleteUsers();
            db.commitAndCloseConnection(true); // Connection needs to be closed in order to use LoginService.

            // Create Request
            LoginRequest req = new LoginRequest("test", "password");

            // Test loginService
            LoginService login_service = new LoginService();
            Response response = login_service.LoginUser(req);

            // Check response type:
            if(response instanceof ErrorResponse){
                System.out.println("ErrorResponse returned");
                success = true;
            }
            else if (response instanceof ConnectionResponse){
                System.out.println("ConnectionResponse returned");
                success = false;
            }

        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void loginUserWrongPassword(){
        OperationDAO db = null;

        User user = new User("test", "password", "test@test.com", "this",
                "test", "f", "test_id");

        boolean success = false;
        try {
            System.out.println("testing...");

            // register the user using UserDAO
            db = new OperationDAO();
            UserDAO udao = db.getUser_dao();
            udao.deleteUsers();
            udao.addUser(user);
            db.commitAndCloseConnection(true); // Connection need to be closed in order to use LoginService.

            // Create Request
            LoginRequest req = new LoginRequest("test", "wrong");

            // Test loginService
            LoginService login_service = new LoginService();
            Response response = login_service.LoginUser(req);

            // Check response type:
            if(response instanceof ErrorResponse){
                System.out.println("ErrorResponse returned");
                success = true;
            }
            else if (response instanceof ConnectionResponse){
                System.out.println("ConnectionResponse returned");
                success = false;
            }

        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void loginUserWrongUserName(){
        OperationDAO db = null;

        User user = new User("test", "password", "test@test.com", "this",
                "test", "f", "test_id");

        boolean success = false;
        try {
            System.out.println("testing...");

            // register the user using UserDAO
            db = new OperationDAO();
            UserDAO udao = db.getUser_dao();
            udao.deleteUsers();
            udao.addUser(user);
            db.commitAndCloseConnection(true); // Connection need to be closed in order to use LoginService.

            // Create Request
            LoginRequest req = new LoginRequest("wrong", "password");

            // Test loginService
            LoginService login_service = new LoginService();
            Response response = login_service.LoginUser(req);

            // Check response type:
            if(response instanceof ErrorResponse){
                System.out.println("ErrorResponse returned");
                success = true;
            }
            else if (response instanceof ConnectionResponse){
                System.out.println("ConnectionResponse returned");
                success = false;
            }

        }
        catch (Exception e){
            success = false;
        }

        assertTrue(success);
    }
}
