package view;

import Model.Appointment;
import Model.GetData;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AppointmentsByContact {

    public static Stage window;
    public static TableView<Appointment> contactTableView;
    public static void getAppointmentTableView(ObservableList<Appointment> appointments) {
        window = new Stage();
        window.setTitle("Appointments By Contact");

        contactTableView = new TableView<>();

        contactTableView.setMinWidth(1200);
        TableColumn<Appointment, Integer> appointmentIdColumn = new TableColumn<>("Appointment Id");
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Appointment, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Appointment, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Appointment, String> startColumn = new TableColumn<>("Start");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));

        TableColumn<Appointment, String> endColumn = new TableColumn<>("End");
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));

        TableColumn<Appointment, Integer> customerIdColumn = new TableColumn<>("Customer Id");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        contactTableView.getColumns().addAll(appointmentIdColumn, titleColumn, descriptionColumn, typeColumn, startColumn, endColumn,
                customerIdColumn);

        contactTableView.setItems(GetData.getAppointments("select Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID  " +
                "FROM appointments"));

        contactTableView.setItems(appointments);

        Scene scene = new Scene(contactTableView);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest( event -> {
            AppointmentMainView.getAppointmentMainView();
        });
    }
}
