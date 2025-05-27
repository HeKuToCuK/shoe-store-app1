
package com.shoeshop.controller;

import com.shoeshop.model.Order;
import com.shoeshop.model.OrderDetail;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;

@Controller
public class OrderDetailsController {
    @FXML private Label orderIdLabel;
    @FXML private Label customerLabel;
    @FXML private Label dateLabel;
    @FXML private Label totalLabel;
    @FXML private TableView<OrderDetail> detailsTable;

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        System.out.println("Order details count: " + order.getOrderDetails().size()); // Логируем
        updateUI();  // Обновляем интерфейс
    }

    private void updateUI() {
        if (order == null) return;

        orderIdLabel.setText("Заказ #" + order.getId());
        customerLabel.setText("Клиент: " + order.getCustomer().getFullName());
        dateLabel.setText("Дата: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        totalLabel.setText(String.format("Итого: %.2f руб.", order.getTotalPrice()));

        setupTable();
        detailsTable.setItems(FXCollections.observableArrayList(order.getOrderDetails()));
    }

    private void setupTable() {
        detailsTable.setItems(FXCollections.observableArrayList(order.getOrderDetails()));
        TableColumn<OrderDetail, String> productCol = new TableColumn<>("Товар");
        productCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getProduct().getName()));

        TableColumn<OrderDetail, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<OrderDetail, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(column -> new TableCell<OrderDetail, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("%.2f руб.", price));
            }
        });

        TableColumn<OrderDetail, Double> sumCol = new TableColumn<>("Сумма");
        sumCol.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getQuantity() * cd.getValue().getPrice()).asObject());
        sumCol.setCellFactory(column -> new TableCell<OrderDetail, Double>() {
            @Override
            protected void updateItem(Double sum, boolean empty) {
                super.updateItem(sum, empty);
                setText(empty ? null : String.format("%.2f руб.", sum));
            }
        });

        detailsTable.getColumns().setAll(productCol, quantityCol, priceCol, sumCol);

    }
    @FXML
    private void handleClose() {
        orderIdLabel.getScene().getWindow().hide();
    }
}
