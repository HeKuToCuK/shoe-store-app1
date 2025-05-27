package com.shoeshop.controller;

import com.shoeshop.model.Product;
import com.shoeshop.model.Supplier;
import com.shoeshop.service.ProductService;
import com.shoeshop.service.SupplierService;
import com.shoeshop.util.FxUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class EditProductController {
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private ComboBox<Supplier> supplierCombo;

    @Autowired private ProductService productService;
    @Autowired private SupplierService supplierService;

    private Product product;
    private Stage stage;

    public void setProduct(Product product) {
        this.product = product;
        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStockQuantity()));
        supplierCombo.getSelectionModel().select(product.getSupplier());
    }

    @FXML
    public void initialize() {
        supplierCombo.setItems(FXCollections.observableArrayList(supplierService.getAllSuppliers()));
        supplierCombo.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier supplier) {
                return supplier != null ? supplier.getName() : "";
            }
            @Override
            public Supplier fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleSave() {
        try {
            product.setName(nameField.getText());
            product.setPrice(Double.parseDouble(priceField.getText()));
            product.setStockQuantity(Integer.parseInt(stockField.getText()));
            product.setSupplier(supplierCombo.getValue());

            productService.updateProduct(product);
            closeWindow();
        } catch (Exception e) {
            FxUtil.showErrorAlert("Ошибка", "Неверные данные: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        } else {
            nameField.getScene().getWindow().hide();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}