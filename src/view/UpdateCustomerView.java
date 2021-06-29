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

public class UpdateCustomerView {

    public static Label idLabel;
    public static TextField idField;

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

    public static Button updateCustomerButton;

    public static void displayUpdateCustomerForm(Customer customer){
        Stage window = new Stage();
        window.setTitle("Add Customer Form");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        //creating form labels and form fields

        idLabel = new Label("Id: ");
        idField = new TextField();
        //setting the value of ID
        idField.setText(String.valueOf(customer.getCustomerId()));
        idField.setDisable(true);
        GridPane.setConstraints(idLabel, 0, 0);
        GridPane.setConstraints(idField, 1, 0);

        nameLabel = new Label("Customer Name: ");
        nameField = new TextField();
        nameField.setText(customer.getCustomerName());
        GridPane.setConstraints(nameLabel, 0, 1);
        GridPane.setConstraints(nameField, 1, 1);

        addressLabel = new Label("Address: ");
        addressField = new TextField();
        addressField.setText(customer.getAddress());
        GridPane.setConstraints(addressLabel, 0, 2);
        GridPane.setConstraints(addressField, 1, 2);

        postalCodeLabel = new Label("Postal Code: ");
        postalCodeField = new TextField();
        postalCodeField.setText(customer.getPostalCode());
        GridPane.setConstraints(postalCodeLabel, 0, 3);
        GridPane.setConstraints(postalCodeField, 1, 3);

        phoneLabel = new Label("Phone Number: " );
        phoneField = new TextField();
        phoneField.setText(customer.getPhone());
        GridPane.setConstraints(phoneLabel, 0, 4);
        GridPane.setConstraints(phoneField, 1, 4);

        //Combo Box for Country
        countryLabel = new Label("Country: ");
        countryComboBox = new ComboBox<String>();
        countryComboBox.getSelectionModel().select(GetData.getCountry(customer.getDivision()));
        GridPane.setConstraints(countryLabel, 0, 5);
        GridPane.setConstraints(countryComboBox, 1, 5);

        //Combo Box for Division
        divisionLabel = new Label("Division: ");
        divisionComboBox = new ComboBox<String>();
        divisionComboBox.getSelectionModel().select(customer.getDivision());
        GridPane.setConstraints(divisionLabel, 0, 6);
        GridPane.setConstraints(divisionComboBox, 1, 6);

        //populating Country ComboBox
        Iterator<String> countryIterator = GetData.getCountryList().iterator();
        while(countryIterator.hasNext()){
            countryComboBox.getItems().add(countryIterator.next());
        }

        divisionComboBox.getItems().clear();

        //populating Division ComboBox
        Iterator<String> divisionIterator = GetData.getDivisionList(GetData.getCountry(customer.getDivision())).iterator();
        while(divisionIterator.hasNext()){
            divisionComboBox.getItems().add(divisionIterator.next());
        }

        //populating Divisions according to the country selection
        countryComboBox.setOnAction(event -> {
            //emptying the divisionComboBox lists to eliminate getting duplicate division list values
            divisionComboBox.getItems().clear();

            //populating Division ComboBox
           Iterator<String>  iterator = GetData.getDivisionList(countryComboBox.getValue().toString()).iterator();
            while(iterator.hasNext()){
                divisionComboBox.getItems().add(iterator.next());
            }
        });

        updateCustomerButton = new Button("Update Customer");
        updateCustomerButton.setOnAction(event -> {
            CustomerController.errorMessages = "";
            CustomerController.updateCustomer(customer.getCustomerId());
            CustomersView.customerTableView.refresh();
        });
        GridPane.setConstraints(updateCustomerButton, 0, 7);

        grid.getChildren().addAll(idLabel, idField, nameLabel, nameField, addressLabel, addressField, postalCodeLabel, postalCodeField, phoneLabel, phoneField,
                divisionLabel, divisionComboBox, countryLabel, countryComboBox, updateCustomerButton);

        Scene scene = new Scene(grid, 600, 600);

        window.setScene(scene);
        window.show();

        window.setOnCloseRequest(event -> {
            CustomersView.window.show();
            CustomersView.customerTableView.setItems(GetData.getCustomers());
            CustomersView.customerTableView.refresh();
        });
    }
}
