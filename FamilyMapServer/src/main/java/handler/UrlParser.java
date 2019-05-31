package handler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlParser {


    public UrlParser(){}

    public String[] parseFillUrl(String url_path) throws Exception {

        System.out.println(LocalTime.now() + " UrlParser.parseFillUrl(): URI url_path: " + url_path);

        // Parse the URL (first command is always the name of the service/handler)
        url_path = url_path.substring(1); // Remove the first "/"

        String[] commands = url_path.split("/");
        System.out.println(LocalTime.now() + " UrlParser.parseFillUrl():  commands: " + Arrays.toString(commands));

        if (commands.length < 2) {
            System.out.println(LocalTime.now() + " UrlParser.parseFillUrl: too many arguments");
            throw new Exception("Wrong number of arguments");
        }

        // Check if num_generation is an Int
        if(commands.length == 3) {
            try {
                int num_generation = Integer.parseInt(commands[2]);
            } catch (Exception e) {
                System.out.println(LocalTime.now() + " UrlParser.parseFillUrl: error: " + e.toString());
                e.printStackTrace();
                throw new Exception("second argument (number of generations) should be a number");
            }
        }

        return commands;
    }

    public String[] parseIdUrl(String url_path) throws  Exception{
        System.out.println(LocalTime.now() + " UrlParser.parseIdUrl(): URI url_path: " + url_path);

        // Parse the URL (first command is always the name of the service/handler)
        url_path = url_path.substring(1); // Remove the first "/"

        String[] commands = url_path.split("/");
        System.out.println(LocalTime.now() + " UrlParser.parseFillUrl():  commands: " + Arrays.toString(commands));

        return commands;
    }
}
