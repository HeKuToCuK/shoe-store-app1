package com.shoeshop.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

@Controller
public class HelpController {
    @FXML
    private Accordion accordion;

    @FXML
    public void initialize() {
        try {
            loadHelpContent();
            System.out.println("HelpController initialized successfully"); // Логирование
        } catch (Exception e) {
            System.err.println("Error in HelpController: " + e.getMessage()); // Логирование ошибки
            e.printStackTrace();
            accordion.getPanes().add(new TitledPane("Ошибка", new Label("Не удалось загрузить справку")));
        }
    }

    private void loadHelpContent() {
        System.out.println("Загрузка контента справки..."); // Логирование


        if (accordion == null) {
            System.err.println("Accordion не инициализирован!");
            return;
        }

        TitledPane testPane = new TitledPane("Тестовая панель", new Label("Работает!"));
        accordion.getPanes().add(testPane);

        System.out.println("Добавлено панелей: " + accordion.getPanes().size()); // Логирование
    }

}