package com.shoeshop.controller;

import com.shoeshop.model.Role;
import com.shoeshop.config.SecurityConfig;
import com.shoeshop.model.User;
import com.shoeshop.repository.RoleRepository;
import com.shoeshop.repository.UserRepository;
import com.shoeshop.service.UserService;
import com.shoeshop.util.FxUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomersController {
    private final ApplicationContext context;
    @FXML
    private TableView<User> customersTable;
    @FXML private TextField searchField;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public CustomersController(ApplicationContext context) {
        this.context = context;
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing CustomersController..."); // Логирование
        setupTable();
        loadCustomers();
    }

    private void loadCustomers() {
        List<User> customers = userRepository.findByRoleRole_name("CUSTOMER");
        if (customers.isEmpty()) {
            System.out.println("No customers found!");
            // Создаем тестовых клиентов при первом запуске
            createTestCustomers();
            customers = userRepository.findByRoleRole_name("CUSTOMER");
        }
        customersTable.setItems(FXCollections.observableArrayList(customers));
    }

    private void createTestCustomers() {
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        User customer1 = new User();
        customer1.setFullName("Иванов Иван Иванович");
        customer1.setPhoneNumber("+79161234567");
        customer1.setEmail("ivanov@example.com");
        customer1.setRole(customerRole);

        User customer2 = new User();
        customer2.setFullName("Петрова Мария Сергеевна");
        customer2.setPhoneNumber("+79031234568");
        customer2.setEmail("petrova@example.com");
        customer2.setRole(customerRole);

        userRepository.saveAll(List.of(customer1, customer2));
        System.out.println("Created test customers");
    }

    private void setupTable() {
        customersTable.getColumns().clear();

        TableColumn<User, String> nameCol = new TableColumn<>("ФИО");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setPrefWidth(200);

        TableColumn<User, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(150);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        customersTable.getColumns().addAll(nameCol, phoneCol, emailCol);
    }


    @FXML
    private void refreshCustomers() {
        String search = searchField.getText();
        List<User> customers = userService.findCustomers(search);
        customersTable.setItems(FXCollections.observableArrayList(customers));
    }

    @FXML
    private void addCustomer() {
        try {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Добавить покупателя");

            // Создаем поля формы
            TextField usernameField = new TextField();
            PasswordField passwordField = new PasswordField();
            TextField fullNameField = new TextField();
            TextField emailField = new TextField();
            TextField phoneField = new TextField();

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.addRow(0, new Label("Логин:"), usernameField);
            grid.addRow(1, new Label("Пароль:"), passwordField);
            grid.addRow(2, new Label("ФИО:"), fullNameField);
            grid.addRow(3, new Label("Email:"), emailField);
            grid.addRow(4, new Label("Телефон:"), phoneField);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    User customer = new User();
                    customer.setUsername(usernameField.getText());
                    customer.setPasswordHash(passwordEncoder.encode(passwordField.getText()));
                    customer.setFullName(fullNameField.getText());
                    customer.setEmail(emailField.getText());
                    customer.setPhoneNumber(phoneField.getText());
                    customer.setCreatedAt(LocalDateTime.now());

                    // Устанавливаем роль "CUSTOMER" (ID = 10)
                    Role customerRole = new Role();
                    customerRole.setRole_id(10L);
                    customer.setRole(customerRole);

                    return customer;
                }
                return null;
            });

            Optional<User> result = dialog.showAndWait();
            result.ifPresent(customer -> {
                userRepository.save(customer);
                refreshCustomers();
            });

        } catch (Exception e) {
            FxUtil.showErrorAlert("Ошибка", "Не удалось добавить покупателя: " + e.getMessage());
            e.printStackTrace();
        }


}
}
