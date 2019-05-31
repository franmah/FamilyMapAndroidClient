package dao;

import models.User;
import models.AuthToken;
import java.time.LocalTime;
import java.sql.*;
import java.util.ArrayList;

/**
 * Perform database operation for users
 */ 
public class UserDAO{

   private Connection connection = null;

   public UserDAO(){}

   public UserDAO(Connection connection){
      this.connection = connection;
   }

   /**
    * Add a user to the database. Create a String with the query and pass it to the select method in OperationDAO
    * 
    * @param    user to add
    * @return   False if the username is already used.
    */
   public boolean addUser(User user) throws DataBaseException {

      // Check if user is already registered
      User tmp_user = getUser(user.getUserName());
      if(tmp_user != null){
         return false;
      }
      // Create update statement:
      String query = "INSERT INTO users VALUES(?,?,?,?,?,?,?);";

      PreparedStatement stmt = null;

      try{
         stmt = connection.prepareStatement(query);

         stmt.setString(1, user.getUserName());
         stmt.setString(3, user.getPassword());
         stmt.setString(4, user.getEmail());
         stmt.setString(5, user.getFirstName());
         stmt.setString(6, user.getLastName());
         stmt.setString(7, user.getGender());
         stmt.setString(2, user.getPersonId());

         stmt.executeUpdate();

      }
      catch(DataBaseException message){
         System.out.println(LocalTime.now() + "  UserDao.addUser(): ERROR: " + message.toString());
         throw new DataBaseException(message.toString());
      }
      catch(Exception e){
         System.out.println(LocalTime.now() + "UserDao.addUser(): " + e.toString());
         e.getStackTrace();
         throw new DataBaseException("Internal Error: Something went wrong while trying to register the user");
      }
      finally{
         if(stmt != null){
            try{
               stmt.close();
            }
            catch (Exception e){
               System.out.println(LocalTime.now() + " UserDao.addUser(): ERROR unable to close prepared statement, " + e.toString());
               throw new DataBaseException("Internal Error: Something went wrong while trying to register the user");
            }
         }
      }

      System.out.println(LocalTime.now() + " UserDAO.addUser(): User: \"" + user.getUserName() + "\" has been added");
      return true;
   }

   /**
    * Return a user using a user_name.
    * @param user_name: the user to fetch.
    * @return
    * @throws DataBaseException: Contains the message to be returned to the client in case of an error.
    */
   public User getUser(String user_name) throws DataBaseException {
      User usr = null;

      ResultSet result = null;
      PreparedStatement stmt = null;

      String query = "SELECT * FROM users WHERE user_name = ?";

      try{
         stmt = connection.prepareStatement(query);
         stmt.setString(1, user_name);

         result = stmt.executeQuery();

         if(result.next()){
            System.out.println(LocalTime.now() + " UserDAO.getUser(): User has been found");

            usr = new User(result.getString("user_name"),
                    result.getString("password"),
                    result.getString("email"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("gender"),
                    result.getString("person_id"));
         }
         if(usr != null){
            System.out.println(LocalTime.now() + " UserDAO.getUser() : Fetched user: \"" + usr.getUserName() + "\"");
         }
         return usr;

      }
      catch(DataBaseException message){
         System.out.println(LocalTime.now() + " UserDao.getUsers(): " + message.toString());
         throw new DataBaseException(message.toString());
      }
      catch(Exception e){
         System.out.println(LocalTime.now() + " UserDAO.getUsers(): ERROR while deleting data from users: " + e.toString());
         throw new DataBaseException("Error while deleting data from users table");
      }
      finally {
         if(result != null){
            try{
               stmt.close();
            }
            catch (Exception e){
               System.out.println(LocalTime.now() + " UserDao.getUser(): ERROR couldn't close prepared statement, " + e.toString());
               throw new DataBaseException("Unable to retrieve user");
            }
         }
         if(stmt != null){
            try{
               stmt.close();
            }
            catch (Exception e){
               System.out.println(LocalTime.now() + " UserDao.getUser(): ERROR couldn't close prepared statement, " + e.toString());
               throw new DataBaseException("Unable to retrieve user");
            }
         }
      }
   }

   /** Return an ArrayList with every user in the database.
    *
    * @return ArrayList of users
    * @throws DataBaseException
    */
   public ArrayList<User> getUserAll() throws  DataBaseException{
      ArrayList<User> users = new ArrayList<>();
      User usr = null;

      ResultSet result = null;
      PreparedStatement stmt = null;

      String query = "SELECT * FROM users";

      try{
         stmt = connection.prepareStatement(query);

         result = stmt.executeQuery();

         while(result.next()){
            System.out.println(LocalTime.now() + " UserDAO.getUserAll(): User has been found");

            usr = new User(result.getString("user_name"),
                    result.getString("password"),
                    result.getString("email"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("gender"),
                    result.getString("person_id"));

            users.add(usr);
         }

         return users;

      }
      catch(DataBaseException message){
         System.out.println(LocalTime.now() + " UserDao.getUsersAll(): " + message.toString());
         throw new DataBaseException(message.toString());
      }
      catch(Exception e){
         System.out.println(LocalTime.now() + " UserDAO.getUsersAll(): ERROR while retrieve users: " + e.toString());
         e.printStackTrace();
         throw new DataBaseException("Unable to retrieve users");
      }
      finally {
         if(result != null){
            try{
               stmt.close();
            }
            catch (Exception e){
               System.out.println(LocalTime.now() + " UserDao.getUserAll(): ERROR couldn't close prepared statement, " + e.toString());
               e.printStackTrace();
               throw new DataBaseException("Unable to retrieve users");
            }
         }
         if(stmt != null){
            try{
               stmt.close();
            }
            catch (Exception e){
               System.out.println(LocalTime.now() + " UserDao.getUserAll(): ERROR Unable to close prepared statement, " + e.toString());
               e.printStackTrace();
               throw new DataBaseException("Unable to retrieve user");
            }
         }
      }
   }

   /**
    * Create a token and use it to connect the user. If the user can't be logged in an error will be thrown,
    * Check if the user is registered and if the password is right (if not return a null string)
    * @param   user_name: the user to connect.
    * @param   password: the user's password.
    * @return  unique string token. If String is null then user was not registered or the password was wrong.
    */
   public String connectUser(String user_name, String password) throws DataBaseException {
      User user = null;

      try {

         // Check if user is registered.
         user = getUser(user_name);
         if (user == null) {
            System.out.println(LocalTime.now() + "UserDAO.connectUser(): Error: user not registered");
            return null;
         }

         // Check if password is right.
         if(!user.getPassword().equals(password)){
            System.out.println(LocalTime.now() + "UserDAO.connectUser(): wrong password");
            return null;
         }

         AuthToken token = new AuthToken(user_name);

         AuthTokenDAO atdao = new AuthTokenDAO(connection);
         if(!atdao.addToken(token)){
            return  null;
         }
         else{
            return token.getToken();
         }
      }
      catch(DataBaseException message){
         throw new DataBaseException(message.toString());
      }
      catch (Exception e){
         System.out.println(LocalTime.now() + " UserDAO.connectUser(): ERROR: unable to connect user: " + e.toString());
         throw new DataBaseException("Unable to connect user");
      }
   }

   /** delete EVERY user. (Does not delete related events and persons)
    *
    * @return
    * @throws DataBaseException
    */
   public boolean deleteUsers() throws  DataBaseException{

      PreparedStatement stmt = null;

      try{
         String query = "DELETE FROM users;";
         stmt = connection.prepareStatement(query);
         stmt.executeUpdate();
      }
      catch(DataBaseException message){
         System.out.println(LocalTime.now() + " UserDao.deleteUsers(): " + message.toString());
         throw new DataBaseException(message.toString());
      }
      catch(Exception e){
         System.out.println(LocalTime.now() + " UserDAO.deleteUsers(): ERROR while deleting data from users: " + e.toString());
         e.printStackTrace();
         throw new DataBaseException("Error while deleting data from users table");
      }
      finally {
         if(stmt != null){
            try{
               stmt.close();
            }
            catch (Exception e){
               System.out.println(LocalTime.now() + " UserDao.deleteUser(): ERROR couldn't close prepared statement, " + e.toString());
               e.printStackTrace();
               throw new DataBaseException("Unable to delete users");
            }
         }
      }

      System.out.println(LocalTime.now() + " UserDao.deleteUser(): data in users table cleared");
      return true;
   }


}