package Controller;

import Database.DBConnection;
import Model.GetData;
import com.mysql.cj.protocol.Resultset;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import view.AddCustomerView;
import view.CustomersView;
import view.UpdateCustomerView;

import java.sql.*;

public class CustomerController{

    public static String errorMessages = "";

    public static void deleteCustomer(int id) throws Exception{
            //delete a customer from the database

            //deleting the customer's appointments (from appointments table)
            String query = "DELETE FROM appointments WHERE Customer_ID = ?";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            //Now deleting the customer (from customer table)
            query = "DELETE FROM customers WHERE Customer_ID = ?";
            preparedStatement = DBConnection.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
    }

    //getting data for create and update customer (also validation)
    //the parameter will be the class AddCustomerView or UpdateCustomerView
    //so that respective data can be pulled using the class name


    public static void addCustomer(){
        try{
            //adding customer to the database
            String name = AddCustomerView.nameField.getText();
            String address = AddCustomerView.addressField.getText();
            String postal = AddCustomerView.postalCodeField.getText();
            String phone = AddCustomerView.phoneField.getText();

            String country = (String) AddCustomerView.countryComboBox.getSelectionModel().getSelectedItem();
            int country_id = getCountryId(country);
            System.out.println(country_id);
            System.out.println(country);
            String division = (String) AddCustomerView.divisionComboBox.getSelectionModel().getSelectedItem();
            System.out.println(division);

            if(name == null || address == null || postal == null || phone == null || division == null || country == null){
                throw new NullPointerException();
            }
            long numericPhone = Long.parseLong(phone);

            //if no any exceptions found when submitting the button,
            //adding the data to the database
            String query = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP , ?, (SELECT Division_ID from first_level_divisions WHERE Division = ?))";
            try{
                PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, address);
                preparedStatement.setString(3, postal);
                preparedStatement.setString(4, phone);
                preparedStatement.setString(5, "script");
                preparedStatement.setString(6, "script");
                preparedStatement.setString(7, division);
                //executing insert statement
                if(preparedStatement.executeUpdate() > 0){
                    /*Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success!");
                    alert1.setHeaderText("Customer created Successfully!");*/
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Successfully created customer!");
                    alert.showAndWait();
                }
                CustomersView.customerTableView.refresh();
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unsuccessful customer creation!");
                alert.setHeaderText("Couldn't add customers to database!");
                alert.showAndWait();
            }
        } catch (NullPointerException e){
            errorMessages += "Fields and Selections input missing!\n";
        } catch (NumberFormatException nfe){
            errorMessages += "Phone number should be numeric! \n";
        }
        if(errorMessages.length() > 1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unsuccessful customer creation!");
            alert.setHeaderText(errorMessages);
            alert.showAndWait();
        }
    }

    public static void updateCustomer(int customerId){
        //updating customer to the database
        try{
            //adding customer to the database
            String name = UpdateCustomerView.nameField.getText();
            String address = UpdateCustomerView.addressField.getText();
            String postal = UpdateCustomerView.postalCodeField.getText();
            String phone = UpdateCustomerView.phoneField.getText();

            String country = (String) UpdateCustomerView.countryComboBox.getSelectionModel().getSelectedItem();
            int country_id = getCountryId(country);
            String division = (String) UpdateCustomerView.divisionComboBox.getSelectionModel().getSelectedItem();

            if(name == null || address == null || postal == null || phone == null || division == null || country == null){
                throw new NullPointerException();
            }
            long numericPhone = Long.parseLong(phone);

            //if no any exceptions found when submitting the button,
            //updating the data to the database
            String query = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = CURRENT_TIMESTAMP , Created_By = ?," +
                    " Last_Update = CURRENT_TIMESTAMP , Last_Updated_By = ?, Division_ID = ? " +
                    "WHERE Customer_ID = ?";
            try{
                PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, address);
                preparedStatement.setString(3, postal);
                preparedStatement.setString(4, phone);
                preparedStatement.setString(5, "script");
                preparedStatement.setString(6, "script");
                preparedStatement.setInt(7, GetData.getDivisionId(division));
                preparedStatement.setInt(8, customerId);
                //executing insert statement
                if(preparedStatement.executeUpdate() > 0){
                    /*Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success!");
                    alert1.setHeaderText("Customer created Successfully!");*/
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Successfully updated customer!");
                    alert.showAndWait();
                }
                CustomersView.customerTableView.refresh();
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unsuccessful!");
                alert.setHeaderText("Couldn't update customers to database!");
                alert.showAndWait();
            }
        } catch (NullPointerException e){
            errorMessages += "Fields and Selections input missing!\n";
        } catch (NumberFormatException nfe){
            errorMessages += "Phone number should be numeric! \n";
        }
        if(errorMessages.length() > 1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unsuccessful customer Update!");
            alert.setHeaderText(errorMessages);
            alert.showAndWait();
        }
    }


    public static int getCountryId(String country){
        int id = 0;
        if(country.equals("U.S")){
            id =  1;
        }
        else if(country.equals("UK")){
            id =  2;
        }
        else if(country.equals("Canada")){
            id = 3;
        }
        return id;
    }





}
