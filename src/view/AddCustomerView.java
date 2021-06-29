package view;

import Controller.CustomerController;
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

import java.util.Iterator;

public class AddCustomerView {

    public static Label nameLabel;
    public static TextField nameField;

    public static Label addressLabel;
    public static TextField addressField;

    public static Label postalCodeLabel;
    public static TextField postalCodeField;

    public static Label phoneLabel;
    public static TextField phoneField;

    public static Label divisionLabel;
    public static ComboBox divisionComboBox ;

    public static Label countryLabel;
    public static ComboBox countryComboBox;

    public static Button createCustomerButton;

    public static Stage window;

    public static void displayAddCustomerForm() {
        window = new Stage();
        window.setTitle("Add Customer Form");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        //creating form labels and form fields
        nameLabel = new Label("Customer Name: ");
        nameField = new TextField();
        GridPane.setConstraints(nameLabel, 0, 0);
        GridPane.setConstraints(nameField, 1, 0);

        addressLabel = new Label("Address: ");
        addressField = new TextField();
        GridPane.setConstraints(addressLabel, 0, 1);
        GridPane.setConstraints(addressField, 1, 1);

        postalCodeLabel = new Label("Postal Code: ");
        postalCodeField = new TextField();
        GridPane.setConstraints(postalCodeLabel, 0, 2);
        GridPane.setConstraints(postalCodeField, 1, 2);

        phoneLabel = new Label("Phone Number: ");
        phoneField = new TextField();
        GridPane.setConstraints(phoneLabel, 0, 3);
        GridPane.setConstraints(phoneField, 1, 3);

        //Combo Box for Country
        countryLabel = new Label("Country: ");
        countryComboBox = new ComboBox<String>();
        countryComboBox.setPromptText("Select Country");
        GridPane.setConstraints(countryLabel, 0, 4);
        GridPane.setConstraints(countryComboBox, 1, 4);

        //Combo Box for Division
        divisionLabel = new Label("Division: ");
        divisionComboBox = new ComboBox<String>();
        divisionComboBox.setPromptText("Select Division");
        GridPane.setConstraints(divisionLabel, 0, 5);
        GridPane.setConstraints(divisionComboBox, 1, 5);

        //populating Country ComboBox
        Iterator<String> countryIterator = GetData.getCountryList().iterator();
        while (countryIterator.hasNext()) {
            countryComboBox.getItems().add(countryIterator.next());
        }

        //populating Divisions according to the country selection
        countryComboBox.setOnAction(event -> {
            //emptying the divisionComboBox lists to eliminate getting duplicate division list values
            divisionComboBox.getItems().clear();

            //populating Division ComboBox
            Iterator<String> divisionIterator = GetData.getDivisionList(countryComboBox.getValue().toString()).iterator();
            while (divisionIterator.hasNext()) {
                divisionComboBox.getItems().add(divisionIterator.next());
            }
        });

        createCustomerButton = new Button("Create Customer");
        createCustomerButton.setOnAction(event -> {
            CustomerController.errorMessages = "";
            CustomerController.addCustomer();
        });
        GridPane.setConstraints(createCustomerButton, 0, 6);

        grid.getChildren().addAll(nameLabel, nameField, addressLabel, addressField, postalCodeLabel, postalCodeField, phoneLabel, phoneField,
                divisionLabel, divisionComboBox, countryLabel, countryComboBox, createCustomerButton);

        Scene scene = new Scene(grid, 600, 600);

        window.setScene(scene);
        window.show();


        window.setOnCloseRequest(event -> {
            CustomersView.displayCustomersView();
        });
    }

}
