package view;

import Controller.AppointmentController;
import Controller.CustomerController;
import Model.Appointment;
import Model.Customer;
import Model.GetData;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Optional;

public class AppointmentMainView {

    public static Stage window;
    public static TableView<Appointment> appointmentTableView;
    private static RadioButton weeklyViewButton;
    private static RadioButton monthlyViewButton;

    public static void getAppointmentMainView(){
        window = new Stage();
        window.setTitle("Appointments View");
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        Button addAppointmentButton = new Button("Add Appointment");
        addAppointmentButton.setOnAction(event -> {
            AddAppointmentView.displayAddAppointmentView();
        });

        Button updateAppointmentButton = new Button("Update Appointment");

        updateAppointmentButton.setOnAction(event -> {
            if(appointmentTableView.getSelectionModel().getSelectedItem() == null){
                System.out.println("No Item clicked");
            }
            else{
                UpdateAppointmentView.displayUpdateAppointmentView(appointmentTableView.getSelectionModel().getSelectedItem());
            }
        });

        Button deleteAppointmentButton = new Button("Delete Appointment");
        deleteAppointmentButton.setOnAction(event -> {
            if(appointmentTableView.getSelectionModel().getSelectedItem() != null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Appointment");
                alert.setHeaderText("Are you sure you want to delete this appointment?");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    try{
                        AppointmentController.deleteAppointment(appointmentTableView.getSelectionModel().getSelectedItem().getId());
                        //refreshing the tableview after data (customer) has been deleted.
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Success");
                        alert1.setHeaderText("Successfully deleted appointment with Appointment ID " + appointmentTableView.getSelectionModel().getSelectedItem().getId()
                                + " and Title: " + appointmentTableView.getSelectionModel().getSelectedItem().getType());
                        alert1.showAndWait();
                        appointmentTableView.setItems(GetData.getAppointments("select Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID  " +
                                "FROM appointments"));
                        appointmentTableView.refresh();
                    } catch (Exception e){
                        Alert al = new Alert(Alert.AlertType.ERROR);
                        al.setTitle("Error");
                        al.setHeaderText("Couldn't Delete Appointment!");
                        al.showAndWait();
                    }
                }
            }
        });

        //this will act like a refresh button for displaying all the appointments
        Button allAppointmentsButton = new Button("All Appointments");
        allAppointmentsButton.setOnAction( event -> {
            appointmentTableView.setItems(GetData.getAppointments("select Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID  " +
                    "FROM appointments"));
            appointmentTableView.refresh();
            weeklyViewButton.setSelected(false);
            monthlyViewButton.setSelected(false);
        });

        //Creating radio buttons for Weekly and Monthly View
        final ToggleGroup group = new ToggleGroup();

        weeklyViewButton = new RadioButton("Weekly View");
        weeklyViewButton.setToggleGroup(group);
        weeklyViewButton.setOnAction( event -> {
            appointmentTableView.setItems(GetData.getWeeklyAppointments());
            appointmentTableView.refresh();
        });

        monthlyViewButton = new RadioButton("Monthly View");
        monthlyViewButton.setToggleGroup(group);
        monthlyViewButton.setOnAction( event -> {
            appointmentTableView.setItems(GetData.getMonthlyAppointments());
            appointmentTableView.refresh();
        });

        //button for MonthType Appointment Report
        Button monthTypeButton = new Button("Month-Type Report");
        monthTypeButton.setOnAction(event -> {
            MonthTypeReportView.getMonthlyTypeReportView();
        });

        //Reports for each contact
        ComboBox contactBox = new ComboBox();
        contactBox.setPromptText("Contact-Report");
        //populating contact
        Iterator<String> contactIterator = GetData.getContacts().iterator();
        while(contactIterator.hasNext()){
            contactBox.getItems().add(contactIterator.next());
        }
        contactBox.setOnAction( event -> {
            AppointmentsByContact.getAppointmentTableView(GetData.getAppointmentsByContact(contactBox.getSelectionModel().getSelectedItem().toString()));
            window.close();
        });

        //button to generate reports based on location
        ComboBox<String> locationBox = new ComboBox();
        locationBox.setPromptText("Report-By-Location");
        //populating the location combobox
        ArrayList<String> locationList = GetData.getLocation();
        locationList.forEach(location -> locationBox.getItems().add(location));

        //Using LAMBDA expression to populate the Location ComboBox
        locationBox.setOnAction( event -> {
            appointmentTableView.setItems(GetData.getAppointmentsByLocation(locationBox.getSelectionModel().getSelectedItem().toString()));
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.getChildren().addAll(addAppointmentButton, updateAppointmentButton, deleteAppointmentButton,  allAppointmentsButton,
                weeklyViewButton, monthlyViewButton, monthTypeButton, contactBox, locationBox);
        vBox.getChildren().addAll(getAppointmentTableView(), hBox);

        Scene scene = new Scene(vBox);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest( event -> {
            window.close();
            MainView.displayMainView();
        });
    }

    public static TableView<Appointment> getAppointmentTableView(){
        appointmentTableView = new TableView<>();
        appointmentTableView.setMinWidth(1200);
        TableColumn<Appointment, Integer> appointmentIdColumn = new TableColumn<>("Appointment Id");
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Appointment, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Appointment, String> locationColumn= new TableColumn<>("Location");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Appointment, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Appointment, String> startColumn = new TableColumn<>("Start");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));

        TableColumn<Appointment, String> endColumn = new TableColumn<>("End");
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));

        TableColumn<Appointment, Integer> customerIdColumn = new TableColumn<>("Customer Id");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Appointment, Integer> userIdColumn = new TableColumn<>("User Id");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<Appointment, String> contactNameColumn = new TableColumn<>("Contact");
        contactNameColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));

        appointmentTableView.getColumns().addAll(appointmentIdColumn, titleColumn, descriptionColumn, locationColumn, typeColumn, startColumn, endColumn,
                customerIdColumn, userIdColumn, contactNameColumn);

        appointmentTableView.setItems(GetData.getAppointments("select Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID  " +
                "FROM appointments"));
        return appointmentTableView;
    }

}
