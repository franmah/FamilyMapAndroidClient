package com.client.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.client.httpClient.ServerProxy;
import com.client.models.Model;
import com.client.request.LoginRequest;
import com.client.request.LoginTaskRequest;
import com.client.response.ConnectionResponse;
import com.client.service.DataRetriever;
import com.fmahieu.familymap.loginFragment;

public class LoginUserTask extends AsyncTask<LoginTaskRequest, Void, ConnectionResponse>
                            implements RetrieveDataTask.RetrieveDataListener {

    public interface LoginUserListener{
        public void loginResponse(String response, boolean isErrorMessage);
    }

    private LoginUserListener listener;

    public LoginUserTask(LoginUserListener listener){this.listener = listener;}

    private final String TAG = "LoginUserTaks";
    @Override
    protected ConnectionResponse doInBackground(LoginTaskRequest... requests){
        assert requests.length > 0;

        Log.i(TAG, "in LoginUser, starting login request...");

        ServerProxy proxy = new ServerProxy(requests[0].getHostName(), requests[0].getPortNumber());
        ConnectionResponse response = proxy.login(new LoginRequest(requests[0].getUserName(), requests[0].getPassword()));

        return response;
    }

    @Override
    protected void onPostExecute(ConnectionResponse response){

        if(response.getErrorMessage() == null){
            Model model = Model.getInstance();
            model.setUserToken(response.getToken());
            model.setUserPersonId(response.getPersonID());

            Log.i(TAG, "RegisterAsync.onPostExecute : Starting retrieveData aSyncTask");
            new RetrieveDataTask(this).execute();

        }
        else{
            Log.e(TAG,response.getErrorMessage());
            listener.loginResponse(response.getErrorMessage(), true);
        }
    }

    @Override
    public void responseMessage(String message, boolean isErrorMessage){
        listener.loginResponse(message, isErrorMessage);
    }
}