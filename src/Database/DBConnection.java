package Database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DBConnection{
    private static final String jdbcURl = "jdbc:mysql://wgudb.ucertify.com:3306/WJ07wXf";

    private static final String MYSQLDriver = "com.mysql.cj.jdbc.Driver";

    //setting up the username
    private static final String username = "U07wXf";

    private static Connection connection = null;

    public static Connection startConnection(){
        try{
            Class.forName(MYSQLDriver);
            connection = DriverManager.getConnection(jdbcURl, username, "53689155093");
            System.out.println("Connection Successful");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        return connection;
    }

    //Creating getConnection to access all over the program
    public static Connection getConnection(){
        return connection;
    }

    public static void closeConnection(){
        try{
            connection.close();
        } catch (Exception e){
            //print out the exception
        }
    }

}
