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

import java.util.Optional;

public class CustomersView {

    public static Stage window;

    public static TableView<Customer> customerTableView = getCustomerTableView();

    public static void displayCustomersView(){
        window = new Stage();
        window.setTitle("Customers View");
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));

        Button addCustomerButton = new Button("Add Customer");
        addCustomerButton.setOnAction(event -> {
            AddCustomerView.displayAddCustomerForm();
            CustomersView.window.close();
        });

        Button updateCustomerButton = new Button("Update Customer");
        updateCustomerButton.setOnAction(event -> {
            if(customerTableView.getSelectionModel().getSelectedItem() != null){
                window.hide();
                UpdateCustomerView.displayUpdateCustomerForm(customerTableView.getSelectionModel().getSelectedItem());
            }
        });

        //delete Customer Button handling
        Button deleteCustomerButton = new Button("Delete Customer");
        deleteCustomerButton.setOnAction(event -> {
            if(customerTableView.getSelectionModel().getSelectedItem() != null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this customer?");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    try{
                        CustomerController.deleteCustomer(getSelectedCustomerId());
                        //refreshing the tableview after data (customer) has been deleted.
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Success");
                        alert1.setHeaderText("Successfully deleted customer!");
                        alert1.showAndWait();
                        customerTableView.refresh();
                        customerTableView.setItems(GetData.getCustomers());
                    } catch (Exception e){
                        Alert al = new Alert(Alert.AlertType.ERROR);
                        al.setTitle("Error");
                        al.setHeaderText("Couldn't Delete Customer!");
                        al.showAndWait();
                    }
                }
            }
        });

        hBox.getChildren().addAll(addCustomerButton, updateCustomerButton, deleteCustomerButton);
        vBox.getChildren().addAll(customerTableView, hBox);

        Scene scene = new Scene(vBox);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest(event -> {
            MainView.window.show();
        });
    }

    public static TableView<Customer> getCustomerTableView(){
        TableView<Customer> customerTable = new TableView<>();
        customerTable.setMinWidth(750);
        TableColumn customerIdColumn = new TableColumn("Customer Id");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn customerNameColumn = new TableColumn("Name");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn customerAddressColumn = new TableColumn("Address");
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn customerPostalCodeColumn= new TableColumn("Postal Code");
        customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        TableColumn customerPhoneColumn = new TableColumn("Phone");
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn customerDivisionColumn = new TableColumn("Division");
        customerDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("division"));

        customerTable.setItems(GetData.getCustomers());
        customerTable.getColumns().addAll(customerIdColumn, customerNameColumn, customerAddressColumn, customerPostalCodeColumn, customerPhoneColumn, customerDivisionColumn);

        return customerTable;
    }

    public static int getSelectedCustomerId(){
        return customerTableView.getSelectionModel().getSelectedItem().getCustomerId();
    }


}
