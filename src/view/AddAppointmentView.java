package view;

import Controller.AppointmentController;
import Controller.CustomerController;
import Model.Appointment;
import Model.Customer;
import Model.GetData;
import view.MainView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;


public class AddAppointmentView {

    public static Stage window = new Stage();

    public static Label idLabel;
    public static TextField idField;

    public static Label titleLabel;
    public static TextField titleField;

    public static Label descriptionLabel;
    public static TextField descriptionField;

    public static Label locationLabel;
    public static TextField locationField;

    public static Label contactLabel;
    public static ComboBox contactBox;

    public static Label typeLabel;
    public static TextField typeField;

    public static Label dateLabel;
    public static DatePicker datePicker;

    public static Label timeLabel;
    public static ComboBox timeBox;

    public static Label customerLabel;
    public static ComboBox customerBox;

    public static Label userLabel;
    public static ComboBox userBox;

    public static Button addAppointmentButton;

    public static void displayAddAppointmentView(){
        AppointmentMainView.window.close();
        window.setTitle("Add Appointment");
        window.setMinHeight(600);
        window.setMinWidth(600);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        idLabel = new Label("ID: ");
        idField = new TextField();
        idField.setDisable(true);
        GridPane.setConstraints(idLabel, 0, 0);
        GridPane.setConstraints(idField, 1, 0);

        titleLabel = new Label("Title: ");
        titleField = new TextField();
        GridPane.setConstraints(titleLabel, 0, 1);
        GridPane.setConstraints(titleField, 1, 1);

        descriptionLabel = new Label("Description: ");
        descriptionField = new TextField();
        GridPane.setConstraints(descriptionLabel, 0, 2);
        GridPane.setConstraints(descriptionField, 1, 2);

        locationLabel = new Label("Location: ");
        locationField = new TextField();
        GridPane.setConstraints(locationLabel, 0, 3);
        GridPane.setConstraints(locationField, 1, 3);

        contactLabel = new Label("Contact: ");
        contactBox = new ComboBox();
        contactBox.setPromptText("Select Contact");
        GridPane.setConstraints(contactLabel, 0, 4);
        GridPane.setConstraints(contactBox, 1, 4);

        //Populating Contacts
        Iterator<String> contactIterator = GetData.getContacts().iterator();
        while(contactIterator.hasNext()){
            contactBox.getItems().add(contactIterator.next());
        }

        typeLabel = new Label("Type: ");
        typeField = new TextField();
        GridPane.setConstraints(typeLabel, 0, 5);
        GridPane.setConstraints(typeField, 1, 5);

        dateLabel = new Label("Date: ");
        datePicker = new DatePicker();
        GridPane.setConstraints(dateLabel, 0, 6);
        GridPane.setConstraints(datePicker, 1, 6);

        timeLabel = new Label("Time: ");
        timeBox = new ComboBox();
        timeBox.setPromptText("Select Time");
        GridPane.setConstraints(timeLabel, 0, 7);
        GridPane.setConstraints(timeBox, 1, 7);

        //Populating Time ComboBox
        Iterator<String> iterator = GetData.getAppointmentTimes().iterator();
        while(iterator.hasNext()){
            timeBox.getItems().add(iterator.next());
        }

        //display the timezone.
        Label timeDescription = new Label("Note: Time displayed in 24 Hour format");
        GridPane.setConstraints(timeDescription, 2, 7);

        customerLabel = new Label("Customer: ");
        customerBox = new ComboBox();
        customerBox.setPromptText("Select Customer");
        GridPane.setConstraints(customerLabel, 0, 8);
        GridPane.setConstraints(customerBox, 1, 8);

        //Populating Customer Combobox
        Iterator<String> it = GetData.getCustomersName().iterator();
        while(it.hasNext()){
            customerBox.getItems().add(it.next());
        }

        userLabel = new Label("User: ");
        userBox = new ComboBox();
        userBox.setPromptText("Select User");
        GridPane.setConstraints(userLabel, 0, 9);
        GridPane.setConstraints(userBox, 1, 9);

        //Populating User ComboBox with list of user_names;
        Iterator<String> userIterator = GetData.getUserNames().iterator();
        while(userIterator.hasNext()){
            userBox.getItems().add(userIterator.next());
        }

        addAppointmentButton = new Button("Add Appointment");
        addAppointmentButton.setOnAction(event -> {
            AppointmentController.addAppointment();
        });

        GridPane.setConstraints(addAppointmentButton, 0, 10);

        gridPane.getChildren().addAll(idLabel, idField, titleLabel, titleField, descriptionLabel, descriptionField, locationLabel, locationField,
                contactLabel, contactBox, typeLabel, typeField, dateLabel, datePicker, timeLabel, timeBox, timeDescription, customerLabel,
                customerBox, userLabel, userBox, addAppointmentButton);

        Scene scene = new Scene(gridPane);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest(event -> {
            AppointmentMainView.getAppointmentMainView();
            window.close();
        });

    }
}
