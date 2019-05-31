package handler;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import request.LoadRequest;
import response.Response;
import services.LoadService;


public class LoadHandler implements HttpHandler{

    public LoadHandler(){}

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        System.out.println(LocalTime.now() + " LoadHandler: context \"/load/...\" called");

        boolean success = false;

        try{
            if(exchange.getRequestMethod().toLowerCase().equals("post")) {
                StreamHandler stream_handler = new StreamHandler();
                Gson gson = new Gson();

                // Create LoadRequest
                String data_json = stream_handler.readInputStream(exchange.getRequestBody());
                LoadRequest load_request = gson.fromJson(data_json, LoadRequest.class);

                // Call LoadService
                LoadService load_service = new LoadService();
                Response response = load_service.loadDataBase(load_request);

                // Return response
                System.out.println(LocalTime.now() + " LoadHandler: sending response...");
                String response_json = gson.toJson(response);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                stream_handler.writeToOutputStream(response_json, exchange.getResponseBody());
                exchange.getResponseBody().close();

                success = true;

                System.out.println(LocalTime.now() + " LoadHandler: response successfully sent");

            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }

        }
        catch(Exception e){
            System.out.println(LocalTime.now() + " LoadHandler: Error: " + e.toString());
            e.printStackTrace();

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
        }
    }
}
