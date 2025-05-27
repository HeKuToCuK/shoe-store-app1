package com.shoeshop.controller;

import com.shoeshop.model.Order;
import com.shoeshop.model.User;
import com.shoeshop.service.AuthService;
import com.shoeshop.service.OrderService;
import com.shoeshop.util.FxUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class OrderHistoryController {
    @FXML private TableView<Order> ordersTable;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthService authService;

    private User currentUser;

    public void initialize(User user) {
        this.currentUser = user;
        setupTableColumns();
        loadOrderData();
    }

    private void loadOrderData() {
        try {
            if (currentUser == null) {
                FxUtil.showErrorAlert("Ошибка", "Пользователь не авторизован");
                return;
            }

            List<Order> orders = orderService.getOrdersForCustomer(currentUser.getId());
            ordersTable.setItems(FXCollections.observableArrayList(orders));

            if (orders.isEmpty()) {
                FxUtil.showInfoAlert("Информация", "У вас пока нет заказов");
            }
        } catch (Exception e) {
            FxUtil.showErrorAlert("Ошибка", "Не удалось загрузить историю заказов");
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        // Очищаем существующие колонки
        ordersTable.getColumns().clear();

        // Колонка с номером заказа
        TableColumn<Order, Long> idCol = new TableColumn<>("№ заказа");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Колонка с датой заказа
        TableColumn<Order, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getOrderDate()
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                )
        );

        // Колонка с суммой заказа
        TableColumn<Order, Double> totalCol = new TableColumn<>("Сумма");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalCol.setCellFactory(column -> new TableCell<Order, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("%.2f руб.", price));
            }
        });

        // Добавляем колонки в таблицу
        ordersTable.getColumns().addAll(idCol, dateCol, totalCol);
    }
}