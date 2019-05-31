package handler;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import response.Response;
import services.PersonService;


public class PersonHandler implements HttpHandler{

    public PersonHandler(){}

    @Override
    public void handle(HttpExchange exchange) throws IOException{

        boolean success = false;

        System.out.println(LocalTime.now() + " PersonHandler: context \"/person...\" called ");

        try{
            if(exchange.getRequestMethod().toLowerCase().equals("get")){

                boolean getSinglePerson = false;
                String person_id = null;

                // Parse URL path (and get person_id if needed)
                UrlParser url_parser = new UrlParser();
                String[] commands = url_parser.parseIdUrl(exchange.getRequestURI().getPath());
                if(commands.length >= 2) {
                    getSinglePerson = true;
                    person_id = commands[1];
                }

                // Retrieve token
                String token = "";
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    token = reqHeaders.getFirst("Authorization");
                }

                // Call person service
                PersonService person_service = new PersonService();
                Response response = null;

                if(getSinglePerson){
                    response = person_service.getPerson(token, person_id);
                }
                else{
                    response = person_service.getPersonAll(token);
                }

                // Send response back
                System.out.println(LocalTime.now() + " PersonHandler: sending response...");

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                Gson gson = new Gson();
                String response_json = gson.toJson(response);

                StreamHandler stream_handler = new StreamHandler();
                stream_handler.writeToOutputStream(response_json, exchange.getResponseBody());

                exchange.getResponseBody().close();

                success = true;
                System.out.println(LocalTime.now() + " PersonHandler: response successfully sent!");
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " PersonHandler: Error: " + e.toString());
            e.printStackTrace();

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
        }
    }
}
