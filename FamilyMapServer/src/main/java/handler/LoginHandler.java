package handler;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import request.LoginRequest;
import response.Response;
import services.LoginService;


public class LoginHandler implements HttpHandler{

    public LoginHandler(){}

    @Override
    public void handle(HttpExchange exchange) throws IOException{


        boolean success = false;

        System.out.println(LocalTime.now() + " LoginHandler: context \"/user/login/\" called ");

        try{
            if(exchange.getRequestMethod().toLowerCase().equals("post")) {

                StreamHandler stream_handler = new StreamHandler();

                // Create LoginRequest
                String data_json = stream_handler.readInputStream(exchange.getRequestBody());

                Gson gson = new Gson();
                LoginRequest login_request = gson.fromJson(data_json, LoginRequest.class);

                // Call RegisterService
                System.out.println(LocalTime.now() + " LoginHandler: calling LoginService...");
                LoginService login_service = new LoginService();
                Response response = login_service.LoginUser(login_request);

                // Return response
                String response_json = gson.toJson(response);

                System.out.println(LocalTime.now() + " LoginHandler: sending response...");
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                stream_handler.writeToOutputStream(response_json, exchange.getResponseBody());
                exchange.getResponseBody().close();

                success = true;
                System.out.println(LocalTime.now() + " LoginHandler: response successfully sent!");
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " LoginHandler: Error: " + e.toString());
            e.printStackTrace();

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
        }
    }
}
