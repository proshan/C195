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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;


public class UpdateAppointmentView {

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

    public static Button updateAppointmentButton;

    public static void displayUpdateAppointmentView(Appointment appointment){
        window = new Stage();
        window.setTitle("Update Appointment");
        System.out.println("Appointment Update called.");
        window.setMinHeight(600);
        window.setMinWidth(600);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        idLabel = new Label("ID: ");
        idField = new TextField();
        idField.setText(String.valueOf(appointment.getId()));
        idField.setDisable(true);
        GridPane.setConstraints(idLabel, 0, 0);
        GridPane.setConstraints(idField, 1, 0);

        titleLabel = new Label("Title: ");
        titleField = new TextField();
        titleField.setText(appointment.getTitle());
        GridPane.setConstraints(titleLabel, 0, 1);
        GridPane.setConstraints(titleField, 1, 1);

        descriptionLabel = new Label("Description: ");
        descriptionField = new TextField();
        descriptionField.setText(appointment.getDescription());
        GridPane.setConstraints(descriptionLabel, 0, 2);
        GridPane.setConstraints(descriptionField, 1, 2);

        locationLabel = new Label("Location: ");
        locationField = new TextField();
        locationField.setText(appointment.getLocation());
        GridPane.setConstraints(locationLabel, 0, 3);
        GridPane.setConstraints(locationField, 1, 3);

        contactLabel = new Label("Contact: ");
        contactBox = new ComboBox();
        contactBox.setPromptText("Select Contact");
        contactBox.getSelectionModel().select(appointment.getContactName());
        GridPane.setConstraints(contactLabel, 0, 4);
        GridPane.setConstraints(contactBox, 1, 4);

        //Populating Contacts
        Iterator<String> contactIterator = GetData.getContacts().iterator();
        while(contactIterator.hasNext()){
            contactBox.getItems().add(contactIterator.next());
        }

        typeLabel = new Label("Type: ");
        typeField = new TextField();
        typeField.setText(appointment.getType());
        GridPane.setConstraints(typeLabel, 0, 5);
        GridPane.setConstraints(typeField, 1, 5);

        dateLabel = new Label("Date: ");
        datePicker = new DatePicker();
        String startTime = appointment.getStartDateTime();
        System.out.println(startTime);
        //Converting to LocalTime Object
        LocalDateTime localDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        datePicker.setValue(localDateTime.toLocalDate());
        GridPane.setConstraints(dateLabel, 0, 6);
        GridPane.setConstraints(datePicker, 1, 6);

        timeLabel = new Label("Time: ");
        timeBox = new ComboBox();
        timeBox.setPromptText("Select Time");
        //Formatting 9:OO to 09:00
        String hour = String.valueOf(localDateTime.getHour());
        if(hour.length() == 1){
            hour = "0" + hour;
        }
        timeBox.getSelectionModel().select(hour + " : 00 - " + hour + " : 59");
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
        customerBox.getSelectionModel().select(GetData.getCustomerName(appointment.getCustomerId()));
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
        userBox.getSelectionModel().select(GetData.getUserName(appointment.getUserId()));
        GridPane.setConstraints(userLabel, 0, 9);
        GridPane.setConstraints(userBox, 1, 9);

        //Populating User ComboBox with list of user_names;
        Iterator<String> userIterator = GetData.getUserNames().iterator();
        while(userIterator.hasNext()){
            userBox.getItems().add(userIterator.next());
        }

        updateAppointmentButton = new Button("Update Appointment");
        updateAppointmentButton.setOnAction(event -> {
            //to compare if any modification in the appointment date and time has taken place
            //this is stored date and time
            String systemZoneId = ZoneId.systemDefault().getId();
            String startDateTime = AppointmentController.zoneIdToLocal(appointment.getStartDateTime().toString(), systemZoneId);
            LocalDateTime oldStartDateTime = LocalDateTime.parse(startDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            //this is customer's updated date and time (it may have been unchanged too)
            LocalTime startTimeComboBox = LocalTime.of(AppointmentController.getExtractedTime(timeBox.getSelectionModel().getSelectedItem().toString()), 0);//Get from combobox (start time)
            ZonedDateTime updatedDateTime =  ZonedDateTime.of(datePicker.getValue(), startTimeComboBox, ZoneId.systemDefault());
            LocalDateTime updatedStartDateTime = updatedDateTime.toLocalDateTime();
            boolean dateIsUnchanged = false;
            if(oldStartDateTime.equals(updatedStartDateTime)){
                dateIsUnchanged = true;
            }
            AppointmentController.updateAppointment(dateIsUnchanged);
        });

        GridPane.setConstraints(updateAppointmentButton, 0, 10);

        gridPane.getChildren().addAll(idLabel, idField, titleLabel, titleField, descriptionLabel, descriptionField, locationLabel, locationField,
                contactLabel, contactBox, typeLabel, typeField, dateLabel, datePicker, timeLabel, timeBox, timeDescription, customerLabel,
                customerBox, userLabel, userBox, updateAppointmentButton);

        Scene scene = new Scene(gridPane);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest( event -> {
            AppointmentMainView.getAppointmentMainView();
        });
    }
}
