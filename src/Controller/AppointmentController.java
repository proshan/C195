package Controller;

import Database.DBConnection;
import Model.GetData;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import view.*;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AppointmentController {

    private static ZonedDateTime startTimeLocalZDT;
    private static ZonedDateTime endTimeLocalZDT;
    public static String errorMessages = "";

    public static void addAppointment(){
        //For checking if any of the fields are empty.
        errorMessages = "";
        getErrorMessages(AddAppointmentView.titleField, AddAppointmentView.descriptionField, AddAppointmentView.locationField, AddAppointmentView.contactBox,
                AddAppointmentView.typeField, AddAppointmentView.datePicker, AddAppointmentView.timeBox, AddAppointmentView.customerBox, AddAppointmentView.userBox);
        checkValidDate(AddAppointmentView.datePicker);
        checkValidAppointmentTime(AddAppointmentView.datePicker, AddAppointmentView.timeBox, false);
        if(errorMessages.length() > 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(errorMessages);
            alert.showAndWait();
        }
        else{
            //converting the DateTime from comboBox to UTC and formatting to store in the database.
            String formattedStartTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(GetData.convertTimeTo(startTimeLocalZDT, "UTC"));
            String formattedEndTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(GetData.convertTimeTo(endTimeLocalZDT, "UTC"));

            //Now, storing the values to the Database
            try{
                String query = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, " +
                        "Customer_ID, User_ID, Contact_ID)" +
                        "VALUES(?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
                preparedStatement.setString(1, AddAppointmentView.titleField.getText());
                preparedStatement.setString(2, AddAppointmentView.descriptionField.getText());
                preparedStatement.setString(3, AddAppointmentView.locationField.getText());
                preparedStatement.setString(4, AddAppointmentView.typeField.getText());
                preparedStatement.setString(5, formattedStartTime);
                preparedStatement.setString(6, formattedEndTime);
                preparedStatement.setString(7, "roshan");
                preparedStatement.setString(8, "roshan");
                preparedStatement.setInt(9, GetData.getCustomerId(AddAppointmentView.customerBox.getSelectionModel().getSelectedItem().toString()));
                preparedStatement.setInt(10, GetData.getUserId(AddAppointmentView.userBox.getSelectionModel().getSelectedItem().toString()));
                preparedStatement.setInt(11, GetData.getContactId(AddAppointmentView.contactBox.getSelectionModel().getSelectedItem().toString()));
                if(preparedStatement.executeUpdate() > 0){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Successfully added appointment!");
                    alert.showAndWait();
                }
                AddAppointmentView.window.close();
                AppointmentMainView.getAppointmentMainView();
                AppointmentMainView.appointmentTableView.refresh();
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Couldn't add Appointment to the Database!");
                alert.showAndWait();
            }
        }
    }

    public static void getErrorMessages(TextField title, TextField description, TextField location, ComboBox<String> contact, TextField type, DatePicker datepicker,
                                        ComboBox<String> time, ComboBox<String> customer, ComboBox<String> user){
        //Validating the data Fields
        if(title.getText().trim().isEmpty()){
            errorMessages += "Title Field cannot be empty.\n";
        }
        if(description.getText().trim().isEmpty()){
            errorMessages += "Description Field cannot be empty.\n";
        }
        if(location.getText().trim().isEmpty()){
            errorMessages += "Location Field cannot be empty.\n";
        }
        if(contact.getSelectionModel().getSelectedIndex() == -1){
            errorMessages += "Contact Should be selected.\n";
        }
        if(type.getText().trim().isEmpty()){
            errorMessages += "Type field cannot be empty.\n";
        }
        if(datepicker.getValue() == null){
            errorMessages += "Date should be selected.\n";
        }
        if(time.getSelectionModel().getSelectedIndex() == -1){
            errorMessages += "Time Should be selected.\n";
        }
        if(customer.getValue() == null){
            errorMessages += "Customer should be selected.\n";
        }
        if(user.getValue() == null){
            errorMessages += "User should be selected.\n";
        }

    }

    public static void checkValidDate(DatePicker dp){
        try{
            LocalDate localDate = dp.getValue();
            if(localDate.compareTo(LocalDate.now()) < 0){
                errorMessages += "Invalid Date.\n";
            }
            else{
                //Checking if the date is weekend
                if(localDate.getDayOfWeek().toString().toLowerCase().equals("saturday") || localDate.getDayOfWeek().toString().toLowerCase().equals("sunday")){
                    errorMessages += "The selected date falls on Weekend. Please select a weekday.\n";
                }
            }

        } catch (NullPointerException e){
            System.out.println("Exception Caught checking Valid Date");
            errorMessages += "Date not valid.\n";
        }
    }

    public static void checkValidAppointmentTime(DatePicker dp, ComboBox<String> box, boolean unchanged){
        try{
            LocalDate datePicker = dp.getValue();
            LocalTime startTimeComboBox = LocalTime.of(getExtractedTime(box.getSelectionModel().getSelectedItem()), 0);//Get from combobox (start time)
            LocalTime endTimeComboBox = LocalTime.of(getExtractedTime(box.getSelectionModel().getSelectedItem()), 59); //(End time)
            ZoneId localZoneId = ZoneId.systemDefault();
            startTimeLocalZDT =  ZonedDateTime.of(datePicker, startTimeComboBox, localZoneId); //creates ZonedDateTime (for start time)
            endTimeLocalZDT = ZonedDateTime.of(datePicker, endTimeComboBox, localZoneId);
            //System.out.println("Local: " + localZDT.toLocalTime());
            //System.out.println(localZDT.getHour());

            //Converting the time to EST
            ZoneId estZoneId = ZoneId.of("America/New_York");
            ZonedDateTime estZDT = ZonedDateTime.ofInstant(startTimeLocalZDT.toInstant(), estZoneId);
            // System.out.println("EST: " + estZDT.toLocalTime()); //EST Business Hours check
            //System.out.println(estZDT.getHour());

            //checking to see if the est converted hour falls in the business hours.
            if(estZDT.isAfter(LocalDateTime.now().atZone(ZoneId.systemDefault()))){
                if(estZDT.getHour() <= 7 || estZDT.getHour() > 22){
                    if(errorMessages.contains("Invalid Date") || errorMessages.contains("on Weekend")){
                        errorMessages += "Please select a valid future date first.\n";
                    }
                    else{
                        errorMessages += "The Time doesn't fall under the business hour.\n";
                    }
                }
            }
            else{
                errorMessages += "The selected time is a past time!\n";
            }
            if(!unchanged){
                //Checking Conflicting Appointment Times
                //first getting times from database
                Statement statement  = DBConnection.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT Start from appointments");

                System.out.println("User : "+ estZDT);
                while(resultSet.next()){
                    String startDateTime = resultSet.getString("Start");
                    LocalDateTime localDateTime = LocalDateTime.parse(startDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    Instant instant = localDateTime.atZone(ZoneId.of("UTC")).toInstant();
                    ZonedDateTime estDatabaseZDT = ZonedDateTime.ofInstant(instant, ZoneId.of("America/New_York"));

                /*//Converting to LocalTime Object

                //Converting to ZonedDateTime
                localDateTime =  ZonedDateTime.of("UTC");
                ZonedDateTime estDatabaseZDT = ZonedDateTime.ofInstant(localDateTime.toInstant(), estZoneId); //localDateTime.atZone(estZoneId); */

                    //comparing both times in EST
                    if(estDatabaseZDT.toLocalDate().isEqual(estZDT.toLocalDate())){
                        //checking if the the time is equal
                        if(estDatabaseZDT.toLocalTime().getHour() == estZDT.toLocalTime().getHour()){
                            System.out.println("Hour found equal");
                            errorMessages += "There's already existing appointment on this time\n";
                        }
                    }
                }
            }
        } catch (NullPointerException e){
            System.out.println("Exception Caught");
        }
        catch (SQLException se){
            System.out.println("Exception caught when fetching Appointment  Times");
        }
    }

    public static int getExtractedTime(String stringTime){
        String numberOnly = stringTime.replaceAll("[^0-9]", "");
        String startHour = numberOnly.substring(0, 2);
        return Integer.parseInt(startHour);
    }

    public static void updateAppointment(boolean unchanged){
        //here goes the updating process
        errorMessages = "";

        getErrorMessages(UpdateAppointmentView.titleField, UpdateAppointmentView.descriptionField, UpdateAppointmentView.locationField, UpdateAppointmentView.contactBox,
                UpdateAppointmentView.typeField, UpdateAppointmentView.datePicker, UpdateAppointmentView.timeBox, UpdateAppointmentView.customerBox, UpdateAppointmentView.userBox);
        checkValidDate(UpdateAppointmentView.datePicker);

        checkValidAppointmentTime(UpdateAppointmentView.datePicker, UpdateAppointmentView.timeBox, unchanged);
        if(errorMessages.length() > 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(errorMessages);
            alert.showAndWait();
        }
        else{
            //converting the DateTime from comboBox to UTC and formatting to store in the database.
            String formattedStartTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(GetData.convertTimeTo(startTimeLocalZDT, "UTC"));
            String formattedEndTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(GetData.convertTimeTo(endTimeLocalZDT, "UTC"));

            //Now, storing the values to the Database
            try{
                String query = "UPDATE appointments SET Title=?, Description=?, Location = ?, Type = ?, Start = ?, End = ?, Create_Date = CURRENT_TIMESTAMP," +
                        " Created_By = ?, Last_Update = CURRENT_TIMESTAMP, Last_Updated_By = ?, " +
                        "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
                PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
                preparedStatement.setString(1, UpdateAppointmentView.titleField.getText());
                preparedStatement.setString(2, UpdateAppointmentView.descriptionField.getText());
                preparedStatement.setString(3, UpdateAppointmentView.locationField.getText());
                preparedStatement.setString(4, UpdateAppointmentView.typeField.getText());
                preparedStatement.setString(5, formattedStartTime);
                preparedStatement.setString(6, formattedEndTime);
                preparedStatement.setString(7, "roshan");
                preparedStatement.setString(8, "roshan");
                preparedStatement.setInt(9, GetData.getCustomerId(UpdateAppointmentView.customerBox.getSelectionModel().getSelectedItem().toString()));
                preparedStatement.setInt(10, GetData.getUserId(UpdateAppointmentView.userBox.getSelectionModel().getSelectedItem().toString()));
                preparedStatement.setInt(11, GetData.getContactId(UpdateAppointmentView.contactBox.getSelectionModel().getSelectedItem().toString()));
                System.out.println("ID: " + Integer.parseInt(UpdateAppointmentView.idField.getText()));
                preparedStatement.setInt(12, Integer.parseInt(UpdateAppointmentView.idField.getText()));
                preparedStatement.executeUpdate();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Successfully updated appointment!");
                    alert.showAndWait();
                    AppointmentMainView.appointmentTableView.setItems(GetData.getAppointments("select Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID  " +
                        "FROM appointments"));
                    AppointmentMainView.appointmentTableView.refresh();
                    UpdateAppointmentView.window.close();

            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Couldn't Update Appointment!");
                alert.showAndWait();
            }
        }
    }

    public static void deleteAppointment(int id) throws Exception{
            //delete a customer from the database

            //deleting the customer's appointments (from appointments table)
            String query = "DELETE FROM appointments WHERE Appointment_ID = ?";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
    }

    public static String zoneIdToLocal(String time, String zoneId){
        //converting to local datetime
        LocalDateTime localTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Instant timeInstant = localTime.atZone(ZoneId.of(zoneId)).toInstant();
        //to LOCAL TIMEZONE
        ZonedDateTime systemTime = ZonedDateTime.ofInstant(timeInstant, ZoneId.systemDefault());
        //converting to string
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(systemTime);
    }

    public static ZonedDateTime stringToSystemZdt(String dateTime, String zoneId){
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Instant instant = localDateTime.atZone(ZoneId.of(zoneId)).toInstant();
        ZonedDateTime zDt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return zDt;
    }
}

