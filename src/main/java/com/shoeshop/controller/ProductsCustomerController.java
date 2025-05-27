package com.shoeshop.controller;
import com.shoeshop.model.Product;
import com.shoeshop.model.Supplier;
import com.shoeshop.model.User;
import com.shoeshop.service.AuthService;
import com.shoeshop.service.ProductService;
import com.shoeshop.service.SupplierService;
import com.shoeshop.util.FxUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductsCustomerController {
    @FXML private TableView<Product> productsTable;
    @Autowired private ProductService productService;
    @FXML private Button addButton;

    @FXML
    public void initialize() {
        configureTable();
        loadProducts();
        if (addButton != null) {
            addButton.setVisible(false);
        }
    }

    private void configureTable() {
        TableColumn<Product, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> sizeCol = new TableColumn<>("Размер");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("%.2f руб.", price));
            }
        });

        productsTable.getColumns().setAll(nameCol, sizeCol, priceCol);
    }

    private void loadProducts() {
        List<Product> products = productService.getAllAvailableProducts();
        productsTable.setItems(FXCollections.observableArrayList(products));
    }
}
