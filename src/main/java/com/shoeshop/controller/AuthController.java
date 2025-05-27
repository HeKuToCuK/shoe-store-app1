package com.shoeshop.controller;

import com.shoeshop.model.User;
import com.shoeshop.service.AuthService;
import com.shoeshop.util.FxUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class AuthController {
    private final AuthService authService;
    private final ApplicationContext context;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @Autowired
    public AuthController(AuthService authService, ApplicationContext context) {
        this.authService = authService;
        this.context = context;
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        try {
            User user = authService.authenticateAndGetUser(
                    usernameField.getText(),
                    passwordField.getText()
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole_name()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication); // Важно!

            if ("SELLER".equals(user.getRole().getRole_name())) {
                authService.loginAsSeller(user.getUsername(), passwordField.getText());
            }

            openMainWindow(user, event);
        } catch (AuthenticationException e) {
            FxUtil.showErrorAlert("Ошибка", e.getMessage());
        }
    }

    private void openMainWindow(User user, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.initialize(user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Node) event.getSource()).getScene().getWindow().hide();
        } catch (IOException e) {
            FxUtil.showErrorAlert("Ошибка", "Не удалось загрузить главное окно");
            e.printStackTrace();
        }
    }
}