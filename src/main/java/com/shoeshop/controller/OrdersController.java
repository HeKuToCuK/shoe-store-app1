package com.shoeshop.controller;

import com.shoeshop.model.Order;
import com.shoeshop.service.OrderService;
import com.shoeshop.util.FxUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class OrdersController {
    private final ApplicationContext context;
    @FXML
    private TableView<Order> ordersTable;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @Autowired
    private OrderService orderService;

    public OrdersController(ApplicationContext context) {
        this.context = context;
    }
    @FXML
    private void handleOrderDetails(ActionEvent event) {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            FxUtil.showErrorAlert("Ошибка", "Выберите заказ!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/orders/order_details.fxml"));
            loader.setControllerFactory(context::getBean);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Детали заказа #" + selectedOrder.getId());

            OrderDetailsController controller = loader.getController();
            controller.setOrder(selectedOrder);

            stage.show();
        } catch (IOException e) {
            FxUtil.showErrorAlert("Ошибка", "Не удалось загрузить детали заказа");
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        setupTable();
        loadOrders();

        fromDatePicker.setValue(LocalDate.now().minusDays(7));
        toDatePicker.setValue(LocalDate.now());
    }


    private void setupTable() {
        TableColumn<Order, Long> idCol = new TableColumn<>("№");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getOrderDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));

        TableColumn<Order, String> customerCol = new TableColumn<>("Клиент");
        customerCol.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getCustomer().getFullName()));

        TableColumn<Order, Double> totalCol = new TableColumn<>("Сумма");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalCol.setCellFactory(column -> new TableCell<Order, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("%.2f руб.", price));
            }
        });

        ordersTable.getColumns().setAll(idCol, dateCol, customerCol, totalCol);
    }

    @FXML
    private void loadOrders() {
        LocalDate from = fromDatePicker.getValue() != null
                ? fromDatePicker.getValue()
                : LocalDate.now().minusDays(7);

        LocalDate to = toDatePicker.getValue() != null
                ? toDatePicker.getValue()
                : LocalDate.now();

        try {
            List<Order> orders = orderService.getOrdersByPeriodWithDetails(from, to);
            ordersTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            FxUtil.showErrorAlert("Ошибка", "Не удалось загрузить заказы: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showOrderDetails() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            FxUtil.showErrorAlert("Ошибка", "Выберите заказ!");
            return;
        }

        try {
            // Загружаем заказ с деталями
            Order fullOrder = orderService.getOrderWithDetails(selected.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/orders/order_details.fxml"));
            loader.setControllerFactory(context::getBean);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            OrderDetailsController controller = loader.getController();
            controller.setOrder(fullOrder);  // Передаем полностью загруженный заказ

            stage.setTitle("Детали заказа #" + fullOrder.getId());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            FxUtil.showErrorAlert("Ошибка", "Не удалось загрузить детали заказа: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
