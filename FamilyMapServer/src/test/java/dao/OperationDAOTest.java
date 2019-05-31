package dao;

import org.junit.*;
import java.util.List;

import models.AuthToken;

import static org.junit.Assert.*;

public class OperationDAOTest {

    @Test
    public void commitAndClosePass(){
        boolean success = false;
        OperationDAO db = null;

        try{
            db = new OperationDAO();
        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }
        finally {
            db.commitAndCloseConnection(true);
        }
    }


    @Test
    public void commitAndCloseFail(){
        boolean success = false;
        OperationDAO db = null;
        OperationDAO db_second = null;

        try{
            db = new OperationDAO();
            db_second = new OperationDAO();

            db_second.getAutToken_dao().addToken(new AuthToken("test"));
            db.getAutToken_dao().addToken(new AuthToken("test2"));
            // Should create a "DataBase busy" error because there are two connections at the same time.

            success = false;
        }
        catch (Exception e){
            success = true;
            System.out.println(e.toString());
            e.printStackTrace();
        }
        finally {
            db.commitAndCloseConnection(true);
            db_second.commitAndCloseConnection(true);
        }

        assertTrue(success);
    }



}
