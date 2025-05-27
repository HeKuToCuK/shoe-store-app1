package com.shoeshop.controller;

import com.shoeshop.model.User;
import com.shoeshop.service.AuthService;
import com.shoeshop.service.OrderService;
import com.shoeshop.service.ProductService;
import com.shoeshop.service.UserService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainController {
    @FXML private TabPane tabPane;
    private final ApplicationContext context;
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    public MainController(ApplicationContext context,
                          ProductService productService,
                          OrderService orderService,
                          AuthService authService) {
        this.context = context;
        this.productService = productService;
        this.orderService = orderService;
        this.authService = authService;
    }
    private Node createSalesView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sales/sales.fxml"));
            loader.setControllerFactory(context::getBean);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Не удалось загрузить интерфейс продаж");
        }
    }
    private final AuthService authService;

    private User currentUser;

    public MainController(ApplicationContext context, AuthService authService, ProductService productService, OrderService orderService) {
        this.context = context;
        this.authService = authService;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void initialize(User user) {
        this.currentUser = user;
        setupTabsBasedOnRole(user.getRole().getRole_name());
        Tab salesTab = new Tab("Продажи");
        salesTab.setContent(createSalesView());
        tabPane.getTabs().add(salesTab);
    }

    private void setupTabsBasedOnRole(String roleName) {
        tabPane.getTabs().clear();

        Tab productsTab = new Tab("Товары");
        productsTab.setContent(createProductsView());
        tabPane.getTabs().add(productsTab);

        switch (roleName.toUpperCase()) {
            case "ADMIN":
                addAdminTabs();
                break;
            case "SELLER":
                addSellerTabs();
                break;
            case "CUSTOMER":
                addCustomerTabs();
                break;
        }
    }
    private void addProductsTab() {
        Tab productsTab = new Tab("Товары");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sales/sales.fxml)"));
            loader.setControllerFactory(context::getBean);
            productsTab.setContent(loader.load());
        } catch (IOException e) {
            productsTab.setContent(new Label("Ошибка загрузки списка товаров"));
        }
        tabPane.getTabs().add(productsTab);
    }
    private void addAdminTabs() {
        if (!currentUser.getRole().getRole_name().equalsIgnoreCase("ADMIN")) {
            return;
        }

        Tab usersTab = new Tab("Пользователи");
        usersTab.setContent(createUsersView());

        Tab reportsTab = new Tab("Отчеты");
        reportsTab.setContent(createReportsView());

        tabPane.getTabs().addAll(usersTab, reportsTab);
    }

    private Node createUsersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/users.fxml"));
            loader.setControllerFactory(context::getBean);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Не удалось загрузить список пользователей");
        }
    }
    private void addSellerTabs() {
        Tab ordersTab = new Tab("Заказы");
        ordersTab.setContent(createOrdersView());

        Tab customersTab = new Tab("Клиенты");
        customersTab.setContent(createCustomersView());

        tabPane.getTabs().addAll(ordersTab, customersTab);
    }

    private void addCustomerTabs() {
        Tab historyTab = new Tab("История заказов");
        historyTab.setContent(createOrderHistoryView());
        tabPane.getTabs().add(historyTab);
    }

    private Node createProductsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/products/products.fxml"));
            loader.setControllerFactory(context::getBean);
            Node productsNode = loader.load();

            ProductsController controller = loader.getController();
            controller.setCurrentUser(currentUser); // Передаем текущего пользователя

            return productsNode;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Не удалось загрузить интерфейс товаров");
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }


    private Node createCustomersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customers/customers.fxml"));
            loader.setControllerFactory(context::getBean);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Не удалось загрузить интерфейс клиентов");
        }
    }

    private Node createOrdersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/orders/orders.fxml"));
            loader.setControllerFactory(context::getBean);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Не удалось загрузить интерфейс заказов");
        }
    }
    private Node createReportsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"));
            loader.setControllerFactory(context::getBean);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Не удалось загрузить отчеты");
        }
    }

    private void addDefaultTabs() {
        Tab helpTab = new Tab("Помощь");
        helpTab.setContent(createHelpView());
        tabPane.getTabs().add(helpTab);
    }
    @Autowired
    private UserService userService;

    @FXML
    private void handleHelpButton(ActionEvent event) {
        System.out.println("Кнопка 'Помощь' нажата!");

        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Справка");
        helpAlert.setHeaderText("Инструкция по использованию");

        String helpText =
                "1. Авторизуйтесь в системе под нужным пользователям.\n" +
                        "2. Взависимости от роли:\n" +
                        "   - Клиент: просмотр истории заказов и ассортимента\n" +
                        "   - Продавец: управление товарами, клиентами и оформление заказов.\n" +
                        "   - Администратор: управление товарами, сотрудниками и продажами.\n" +
                        "3. По прочим вопросам обращайтесь к Автору\n\n" +
                        "© Никита Потапов +78005553535";

        helpAlert.setContentText(helpText);

        helpAlert.showAndWait();
    }

    @FXML
    private Node createHelpView() {
        try {
            System.out.println("Загрузка help.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"));
            loader.setControllerFactory(context::getBean);
            Node helpNode = loader.load();

            helpNode.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            return helpNode;

        } catch (IOException e) {
            System.err.println("Ошибка загрузки help.fxml: " + e.getMessage());
            return new Label("Ошибка загрузки справки");
        }
    }

    private Node createSuppliersView() {
        return new VBox(new Label("Поставщики"));
    }

    private Node createInventoryView() {
        return new VBox(new Label("Инвентаризация"));
    }

    private Node createCartView() {
        return new VBox(new Label("Корзина"));
    }

    private Node createProfileView() {
        return new VBox(new Label("Профиль пользователя"));
    }


    private Node createOrderHistoryView() {
        try {
            if (currentUser == null) {
                return new Label("Требуется авторизация");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customers/order_history.fxml"));
            loader.setControllerFactory(context::getBean);
            Node orderHistoryNode = loader.load();

            OrderHistoryController controller = loader.getController();
            controller.initialize(currentUser); // Передаём текущего пользователя

            return orderHistoryNode;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Ошибка загрузки истории заказов");
        }
    }
}
