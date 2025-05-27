package com.shoeshop.controller;

import com.shoeshop.model.Role;
import com.shoeshop.model.User;
import com.shoeshop.repository.RoleRepository;
import com.shoeshop.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;

    @Autowired private UserService userService;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().getRole_name())
        );
        loadUsers();
    }

    private void loadUsers() {
        usersTable.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
    }

    @FXML
    private void addUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Добавить пользователя");

        // Создаем поля формы
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField fullNameField = new TextField();
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList(
                "ADMIN", "SELLER", "CUSTOMER"
        ));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Логин:"), usernameField);
        grid.addRow(1, new Label("Пароль:"), passwordField);
        grid.addRow(2, new Label("ФИО:"), fullNameField);
        grid.addRow(3, new Label("Роль:"), roleCombo);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                User user = new User();
                user.setUsername(usernameField.getText());
                user.setPasswordHash(passwordEncoder.encode(passwordField.getText()));
                user.setFullName(fullNameField.getText());
                user.setCreatedAt(LocalDateTime.now());

                Role role = roleRepository.findByName(roleCombo.getValue())
                        .orElseThrow(() -> new RuntimeException("Роль не найдена"));
                user.setRole(role);

                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            userService.saveUser(user);
            loadUsers();
        });
    }

    @FXML
    private void deleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            userService.deleteUser(selected.getId());
            loadUsers();
        }
    }
}