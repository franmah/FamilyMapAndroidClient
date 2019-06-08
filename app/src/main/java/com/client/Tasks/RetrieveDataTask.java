package com.client.Tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.client.models.Model;
import com.client.request.RetrieveDataRequest;
import com.client.service.DataRetriever;
import com.fmahieu.familymap.MainActivity;

public class RetrieveDataTask extends AsyncTask<RetrieveDataRequest, Void, String> {

    public interface RetrieveDataListener{
        void responseMessage(String message, boolean isErrorMessage);
    }

    private RetrieveDataListener listener;

    public RetrieveDataTask(RetrieveDataListener listener) {this.listener = listener;}

    private String TAG = "RetrieveDataTask";


    @Override
    protected String doInBackground(RetrieveDataRequest... requests){
        assert requests.length > 0;

        Log.i(TAG, "in RetrieveData, starting data request...");

        DataRetriever dataRetriever = new DataRetriever();
        String response = dataRetriever.pullData(requests[0].getHostName(), requests[0].getPortNumber());

        return response;
    }

    @Override
    protected void onPostExecute(String response){
        if(response != null){
            Log.i(TAG, "in RetrieveData, response from DataRetriever came back not null, an error occurred.");
            listener.responseMessage(response, true);
        }
        else {
            Log.i(TAG, "in RetrieveData, response from DataRetriever came back null, data has been successfully loaded");

            // TEST:
            Model model = Model.getInstance();

            listener.responseMessage("Connected: " + model.getUserPerson().getFirstName() + " " +
                                        model.getUserPerson().getLastName(), false);
        }
    }
}