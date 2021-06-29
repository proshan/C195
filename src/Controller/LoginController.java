package Controller;

import Database.DBConnection;
import Model.GetData;
import javafx.scene.control.Alert;
import view.LoginView;
import view.MainView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LoginController {
    private static FileWriter file;


    public static void writeToFile(String content){
        BufferedWriter out = null;
        try{
            file = new FileWriter("../loginLog.txt", true);
            out = new BufferedWriter(file);
            out.write(content);
            out.close();
        }catch (Exception e){
            System.out.println("Error getting file");
        }
    }

    private static void logIn(String id, String password){
        //checking language for error control messages
        //checking null and invalid input error
        boolean loginMatched = false;
        try{
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM  users");
            String current_user = "";
            while(resultSet.next()){
                String userId = resultSet.getString("User_Name");
                current_user = userId;
                String userPassword = resultSet.getString("Password");
                if(userId.equals(id) && userPassword.equals(password)){
                    loginMatched = true;
                    //writing to the file
                    writeToFile(LocalDateTime.now().toString() + "   " + id + "   successful" + "\n");
                    System.out.println(LocalDateTime.now().toString() + "   " + id + "   successful" + "\n");
                    checkUpcomingAppointment(userId);
                    MainView.displayMainView();
                    LoginView.window.close();
                    break;
                }
            }
            if(!loginMatched){
                //writing to the file
                System.out.println(LocalDateTime.now().toString() + "   " + id + "   unsuccessful" + "\n");
                writeToFile(LocalDateTime.now().toString() + "   " + id + "   unsuccessful" + "\n");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText("Either the id or password doesn't match!");
                alert.showAndWait();
            }

        } catch(Exception e){
            System.out.println("couldn't get users from database!");
        }
    }

    public static void logInHandle(String id, String password){
        int parsedId;
        try{
            if(password.equals("") || id.equals("")){
                throw new Exception();
            }
            logIn(id, password);
        }catch (Exception npe){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText("Fields cannot be empty.");
            alert.showAndWait();
        }
    }


    //Checking if there is an appointment within 15 minutes of logging in by the user
    public static void checkUpcomingAppointment(String userName){
        try{
            int user_id = GetData.getUserId(userName);
            PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM appointments WHERE User_ID = ?");
            statement.setInt(1, user_id);
            ResultSet resultSet = statement.executeQuery();
            //getting local DateTime (from System)
            LocalDateTime localSystemTime = LocalDateTime.now();
            System.out.println("System Time: " + localSystemTime);
            String availableAppointmentMessage = "Available appointments within 15 minutes: \n";
            while(resultSet.next()) {
                String startTime = resultSet.getString("Start");
                String localT = AppointmentController.zoneIdToLocal(startTime, "UTC");
                LocalDateTime localDatabaseTime = LocalDateTime.parse(localT, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                //checking if the date is equal
                if(localSystemTime.toLocalDate().equals(localDatabaseTime.toLocalDate())){
                    //localDatabaseTime = from
                    //localSystemTime = to
                    long minutesDifference = ChronoUnit.MINUTES.between(localSystemTime, localDatabaseTime);
                    System.out.println(minutesDifference);  // to check the difference minutes
                    if(minutesDifference > 0 && minutesDifference < 16){
                        availableAppointmentMessage += "ID: " + resultSet.getInt("Appointment_ID") +
                                " Start: " + localDatabaseTime +
                                " End: " + AppointmentController.zoneIdToLocal(resultSet.getString("End"), "UTC") + "\n";
                    }
                }
            }
            // if no any appointment was within 15 minutes of login,
            if(availableAppointmentMessage.length() < 50){
                availableAppointmentMessage += "None";
            }
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Upcoming Appointment");
            alert1.setHeaderText(availableAppointmentMessage);
            alert1.showAndWait();
        } catch (Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error fetching appointment times!");
            alert.showAndWait();
        }

    }

}
