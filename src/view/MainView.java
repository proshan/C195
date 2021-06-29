package view;
import Model.GetData;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainView{
    public static Stage window = new Stage();
    public static void displayMainView(){
        LoginView.window.close();
        window.setTitle("Welcome | Dashboard");

        //two buttons
        //one for Customer section
        Button customerButton = new Button("Customer");
        customerButton.setOnAction( event -> {
            try {
                CustomersView.displayCustomersView();
                window.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //another for Appointment Section
        Button appointmentButton = new Button("Appointment");
        appointmentButton.setOnAction(event -> {
           AppointmentMainView.getAppointmentMainView();
           MainView.window.close();
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setMargin(customerButton, new Insets(20, 20, 20, 20));
        hBox.setMargin(appointmentButton, new Insets(20, 20, 20, 20));

        hBox.getChildren().addAll(customerButton, appointmentButton);

        //Creating a scene
        Scene scene = new Scene(hBox);
        window.setScene(scene);
        window.showAndWait();
    }
}
