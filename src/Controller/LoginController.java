package Controller;

import Database.DBConnection;

import java.sql.*;

public class LoginController {

    public static void logIn(int id, String password){
        try{
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM  users");
            while(resultSet.next()){
                int userId = resultSet.getInt("User_ID");
                String userPassword = resultSet.getString("Password");
                if(userId == id && userPassword.equals(password)){
                    System.out.println("Login successful");
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
