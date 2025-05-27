package com.shoeshop.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FxUtil {
    //region Alert Dialogs
    public static void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    public static void showInfoAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    public static void showWarningAlert(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    public static Optional<ButtonType> showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //endregion

    public static void saveReceiptToFile(String receiptText) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить чек");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(receiptText);
                showInfoAlert("Успешно", "Чек сохранен в файл: " + file.getAbsolutePath());
            } catch (IOException e) {
                showErrorAlert("Ошибка", "Не удалось сохранить чек");
            }
        }
    }
    //region Window Management
    public static void loadWindow(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(FxUtil.class.getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Ошибка", "Не удалось загрузить окно: " + e.getMessage());
        }
    }

    public static void loadModalWindow(String fxmlPath, String title, Stage owner) {
        try {
            Parent root = FXMLLoader.load(FxUtil.class.getResource(fxmlPath));
            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showErrorAlert("Ошибка", "Ошибка загрузки модального окна: " + e.getMessage());
        }
    }
    //endregion

    //region Table Utilities
    public static <T, S> void configureTableColumn(
            TableColumn<T, S> column,
            String propertyName,
            Callback<TableColumn<T, S>, TableCell<T, S>> cellFactory) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        if (cellFactory != null) {
            column.setCellFactory(cellFactory);
        }
    }
    //endregion

    //region Validation
    public static boolean validateEmail(TextField field) {
        String email = field.getText().trim();
        boolean isValid = email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        if (!isValid) {
            showErrorAlert("Ошибка", "Некорректный формат email");
            field.requestFocus();
        }
        return isValid;
    }

    public static boolean validateNumber(TextField field) {
        try {
            Double.parseDouble(field.getText());
            return true;
        } catch (NumberFormatException e) {
            showErrorAlert("Ошибка", "Введите число");
            field.requestFocus();
            return false;
        }
    }
    //endregion

    //region Formatting
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    //endregion
}