package Model;

import Database.DBConnection;
import com.sun.javafx.runtime.VersionInfo;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import view.CustomersView;
import view.LoginView;
import view.MainView;

import java.util.Locale;

public class Main extends Application{

    public static void main(String[] args) {
        //Starting Database Connection
        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        LoginView.displayLoginView();
    }

}
