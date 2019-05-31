package services;

import java.io.File;
import java.io.FileReader;
import java.time.LocalTime;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.util.Calendar;
import java.util.Random;
import dao.DataBaseException;
import dao.OperationDAO;
import dao.PersonDAO;
import response.Response;
import response.SuccessResponse;
import response.ErrorResponse;
import models.*;
import java.lang.*;

/**
 * Generate a user's family tree for a given number of generation
 * Handle DataBaseException.
 */
public class FillService{

    private OperationDAO db = null;
    int num_events = 0;
    int num_person = 0;

    // rand is static so no number is generated twice.
    private static Random rand = new Random();

    private final static String json_path = "C:\\Users\\Francois\\AndroidStudioProjects\\FamilyMap\\FamilyMapServer\\src\\main\\java\\jsonFiles\\";
    private final static String location_path = json_path + "locations.json" ;
    private final static String fnames_path = json_path + "fnames.json";
    private final static String mnames_path = json_path + "mnames.json";
    private final static String snames_path = json_path + "snames.json";

    private static Locations locations = null;
    private static FemaleNames female_names = null;
    private static MalesNames males_names = null;
    private static FamilyNames last_names = null;

    /***** JSON CLASSES ****/
    private class Locations {
        private Location[] data;

        public Locations(Location[] data) {
            this.data = data;
        }

        public Location[] getData() {
            return data;
        }

        private class Location {
            private String country;
            private String city;
            private float latitude;
            private float longitude;

            public Location(String country, String city, float latitude, float longitude) {
                this.country = country;
                this.city = city;
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public String getCountry() {
                return country;
            }

            public String getCity() {
                return city;
            }

            public float getLatitude() {
                return latitude;
            }

            public float getLongitude() {
                return longitude;
            }
        }
    }
    private class FemaleNames{
        private String[] data = null;

        public FemaleNames(String[] data) {
            this.data = data;
        }

        public String[] getData() {
            return data;
        }
    }
    private class MalesNames{
        private String[] data = null;

        public String[] getData() {
            return data;
        }

        public MalesNames(String[] data) {
            this.data = data;
        }
    }
    private class FamilyNames{
        private String[] data = null;

        public String[] getData() {
            return data;
        }

        public FamilyNames(String[] data) {
            this.data = data;
        }
    }

    public FillService(){}

    /** Fill the class object using the json files in /jsonFiles/
     *
     */
    static {
        File location_file = new File(location_path);
        File fnames_file = new File(fnames_path);
        File mnames_file = new File(mnames_path);
        File snames_file = new File(snames_path);

        System.out.println(location_path);

        if(!location_file.exists() || !fnames_file.exists() ||
            !mnames_file.exists() || !snames_file.exists()){
            System.out.println(LocalTime.now() + " FillService.fillGeneratorObjects: Error json files not found");
        }

        try {
            Gson gson = new Gson();

            JsonReader jsonReader = new JsonReader(new FileReader(location_file));
            locations = gson.fromJson(jsonReader, Locations.class);

            jsonReader = new JsonReader(new FileReader(fnames_file));
            female_names = gson.fromJson(jsonReader, FemaleNames.class);

            jsonReader = new JsonReader(new FileReader(mnames_file));
            males_names = gson.fromJson(jsonReader, MalesNames.class);

            jsonReader = new JsonReader(new FileReader(snames_file));
            last_names = gson.fromJson(jsonReader, FamilyNames.class);
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " FillService.fillGeneratorObjects: Error: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Method called by other classes.
     * Find a user, it's person and generate his family tree.
     *
     * @param   user_name: the user.
     * @param   num_generations: number of generations to create.
     * @return  SuccessResponse or ErrorResponse with an explanation message.
     */
    public Response fillUserTree(String user_name, int num_generations){
        if(user_name == null || num_generations < 0) {
            System.out.println(LocalTime.now() + " FillService.fillUserTree(): Error: user_name null or num_generation < 0.");
            return new ErrorResponse("Wrong information");
        }

        // Check if the static call to load objects from json files worked.
        if(locations == null || female_names == null || males_names == null
            || last_names == null){
            System.out.println(LocalTime.now() + " FillService.fillUserTree(): " +
                            "Error: json files for locations and names not loaded into objects.");

            return new ErrorResponse("Internal Error: unable to generate family tree.");
        }

        num_events = 0;
        num_person = 1; // starts at 1: the user itself

        boolean commit = false;

        try {
            db = new OperationDAO();

            // Check if user is registered, if he is: erase his data, else: return an error.
            User user = db.getUser_dao().getUser(user_name);
            if (user == null) {
                System.out.println(LocalTime.now() + " FillService.fillUserTree(): User \"" + user_name + "\" is not registered.");
                return new ErrorResponse("User not registered");
            }
            else{
                System.out.println("FillService.fillUserTree(): a user \"" + user_name + "\" has been found, deleting user's previous data...");
                renewUserInfo(user);
            }

            // Generate birth for the user. Ancestor's events will be created based on this event's year.
            generateUserBirth(user);
            num_events++;

            System.out.println(LocalTime.now() + " FillService.fillUserTree(): generating family tree...");
            Person user_person = generateTree(db.getPerson_dao().getPerson(user.getPersonId(), user.getUserName()),
                                            user_name, num_generations, 1);

            // Update user with it's new parents
            db.getPerson_dao().updatePersonParents(user_person);

            commit = true;

            return new SuccessResponse("Successfully generated " + num_person + " people and " + num_events + " event(s)");
        }
        catch (DataBaseException message){
            commit = false;
            System.out.println(LocalTime.now() + " FillService.fillUserTree(): Error: " + message.toString());
            return new ErrorResponse(message.toString());
        }
        catch (Exception e){
            commit = false;
            System.out.println(LocalTime.now() + " FillService.fillUserTree(): Error: " + e.toString());
            e.printStackTrace();
            return new ErrorResponse("Internal Error while trying to generate family tree.");
        }
        finally {
                db.commitAndCloseConnection(commit);
        }
    }

    /**
     * fillUserTree helper.
     * Recursively generate random family tree.
     *
     * @param   child: person who's parents will be generated and then attached to.
     * @param   num_generations: number of generations to generate.
     * @param   current_generation: number giving the generation it the method is creating.
     */
    private Person generateTree(Person child, String user_name, int num_generations, int current_generation) throws DataBaseException{
        if(current_generation > num_generations){
            System.out.println(LocalTime.now() + " FillService.generateTree(): returning");
            return null;
        }

        System.out.println(LocalTime.now() + " FillService.generateTree(): generating generation #" + current_generation + "/" + num_generations);

        try {
            PersonDAO person_dao = db.getPerson_dao();

            // Generate mother ( + birth + random event)
            Person mother = generatePerson(user_name, "f");
            num_person++;
            System.out.println(LocalTime.now() + " FillService.generateTree(): Person generated: " + mother.toString());

            generateBirth(child, mother.getPersonId(), user_name);
            num_events++;
            System.out.println(LocalTime.now() + " FillService.generateTree(): birth successfully generated for the mother");

            generateRandomEvent(mother.getPersonId(), user_name);
            num_events++;
            System.out.println(LocalTime.now() + " FillService.generateTree(): Random event successfully generated for the mother");

            // recursive call
            generateTree(mother, user_name, num_generations, ++current_generation);

            System.out.println(LocalTime.now() + " FillService.generateTree(): generating generation #" + current_generation + "/" + num_generations);

            // Generate the dad (birth + random event)
            Person father = generatePerson(user_name, "m");
            num_person++;
            System.out.println(LocalTime.now() + " FillService.generateTree(): Person generated: " + father.toString());

            generateBirth(child, father.getPersonId(), user_name);
            num_events++;
            System.out.println(LocalTime.now() + " FillService.generateTree(): birth successfully generated for the father");


            generateRandomEvent(father.getPersonId(), user_name);
            num_events++;
            System.out.println(LocalTime.now() + " FillService.generateTree(): Random event successfully generated for the father");


            // Generate marriage event for father and mother
            generateMarriage(mother, father);
            System.out.println(LocalTime.now() + " FillService.generateTree(): successfully generated marriage events");
            num_events += 2;

            mother.setSpouseId(father.getPersonId());
            father.setSpouseId(mother.getPersonId());

            // recursive call
            generateTree(father, user_name, num_generations, current_generation);

            // Add the two Person in the database.
            person_dao.addPerson(mother);
            person_dao.addPerson(father);

            // Update child
            child.setMotherId(mother.getPersonId());
            child.setFatherId(father.getPersonId());

            System.out.println(LocalTime.now() + " FillService.generateTree(): returning");
            return child;
        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " FillService.fillUserTree(): Error: " + message.toString());
            throw new DataBaseException(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " FillService.fillUserTree(): Error: " + e.toString());
            e.printStackTrace();
            throw new DataBaseException("Internal error while generating family tree");
        }

    }


    /** Generate a random Person. !! Does not generate father/mother/spouse id.
     * @param user_name associated user name.
     * @param gender
     */
    private Person generatePerson(String user_name, String gender){
        assert gender.length() == 1;

        String last_name = last_names.getData()[Math.abs(rand.nextInt() % last_names.getData().length)];
        String first_name;
        if(gender.equals("f")){
            first_name = female_names.getData()[Math.abs(rand.nextInt() % female_names.getData().length)];
        }
        else{
            first_name = female_names.getData()[Math.abs(rand.nextInt() % males_names.getData().length)];
        }

        return new Person(user_name, first_name, last_name, gender);
    }

    /** Marry two person (plus pick a random date and location, both persons will have the same date)
     *
     * @param wife
     * @param husband
     */
    private void generateMarriage(Person wife, Person husband){
        System.out.println(LocalTime.now() + " FillService.generateMarriage(): generating marriage... ");

        try{
            Event wife_birth = db.getEvent_dao().getBirthEvent(wife.getPersonId(), wife.getAssociatedUsername());
            Event husband_birth = db.getEvent_dao().getBirthEvent(husband.getPersonId(), husband.getAssociatedUsername());

            // Find youngest person
            int base_year = husband_birth.getYear();
            if(wife_birth.getYear() > husband_birth.getYear()){
                base_year = wife_birth.getYear();
            }

            // Generate year
            int year = base_year + (Math.abs(rand.nextInt() % 60));
            // 60 so that the event happens before death.

            // Generate location
            int location_index = Math.abs(rand.nextInt() % locations.getData().length);
            Locations.Location location_tmp = locations.getData()[location_index];

            db.getEvent_dao().addEvent(new Event(wife.getAssociatedUsername(), wife.getPersonId(), location_tmp.getLatitude(), location_tmp.getLongitude(),
                    location_tmp.getCountry(), location_tmp.city, "marriage", year));
            db.getEvent_dao().addEvent(new Event(husband.getAssociatedUsername(), husband.getPersonId(), location_tmp.getLatitude(), location_tmp.getLongitude(),
                    location_tmp.getCountry(), location_tmp.city, "marriage", year));
        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " FillService.generateMarriage(): Error: " + message.toString());
            throw new DataBaseException(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " FillService.generateMarriage(): Error: " + e.toString());
            e.printStackTrace();
            throw new DataBaseException("Internal error while generating marriage event");
        }
    }


    /** Generate a random event from a list of possible event.
     *  The event happens within 60 years of the year of birth of the person.
     * @param person_id
     * @param user_name
     */
    private void generateRandomEvent(String person_id, String user_name){
        System.out.println(LocalTime.now() + " FillService.generateRandomEvent():generating random event...");

        String[] event_types = {"bar mitzvah", "baptism", "graduation", "retirement", "first job"};

        try {
            int type_index = Math.abs(rand.nextInt() % event_types.length);

            // Generate year for event
            Event event = db.getEvent_dao().getBirthEvent(person_id, user_name);
            int year = event.getYear() + (Math.abs(rand.nextInt() % 60));
            // 60 so that the event happens before death.

            // Find random location
            int location_index = Math.abs(rand.nextInt() % locations.getData().length);
            Locations.Location location_tmp = locations.getData()[location_index];

            db.getEvent_dao().addEvent(new Event(user_name, person_id, location_tmp.latitude, location_tmp.longitude,
                    location_tmp.country, location_tmp.city, event_types[type_index], year));
        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " FillService.generateRandomEvent(): Error: " + message.toString());
            throw new DataBaseException(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " FillService.generateRandomEvent(): Error: " + e.toString());
            e.printStackTrace();
            throw new DataBaseException("Internal error while generating random event");
        }
    }


    /** Generate a birth event using the child'own birthdate.
     *
     * @param child
     * @param person_id
     * @param user_name
     */
    private void generateBirth(Person child, String person_id, String user_name){
        System.out.println(LocalTime.now() + " FillService.generateBirth(): generating birth...");

        assert  child != null;

        try {
            // Find child's birth date.
            Event child_birth = db.getEvent_dao().getBirthEvent(child.getPersonId(), user_name);

            int year = child_birth.getYear() - ((Math.abs(rand.nextInt()) % 30) + 16);
            // 30 and 16 to have a relatively possible age of birth compared to the child.

            // Find random location
            int location_index = Math.abs(rand.nextInt() % locations.getData().length);
            Locations.Location location_tmp = locations.getData()[location_index];

            db.getEvent_dao().addEvent(new Event(user_name, person_id, location_tmp.latitude, location_tmp.longitude,
                                location_tmp.country, location_tmp.city, "birth", year));
        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " FillService.generateBirth(): Error: " + message.toString());
            throw new DataBaseException(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " FillService.generateBirth(): Error: " + e.toString());
            e.printStackTrace();
            throw new DataBaseException("Internal error while generating birth event");
        }
    }


    /** Generate birth event for the user, using the current year.
     *
     * @param user Generate event for this user.
     */
    private void generateUserBirth(User user){
        assert user != null;
        System.out.println(LocalTime.now() + " FillService.generateUserBirth: generating user birth...." );

        try{
            final int MAX_AGE = 90; // Range in which the random number can be.

            // Generate year using current date
            Calendar calendar = Calendar.getInstance();   // Gets the current date and time
            int year = calendar.get(Calendar.YEAR) - (Math.abs(rand.nextInt() % MAX_AGE));

            // Find random location
            int location_index = Math.abs(rand.nextInt() % locations.getData().length);
            Locations.Location location_tmp = locations.getData()[location_index];

            db.getEvent_dao().addEvent(new Event(user.getUserName(), user.getPersonId(), location_tmp.getLatitude(),
                    location_tmp.getLongitude(), location_tmp.getCountry(), location_tmp.city, "birth", year));
        }
        catch (DataBaseException message){
            System.out.println(LocalTime.now() + " FillService.generateUserBirth(): Error: " + message.toString());
            throw new DataBaseException(message.toString());
        }
        catch (Exception e){
            System.out.println(LocalTime.now() + " FillService.generateUserBirth(): Error: " + e.toString());
            e.printStackTrace();
            throw new DataBaseException("Internal error while generating user's birth event");
        }
    }

    /** Delete data related to user (Persons and Events) and recreate a new person for tha user, using the same person id.
     *
     * @param user
     */
    private void renewUserInfo(User user){
        assert db != null;

        // Save user's person object
        Person person = db.getPerson_dao().getPerson(user.getPersonId(), user.getUserName());

        if(person == null){
            // If no person related to the user : create a new person for user:
            person = new Person(user.getPersonId(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getGender());
        }

        // Delete any previous data related to the user
        db.getPerson_dao().deleteUserFamily(user.getUserName());
        db.getEvent_dao().deleteUserFamilyEvents(user.getUserName());

        // Add the user's person object back into the database
        db.getPerson_dao().addPerson(person);

    }
}