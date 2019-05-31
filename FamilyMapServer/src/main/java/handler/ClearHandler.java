package handler;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import response.Response;
import services.ClearService;


public class ClearHandler implements HttpHandler{

    public ClearHandler(){}

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        boolean success = false;

        System.out.println(LocalTime.now() + " ClearHandler: context \"/clear\" called");

        try{
            if(exchange.getRequestMethod().toLowerCase().equals("post")){

                System.out.println(LocalTime.now() + " ClearHandler: calling ClearService...");
                ClearService clear_service = new ClearService();
                Response response = clear_service.clearDataBase();

                // Turn response into Json
                Gson gson = new Gson();
                String response_json = gson.toJson(response);

                System.out.println(LocalTime.now() + " ClearHandler: sending response...");
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                //Write json into OutputStream
                StreamHandler stream_handler = new StreamHandler();
                stream_handler.writeToOutputStream(response_json, exchange.getResponseBody());

                exchange.getResponseBody().close();

                success = true;
                System.out.println(LocalTime.now() + " ClearHandler: response successfully sent!");
            }

            if(!success){
                System.out.println(LocalTime.now() + " ClearHandler: Bad Request");
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " ClearHandler: Error: " + e.toString());
            e.printStackTrace();

            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }
}
