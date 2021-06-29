package Model;

import Controller.AppointmentController;
import Database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import view.CustomersView;

import javax.xml.transform.Result;
import java.sql.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class GetData {
    //Obtaining and setting all the Divisions in the DivisionList array
    static private ArrayList<String> divisionList;

    //Obtaining and setting all the Country names in the CountryList Array
    static private ArrayList<String> countryList;

    public static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    public static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    //get data for Customers Display
    public static ObservableList<Customer> getCustomers(){
        try{
            allCustomers.clear();
            Statement statement = DBConnection.getConnection().createStatement();
            String customerQuery ="select Customer_ID, Customer_Name, Address, Postal_code, Phone, Division_ID FROM customers";
            ResultSet customerResults = statement.executeQuery(customerQuery);

            //to prevent displaying of same duplicate data every time the 'Customers' Button is clicked.
            //prevents adding the same data into the ObservableList 'allCustomers'


            while(customerResults.next()) {
                int customerDivisionId = customerResults.getInt("Division_ID");

                PreparedStatement divisionStatement = DBConnection.getConnection().prepareStatement("SELECT Division from first_level_divisions WHERE Division_ID = ?");
                divisionStatement.setInt(1, customerDivisionId);
                ResultSet customerDivisionResultSet = divisionStatement.executeQuery();

                String customerDivision = "";
                while(customerDivisionResultSet.next()){
                    customerDivision = customerDivisionResultSet.getString("Division");
                }
                Customer customer = new Customer(customerResults.getInt("Customer_ID"), customerResults.getString("Customer_Name"),
                        customerResults.getString("Address"), customerResults.getString("Postal_code"), customerResults.getString("Phone"),
                        customerDivision);
                allCustomers.add(customer);
            }
        } catch (Exception e){
            System.out.println("Exception caught");
        }
        return GetData.allCustomers;
    }

    public static ArrayList<String> getDivisionList(String country){
        divisionList = new ArrayList<>();
        try{
            String query = "SELECT Division from first_level_divisions WHERE COUNTRY_ID = (SELECT Country_ID FROM countries WHERE Country = ?)";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setString(1, country);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                divisionList.add(resultSet.getString("Division"));
            }
        } catch (Exception e){
            System.out.println("Exception caught when getting Divisions List");
        }
        return divisionList;
    }

    public static ArrayList<String> getCountryList(){
        countryList = new ArrayList<>();
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT Country from countries";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                countryList.add(resultSet.getString("Country"));
            }
        } catch (Exception e){
            System.out.println("Exception caught when getting Country List");
        }
        return countryList;
    }
    public static int getDivisionId(String division){

        try{
            int division_id = 0;
            String query = "SELECT Division_ID from first_level_divisions WHERE Division = ?";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setString(1, division);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                division_id = rs.getInt("Division_ID");
            }
            return division_id;
        } catch (Exception e){
            System.out.println("Exception caught when getting division id.");
            return 0;
        }
    }

    public static String getCountry(String division){
        String country = "";
        try{
            String query = "select Country FROM countries WHERE Country_ID = (SELECT COUNTRY_ID FROM first_level_divisions WHERE Division = ?)";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setString(1, division);
            ResultSet resultSet= preparedStatement.executeQuery();
            while(resultSet.next()){
                country = resultSet.getString("Country");
            }
        } catch (Exception e){
            System.out.println("Exception caught when getting country from division!");
        }
        return country;
    }

    public static ArrayList<String> getAppointmentTimes(){
        ArrayList<String> times = new ArrayList<>();
        int startTime = 0;
        String startString = "";
        String endString = "";
        while(true){
            startString = "";
            endString = "";
            if(startTime == 24){
                break;
            }
            if(startTime <= 9){
                startString = "0" + startTime;

            }
            else{
                startString = String.valueOf(startTime);
            }
            endString = startString + " : 59";
            times.add(startString + " : 00" + " - " + endString);
            startTime++;
        }
        return times;
    }


    public static ArrayList<String> getCustomersName(){
        ArrayList<String> names = new ArrayList<>();
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Customer_Name FROM customers");
            while(resultSet.next()){
                names.add(resultSet.getString("Customer_Name"));
            }
        } catch (Exception e){
            System.out.println("Exception caught when getting Customers Name");
        }
        return names;
    }


    /*public static String getTimeZone(){
        Calendar now = Calendar.getInstance();
        TimeZone timeZone = now.getTimeZone();
        //(true, 0) gets the shorter version of timezone.
        return timeZone.getDisplayName(true, 0);
    }*/

    public static void timeConversion(){
        LocalDate datePicker = LocalDate.of(2020, 8, 3);
        LocalTime timeComboBox = LocalTime.of(11, 0);  //Get from combobox
        ZoneId localZoneId = ZoneId.systemDefault();
        ZonedDateTime localZDT =  ZonedDateTime.of(datePicker, timeComboBox, localZoneId); //creates ZonedDateTime
        System.out.println("Local: " + localZDT.toLocalTime());

        //Converting Local to EST
        ZoneId estZoneId = ZoneId.of("America/New_York");
        ZonedDateTime estZDT = ZonedDateTime.ofInstant(localZDT.toInstant(), estZoneId);
        System.out.println("EST: " + estZDT.toLocalTime()); //EST Business Hours check

        //Convert Local to UTC (if using setString)
        ZoneId utcZID = ZoneId.of("UTC");
        ZonedDateTime utcZDT = ZonedDateTime.ofInstant(localZDT.toInstant(), utcZID);
        System.out.println("UTC: " + utcZDT.toLocalTime());
    }

    public static ZonedDateTime convertTimeTo(ZonedDateTime zdt, String zone){
        ZoneId zId = ZoneId.of(zone);
        return ZonedDateTime.ofInstant(zdt.toInstant(), zId);
    }

    public static ArrayList<String> getContacts(){
        ArrayList<String> contacts = new ArrayList<String>();
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Contact_Name from contacts");
            while(resultSet.next()){
                contacts.add(resultSet.getString("Contact_Name"));
            }
        } catch (Exception e){
            System.out.println("Exception caught when getting contacts!");
        }
        return contacts;
    }

    public static ArrayList<String> getUserNames(){
        ArrayList<String> userNames = new ArrayList<String>();
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT User_Name from users");
            while(resultSet.next()){
                userNames.add(resultSet.getString("User_Name"));
            }
        } catch (Exception e){
            System.out.println("Exception caught when getting User Names!");
        }
        return userNames;
    }


    //getting all appointments
    public static ObservableList<Appointment> getAppointments(String query){
        try{
            allAppointments.clear();
            Statement statement = DBConnection.getConnection().createStatement();
            String appointmentQuery = query;
            ResultSet appointmentResults = statement.executeQuery(appointmentQuery);

            while(appointmentResults.next()) {
                int contactId = appointmentResults.getInt("Contact_ID");

                PreparedStatement contactStatement = DBConnection.getConnection().prepareStatement("SELECT Contact_Name from contacts WHERE Contact_ID = ?");
                contactStatement.setInt(1, contactId);
                ResultSet appointmentResultSet = contactStatement.executeQuery();

                String contactName = "";
                while(appointmentResultSet.next()){
                    contactName = appointmentResultSet.getString("Contact_Name");
                }
                //converting to LOCAL TIME FROM UTC for displaying purpose
                String formattedStartTime = AppointmentController.zoneIdToLocal(appointmentResults.getString("Start"), "UTC");
                String formattedEndTime = AppointmentController.zoneIdToLocal(appointmentResults.getString("End"), "UTC");

                Appointment appointment = new Appointment(appointmentResults.getInt("Appointment_ID"), appointmentResults.getString("Title"),
                        appointmentResults.getString("Description"), appointmentResults.getString("Location"),
                        appointmentResults.getString("Type"), formattedStartTime, formattedEndTime,
                        appointmentResults.getInt("Customer_ID"), appointmentResults.getInt("User_ID"), contactName);
                allAppointments.add(appointment);
            }
        } catch (Exception e){
            System.out.println("Exception caught when getting all Appointments.");
        }
        return GetData.allAppointments;
    }

    public static int getContactId(String contactName){
        int result = 0;
        try{
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement("SELECT Contact_ID FROM contacts WHERE Contact_Name = ?");
            preparedStatement.setString(1, contactName);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                result = rs.getInt("Contact_ID");
            }
        } catch (SQLException e){
            System.out.println("Error fetching ContactID");
        }
        return result;
    }

    public static String getContactName(int contactId){
        String result = "";
        try{
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement("SELECT Contact_Name FROM contacts WHERE Contact_ID = ?");
            preparedStatement.setInt(1, contactId);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                result = rs.getString("Contact_Name");
            }
        } catch (SQLException e){
            System.out.println("Error fetching Contact Name");
        }
        return result;
    }
    public static int getCustomerId(String customerName){
        int result = 0;
        try{
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement("SELECT Customer_ID FROM customers WHERE Customer_Name = ?");
            preparedStatement.setString(1, customerName);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                result = rs.getInt("Customer_ID");
            }
        } catch (SQLException e){
            System.out.println("Error fetching customerID");
        }
        return result;
    }

    public static int getUserId(String userName){
        int result = 0;
        try{
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement("SELECT User_ID FROM users WHERE User_Name = ?");
            preparedStatement.setString(1, userName);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                result = rs.getInt("User_ID");
            }
        } catch (SQLException e){
            System.out.println("Error fetching userID");
        }
        return result;
    }

    public static String getCustomerName(int customerId){
        String name = "";
        try{
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement("SELECT Customer_Name FROM customers WHERE Customer_ID = ?");
            preparedStatement.setInt(1, customerId);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                name = rs.getString("Customer_Name");
            }
        } catch (SQLException e){
            System.out.println("Error fetching customerName");
        }
        return name;
    }

    public static String getUserName(int userId){
        String name = "";
        try{
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement("SELECT User_Name FROM users WHERE User_Id = ?");
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                name = rs.getString("User_Name");
            }
        } catch (SQLException e){
            System.out.println("Error fetching userName");
        }
        return name;
    }

    public static ObservableList<Appointment> getMonthlyAppointments(){
        ObservableList<Appointment> list = FXCollections.observableArrayList();
        ZonedDateTime localZdt = getStartOfMonthZdt();
        System.out.println("ZDT Local: " + localZdt);
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM appointments");
            while(resultSet.next()){
                ZonedDateTime currentStartAppointmentZdt = AppointmentController.stringToSystemZdt(resultSet.getString("Start"), "UTC");
                ZonedDateTime currentEndAppointmentZdt = AppointmentController.stringToSystemZdt(resultSet.getString("End"), "UTC");
                if(localZdt.getMonth() == currentStartAppointmentZdt.getMonth()){
                    Appointment appointment = new Appointment(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"),
                            resultSet.getString("Description"), resultSet.getString("Location"),
                            resultSet.getString("Type"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentStartAppointmentZdt),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentEndAppointmentZdt),
                            resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), GetData.getContactName(resultSet.getInt("Contact_ID")));
                    list.add(appointment);
                }
            }
        }
        catch (Exception e){
            System.out.println("Error fetching Monthly Appointments!");
        }
        return list;
    }

    public static ObservableList<Appointment> getWeeklyAppointments(){
        ObservableList<Appointment> list = FXCollections.observableArrayList();
        Instant instant = Instant.now();
        ZonedDateTime zDt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM appointments");
            while(resultSet.next()){
                ZonedDateTime currentStartAppointmentZdt = AppointmentController.stringToSystemZdt(resultSet.getString("Start"), "UTC");
                ZonedDateTime currentEndAppointmentZdt = AppointmentController.stringToSystemZdt(resultSet.getString("End"), "UTC");
                for(int i = 1; i<8; i++){
                    if(zDt.plusDays(i).toLocalDate().isEqual(currentStartAppointmentZdt.toLocalDate())){
                        System.out.println("Local Date: " + zDt.plusDays(i).toLocalDate() + "    Database: " + currentStartAppointmentZdt.toLocalDate());
                        Appointment appointment = new Appointment(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"),
                                resultSet.getString("Description"), resultSet.getString("Location"),
                                resultSet.getString("Type"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentStartAppointmentZdt),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentEndAppointmentZdt),
                                resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), GetData.getContactName(resultSet.getInt("Contact_ID")));
                        list.add(appointment);
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("Error fetching Weekly Appointments!");
        }
        return list;
    }

    public static ZonedDateTime getStartOfMonthZdt(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime localZdt = AppointmentController.stringToSystemZdt(zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.systemDefault().toString());
        return localZdt;
    }


    public static String getAppointmentbyTypeMonth(){
        String result = "";
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MONTHNAME(Start) AS Month, type AS Appointment_Type, COUNT(*) AS NUMBER " +
                    "FROM appointments GROUP BY Month, Appointment_Type");
            while (resultSet.next()) {
                result += resultSet.getString("Month") + "     " + resultSet.getString("Appointment_Type") + "       " +
                        resultSet.getInt("Number") + "\n" + "\n";
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception caught when getting appointment by TypeMonth");
        }
        return result;
    }

    public static ObservableList<Appointment> getAppointmentsByContact(String contactName){
        ObservableList<Appointment> list = FXCollections.observableArrayList();
        try{
            String query = "SELECT * FROM appointments WHERE Contact_ID = ?";
            PreparedStatement preparedStatement= DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, GetData.getContactId(contactName));
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                ZonedDateTime currentStartAppointmentZdt = AppointmentController.stringToSystemZdt(resultSet.getString("Start"), "UTC");
                ZonedDateTime currentEndAppointmentZdt = AppointmentController.stringToSystemZdt(resultSet.getString("End"), "UTC");
                    Appointment appointment = new Appointment(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"),
                            resultSet.getString("Description"), resultSet.getString("Location"),
                            resultSet.getString("Type"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentStartAppointmentZdt),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentEndAppointmentZdt),
                            resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), GetData.getContactName(resultSet.getInt("Contact_ID")));
                    list.add(appointment);
                }
            }
        catch (Exception e){
            System.out.println("Error fetching Monthly Appointments!");
        }
        return list;
    }

    public static ArrayList<String> getLocation(){
        ArrayList<String> locations = new ArrayList<String>();
        String currentLocation = "";
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Location from appointments");
            while(resultSet.next()){
                currentLocation = resultSet.getString("Location");
                if(!locations.contains(currentLocation)){
                    locations.add(currentLocation);
                }
            }
        } catch (Exception e){
            System.out.println("Exception caught when getting Location!");
        }
        return locations;
    }

    public static ObservableList<Appointment> getAppointmentsByLocation(String location){
        ObservableList<Appointment> list = FXCollections.observableArrayList();
        try{
            String query = "SELECT * FROM appointments WHERE Location = ?";
            PreparedStatement preparedStatement= DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setString(1, location);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                ZonedDateTime currentStartAppointmentZdt = AppointmentController.stringToSystemZdt(resultSet.getString("Start"), "UTC");
                ZonedDateTime currentEndAppointmentZdt = AppointmentController.stringToSystemZdt(resultSet.getString("End"), "UTC");
                Appointment appointment = new Appointment(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"),
                                resultSet.getString("Description"), resultSet.getString("Location"),
                                resultSet.getString("Type"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentStartAppointmentZdt),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentEndAppointmentZdt),
                                resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), GetData.getContactName(resultSet.getInt("Contact_ID")));
                list.add(appointment);
            }
        }
        catch (Exception e){
            System.out.println("Error fetching Appointments by Location!");
        }
        return list;
    }


}
