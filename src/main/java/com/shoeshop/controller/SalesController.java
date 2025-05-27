package com.shoeshop.controller;

import com.shoeshop.model.Order;
import com.shoeshop.model.OrderDetail;
import com.shoeshop.model.Product;
import com.shoeshop.model.User;
import com.shoeshop.repository.UserRepository;
import com.shoeshop.service.AuthService;
import com.shoeshop.service.OrderService;
import com.shoeshop.service.ProductService;
import com.shoeshop.util.FxUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class SalesController {
    @FXML private TableView<Product> productsTable;
    @FXML private TableView<OrderDetail> cartTable;
    @FXML private Label totalLabel;
    @FXML private TextField clientNameField;
    @FXML private ComboBox<String> paymentMethodCombo;

    @Autowired private ProductService productService;
    @Autowired private OrderService orderService;
    @Autowired
    private UserRepository userRepository;

    private ObservableList<OrderDetail> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupProductsTable();
        setupCartTable();
        loadProducts();

        paymentMethodCombo.getItems().addAll("Наличные", "Карта", "Онлайн");
        paymentMethodCombo.getSelectionModel().selectFirst();

        List<User> customers = userRepository.findByRoleRole_name("CUSTOMER");
        clientCombo.getItems().setAll(customers);
        clientCombo.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user.getFullName() + " (" + user.getUsername() + ")";
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
    }

    private void setupProductsTable() {
        TableColumn<Product, String> nameCol = new TableColumn<>("Товар");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        productsTable.getColumns().setAll(nameCol, priceCol);
    }

    private void setupCartTable() {
        TableColumn<OrderDetail, String> productCol = new TableColumn<>("Товар");
        productCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getProduct().getName()));

        TableColumn<OrderDetail, Integer> quantityCol = new TableColumn<>("Кол-во");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<OrderDetail, Double> sumCol = new TableColumn<>("Сумма");
        sumCol.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getQuantity() * cd.getValue().getPrice()).asObject());

        cartTable.getColumns().setAll(productCol, quantityCol, sumCol);
        cartTable.setItems(cartItems);


        cartItems.addListener((ListChangeListener.Change<? extends OrderDetail> c) -> updateTotal());
    }

    @FXML
    private void addToCart() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Optional<OrderDetail> existing = cartItems.stream()
                    .filter(item -> item.getProduct().getId().equals(selected.getId()))
                    .findFirst();

            if (existing.isPresent()) {
                existing.get().setQuantity(existing.get().getQuantity() + 1);
            } else {
                OrderDetail item = new OrderDetail();
                item.setProduct(selected);
                item.setQuantity(1);
                item.setPrice(selected.getPrice());
                cartItems.add(item);
            }
            cartTable.refresh();
        }
    }

    @FXML
    private void removeFromCart() {
        OrderDetail selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItems.remove(selected);
        }
    }

    @Autowired
    private AuthService authService;

    @FXML
    private void createOrder() {
        try {
            User seller = authService.getCurrentSeller();
            if (seller == null) {
                FxUtil.showErrorAlert("Ошибка", "Требуется повторный вход в систему");
                forceLogout();
                return;
            }
            if (cartItems.isEmpty()) {
                FxUtil.showErrorAlert("Ошибка", "Корзина пуста");
                return;
            }

            User customer = clientCombo.getSelectionModel().getSelectedItem();
            if (customer == null) {
                FxUtil.showErrorAlert("Ошибка", "Выберите клиента");
                return;
            }


            Order order = new Order();
            order.setCustomer(customer);
            order.setEmployee(seller);
            order.setOrderDate(LocalDateTime.now());
            order.setPaymentMethod(paymentMethodCombo.getValue());
            order.setStatus("Завершен");

            Order savedOrder = orderService.createOrder(order, new ArrayList<>(cartItems));
            printReceipt(savedOrder);
            cartItems.clear();

            FxUtil.showInfoAlert("Успешно", "Заказ #" + savedOrder.getId() + " оформлен");

        } catch (Exception e) {
            FxUtil.showErrorAlert("Ошибка оформления заказа", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void printReceipt(Order order) {
        List<OrderDetail> details = order.getOrderDetails() != null
                ? order.getOrderDetails()
                : Collections.emptyList();

        StringBuilder receipt = new StringBuilder();
        receipt.append("ЧЕК #").append(order.getId()).append("\n");

        for (OrderDetail item : details) {
            receipt.append(String.format("%s x%d = %.2f руб.\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getQuantity() * item.getPrice()));
        }

        receipt.append("ИТОГО: ").append(order.getTotalPrice()).append(" руб.");
        FxUtil.showInfoAlert("Чек", receipt.toString());
    }

    private void updateTotal() {
        double total = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
        totalLabel.setText(String.format("Итого: %.2f руб.", total));
    }

    private void loadProducts() {
        try {
            List<Product> products = productService.getAll();
            productsTable.setItems(FXCollections.observableArrayList(products));
        } catch (Exception e) {
            FxUtil.showErrorAlert("Ошибка", "Не удалось загрузить товары");
            e.printStackTrace();
        }
    }

    @FXML
    private void clearCart() {
        cartItems.clear();
        updateTotal();
    }

    private void forceLogout() {
        SecurityContextHolder.clearContext();
    }
    @FXML
    private ComboBox<User> clientCombo;

}