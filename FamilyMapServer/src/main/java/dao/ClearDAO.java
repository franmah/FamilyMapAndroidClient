package dao;


import java.time.LocalTime;

/**
 * Delete user, person, event and token entry.
 */
public class ClearDAO{

    public ClearDAO(){}
    
    /**
     * Clear the data base. ClearDAO is not connected to the database through OperationDAO like other DAO classes.
     * ClearDAO has it's own instance of OperationDAO, using it along an OperationDAO object will block the
     * database!(due to multiple connections)
     * @return  true if database is deleted, else throw an error.
     */
    public boolean clearDataBase() throws DataBaseException{
        boolean commit = false;
        OperationDAO db = null;

        try {
            db = new OperationDAO();

            db.getEvent_dao().deleteEvents();
            db.getAutToken_dao().deleteTokens();
            db.getPerson_dao().deletePersons();
            db.getUser_dao().deleteUsers();

            commit = true;
            return true;
        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " ClearDAO: Error while clearing database");
            commit = false;
            throw new DataBaseException(message.toString());
        }
        catch (Exception e){
            commit = false;
            System.out.println(LocalTime.now() + " ClearDAO: Error : " + e.toString());
            e.printStackTrace();
            throw new DataBaseException("Internal Error: Unable to clear database");
        }
        finally {
            try{
                db.commitAndCloseConnection(commit);
                if(commit){
                    System.out.println(LocalTime.now() + " ClearDAO: Connection closed -> changes were committed.");
                }
                else{
                    System.out.println(LocalTime.now() + " ClearDAO: Connection closed -> changes were not committed.");
                }
            }
            catch (Exception e){
                System.out.println(LocalTime.now() + " ClearDAO: Error unable to close connection.");
                e.printStackTrace();
                throw new DataBaseException("Internal Error: Unable to clear database");
            }
        }

    }
}