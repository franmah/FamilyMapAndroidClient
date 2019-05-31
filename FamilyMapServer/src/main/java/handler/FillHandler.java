package handler;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import response.ErrorResponse;
import response.Response;
import services.FillService;


public class FillHandler implements HttpHandler{

    public FillHandler(){}

    private int DEFAULT_NUM_GENERATION = 4;

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        System.out.println(LocalTime.now() + " FillHandler: context \"/fill/...\" called");

        boolean success = false;

        try{
            if(exchange.getRequestMethod().toLowerCase().equals("post")) {

                // Get user_name and num_generations from URL
                String user_name = null;
                int num_generations = DEFAULT_NUM_GENERATION;

                UrlParser url_parser = new UrlParser();
                String[] commands = url_parser.parseFillUrl(exchange.getRequestURI().getPath());

                user_name = commands[1];
                if(commands.length == 3) { num_generations = Integer.parseInt(commands[2]); }
                System.out.println(user_name + num_generations);


                // Call FillService
                System.out.println(LocalTime.now() + " FillHandler: calling FillService...");
                FillService fill_service = new FillService();
                Response response = fill_service.fillUserTree(user_name, num_generations);

                // Return response
                System.out.println(LocalTime.now() + " FillHandler: sending response...");
                Gson gson = new Gson();
                String response_json = gson.toJson(response);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                StreamHandler stream_handler = new StreamHandler();
                stream_handler.writeToOutputStream(response_json, exchange.getResponseBody());
                exchange.getResponseBody().close();

                success = true;

                System.out.println(LocalTime.now() + " FillHandler: response successfully sent");

            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }

        }
        catch(Exception e){
            System.out.println(LocalTime.now() + " FillHandler: Error: " + e.toString());
            e.printStackTrace();

            // Return error message
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);

            Gson gson = new Gson();
            String response_json = gson.toJson(new ErrorResponse(e.toString()));

            StreamHandler stream_handler = new StreamHandler();
            stream_handler.writeToOutputStream(response_json, exchange.getResponseBody());


            exchange.getResponseBody().close();
        }

    }

}
