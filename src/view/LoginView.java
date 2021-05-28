package view;

import Controller.LoginController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

//for Zone Id (Location)
import java.time.ZoneId;

public class LoginView{
    public static void displayLoginView(){
            Stage window = new Stage();
            window.setTitle("Appointment Scheduler");
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);

            //this padding is for the main pane
            grid.setPadding(new Insets(10, 10, 10, 10));

            //this is for the individual cells in grid
            grid.setVgap(10);
            grid.setHgap(10);

            Label heading = new Label("Log-in");
            GridPane.setConstraints(heading, 0, 0);


            Label id = new Label("Id");
            GridPane.setConstraints(id, 0, 1);

            TextField idTextField = new TextField();
            GridPane.setConstraints(idTextField, 1, 1);

            Label password = new Label("Password");
            GridPane.setConstraints(password, 0, 2);

            TextField passwordTextField = new TextField();
            passwordTextField.setPromptText("Password"); // this disappears after clicking in the field.
            GridPane.setConstraints(passwordTextField, 1, 2);

            Button loginButton = new Button("Log In");
            //Login Button Handling
            loginButton.setOnAction(event -> {
                    LoginController.logIn(Integer.parseInt(idTextField.getText()), passwordTextField.getText());
            });

            GridPane.setConstraints(loginButton, 1, 3);

            //this comes from the user's location
            String location = getZoneId();
            Label locationLabel = new Label("Your location: "+location);
            GridPane.setColumnSpan(locationLabel, 2);
            GridPane.setConstraints(locationLabel, 0, 4);

            grid.getChildren().addAll(heading, id, idTextField, password, passwordTextField, loginButton, locationLabel);

            Scene scene = new Scene(grid, 300, 200);
            window.setScene(scene);
            window.show();
    }

    public static String getZoneId(){
        ZoneId zoneId = ZoneId.systemDefault();
        return zoneId.getId();
    }

    public static String getg
}
