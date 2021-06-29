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
import java.util.Calendar;
import java.util.Optional;


/*
SELECT MONTHNAME(Start) AS Month,
type AS Appointment_Type,
COUNT(*) AS Number
FROM appointments
GROUP BY Month, type;
 */
public class MonthTypeReportView {
    public static void getMonthlyTypeReportView(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Report");
        alert.setHeaderText("Appointments by Month and Type Report!\n Month----Type----Count");
        alert.setContentText(GetData.getAppointmentbyTypeMonth());
        alert.showAndWait();
    }
}
