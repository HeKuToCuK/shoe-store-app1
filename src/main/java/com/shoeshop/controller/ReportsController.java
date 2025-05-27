package com.shoeshop.controller;

import com.shoeshop.model.Order;
import com.shoeshop.model.SalesReport;
import com.shoeshop.service.ReportService;
import com.shoeshop.util.FxUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ReportsController {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Order> reportTable;
    @FXML private Label totalLabel;

    @Autowired
    private ReportService reportService;

    @FXML
    public void initialize() {
        startDatePicker.setValue(LocalDate.now().minusDays(7));
        endDatePicker.setValue(LocalDate.now());

        setupTable();
        System.out.println("ReportsController initialized"); // Отладочное сообщение
    }

    private void setupTable() {
        reportTable.getColumns().clear();

        TableColumn<Order, Long> idCol = new TableColumn<>("№ заказа");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(cd -> {
            LocalDateTime orderDate = cd.getValue().getOrderDate();
            String formattedDate = orderDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            return new SimpleStringProperty(formattedDate);
        });

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

        TableColumn<Order, String> paymentCol = new TableColumn<>("Метод оплаты");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        reportTable.getColumns().addAll(idCol, dateCol, customerCol, totalCol, paymentCol);
    }

    @FXML
    private void generateReport() {
        try {
            System.out.println("Generating report..."); // Отладочное сообщение

            LocalDateTime start = startDatePicker.getValue().atStartOfDay();
            LocalDateTime end = endDatePicker.getValue().atTime(23, 59, 59);

            System.out.println("Period: " + start + " - " + end); // Отладочное сообщение

            List<Order> orders = reportService.getOrdersByPeriod(start, end);

            System.out.println("Found " + orders.size() + " orders"); // Отладочное сообщение

            if (orders.isEmpty()) {
                FxUtil.showInfoAlert("Информация", "Нет данных за выбранный период");
            }

            reportTable.setItems(FXCollections.observableArrayList(orders));

            double total = orders.stream()
                    .mapToDouble(Order::getTotalPrice)
                    .sum();

            totalLabel.setText(String.format("Итого: %.2f руб.", total));

            System.out.println("Report generated successfully"); // Отладочное сообщение
        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
            FxUtil.showErrorAlert("Ошибка", "Не удалось сформировать отчет: " + e.getMessage());
        }
    }
}