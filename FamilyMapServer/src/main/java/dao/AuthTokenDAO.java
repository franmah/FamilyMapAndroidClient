package dao;

import models.AuthToken;
import java.time.LocalTime;
import java.sql.*;

/**
 * Manage AuthToken in the database:
 * Add tokens and link them to users.
 * Check if a token is linked to a user.
 */
public class AuthTokenDAO{
    private Connection connection = null;
    
    public AuthTokenDAO(){
    }

    public AuthTokenDAO(Connection connection){ this.connection = connection; }
    
    /**
     * Check in the database if a token is linked to a user.
     * @param   token: the user's token.
     * @return  the user_name, will be null if no user found.
     */
    public String isConnected(String token) throws DataBaseException{
        ResultSet result = null;
        PreparedStatement stmt = null;

        String query = "SELECT * FROM authtokens WHERE token = ?";
        // Look for a token because there can be multiple same user but only one token.

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, token);
            result = stmt.executeQuery();

            String user_name = null;

            if(result.next()){
                user_name = result.getString("user_name");
                System.out.println(LocalTime.now() + " AuthTokenDAO.isConnected(): A pair user/token has been found for: \"" +
                                    user_name + "\"");
            }
            else{
                System.out.println(LocalTime.now() + " AuthToken.isConnected(): No user found for this token");
            }

            return user_name;

        }
        catch(DataBaseException message){
            System.out.println(LocalTime.now() + " AuthTokenDao.isConnected(): " + message.toString());
            throw new DataBaseException(message.toString());
        }
        catch(Exception e){
            System.out.println(LocalTime.now() + " AuthTokenDAO.isConnected(): ERROR while retrieving data: " + e.toString());
            e.printStackTrace();
            throw new DataBaseException("Error while retrieving token from database");
        }
        finally {
            try {
                if (result != null) { result.close(); }
                if (stmt != null) { stmt.close(); }
            }
            catch (Exception e) {
                System.out.println(LocalTime.now() + " AuthTokenDAO.isConnected(): ERROR Unable to close resultSet, " + e.toString());
                e.printStackTrace();
                throw new DataBaseException("Unable to retrieve token from database");
            }
        }
    }
    
    /**
     * Add a token with it's user (user_name). Assume the user has already been checked for registration.
     * @param   token: the token to add.
     * @return  true if the token is added, else return an error.
     */
    public boolean addToken(AuthToken token) throws DataBaseException{

        // check if user is already connected
        String test_connection = isConnected(token.getToken());
        if(test_connection != null){
            System.out.println(LocalTime.now() + "  AuthTokenDao.addToken(): ERROR: User is already connected ");
            throw new DataBaseException("User already connected");
        }

        String query = "INSERT INTO authtokens VALUES(?,?);";

        PreparedStatement stmt = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, token.getToken());
            stmt.setString(2, token.getUser());
            stmt.executeUpdate();

            System.out.println(LocalTime.now() + " AuthTokenDao.addToken(): Token for the user: \"" +
                                token.getUser() + "\" has been added");
            return true;
        }
        catch(DataBaseException message){
            System.out.println(LocalTime.now() + "  AuthTokenDao.addToken(): ERROR: " + message.toString());
            throw new DataBaseException(message.toString());
        }
        catch(Exception e){
            System.out.println(LocalTime.now() + "AuthTokenDao.addToken(): " + e.toString());
            e.getStackTrace();
            throw new DataBaseException("Something went wrong while connecting the user");
        }
        finally{
            if(stmt != null){
                try{
                    stmt.close();
                }
                catch (Exception e){
                    System.out.println(LocalTime.now() + " AuthTokenDao.addToken(): ERROR Unable to close prepared statement, " + e.toString());
                    e.printStackTrace();
                    throw new DataBaseException("Unable to connect user");
                }
            }
        }

    }

    /** Delete every entry in authtokens table.
     *
     * @return true is the table has been cleared.
     * @throws DataBaseException
     */
    public boolean deleteTokens() throws  DataBaseException{

        String query = "DELETE FROM authtokens;";

        PreparedStatement stmt = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.executeUpdate();

            System.out.println(LocalTime.now() + " AuthTokenDAO.deleteTokens(): data in authtokens table cleared");
            return true;
        }
        catch(DataBaseException message){
            System.out.println(LocalTime.now() + " AuthTokenDAO.deleteTokens(): " + message.toString());
            throw new DataBaseException(message.toString());
        }
        catch(Exception e){
            System.out.println(LocalTime.now() + " AuthTokenDAO.deleteTokens(): ERROR while deleting data from authtokens: " + e.toString());
            e.printStackTrace();
            throw new DataBaseException("Error while deleting data from authtokens table");
        }
        finally {
            if(stmt != null){
                try{
                    stmt.close();
                }
                catch (Exception e){
                    System.out.println(LocalTime.now() + " AuthTokenDAO.deleteTokens(): ERROR unable to close prepared statement, " + e.toString());
                    e.printStackTrace();
                    throw new DataBaseException("Unable to delete tokens");
                }
            }
        }

    }
}