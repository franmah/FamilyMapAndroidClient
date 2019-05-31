package handler;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import request.RegisterRequest;
import response.Response;
import services.RegisterService;


public class RegisterHandler implements HttpHandler {

    public RegisterHandler(){}

    @Override
    public void handle(HttpExchange exchange) throws IOException{

        boolean success = false;

        System.out.println(LocalTime.now() + " RegisterHandler: context \"/user/register/\" called ");

        try{
            if(exchange.getRequestMethod().toLowerCase().equals("post")) {

                StreamHandler stream_handler = new StreamHandler();

                // Create RegisterRequest
                String data_json = stream_handler.readInputStream(exchange.getRequestBody());
                //System.out.println(LocalTime.now() + " RegisterHandler: Json string retrieved: " + data_json);
                Gson gson = new Gson();
                RegisterRequest register_request = gson.fromJson(data_json, RegisterRequest.class);
                //System.out.println(LocalTime.now() + " Json request received: " + register_request.toString());

                // Call RegisterService
                System.out.println(LocalTime.now() + " RegisterHandler: calling RegisterService...");
                RegisterService register_service = new RegisterService();
                Response response = register_service.registerNewUser(register_request);

                // Turn response into Json
                String response_json = gson.toJson(response);

                // Return response
                System.out.println(LocalTime.now() + " RegisterHandler: sending response...");
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                stream_handler.writeToOutputStream(response_json, exchange.getResponseBody());
                exchange.getResponseBody().close();

                success = true;
                System.out.println(LocalTime.now() + " RegisterHandler: response successfully sent!");
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " RegisterHandler: Error: " + e.toString());
            e.printStackTrace();

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
        }
    }

}
