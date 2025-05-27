package com.shoeshop.controller;

import com.shoeshop.model.Product;
import com.shoeshop.model.Supplier;
import com.shoeshop.model.User;
import com.shoeshop.service.AuthService;
import com.shoeshop.service.ProductService;
import com.shoeshop.service.SupplierService;
import com.shoeshop.util.FxUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductsController {
    @FXML private TableView<Product> productsTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<Supplier> supplierFilter;
    @FXML private Button refreshButton;
    @FXML private Button addButton;

    private final ApplicationContext context;
    private final ProductService productService;
    private final SupplierService supplierService;

    @Autowired
    public ProductsController(ApplicationContext context,
                              ProductService productService,
                              SupplierService supplierService) {
        this.context = context;
        this.productService = productService;
        this.supplierService = supplierService;
    }
    @Autowired
    private AuthService authService;

    @FXML
    public void initialize() {
        configureUI();
        setupTable();
        setupSupplierFilter();
        loadInitialData();
        setupEventHandlers();
    }


    private void setupTable() {
        productsTable.getColumns().clear();

        TableColumn<Product, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> supplierCol = new TableColumn<>("Производитель");
        supplierCol.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getSupplier().getName()));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("%.2f руб.", price));
            }
        });
        TableColumn<Product, Integer> stockCol = new TableColumn<>("Остаток");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        stockCol.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (!empty) {
                    setText(String.valueOf(quantity));
                    // Подсветка низкого остатка
                    if (quantity < 5) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                } else {
                    setText(null);
                }
            }
        });
        productsTable.getColumns().addAll(nameCol, supplierCol, priceCol, stockCol);
    }

    private void setupSupplierFilter() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        Supplier allSuppliers = new Supplier();
        allSuppliers.setName("Все поставщики");
        suppliers.add(0, allSuppliers);

        supplierFilter.setItems(FXCollections.observableArrayList(suppliers));
        supplierFilter.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier supplier) {
                return supplier.getName();
            }
            @Override
            public Supplier fromString(String string) {
                return null;
            }
        });
        supplierFilter.getSelectionModel().selectFirst();
    }

    private void loadInitialData() {
        refreshProducts();
    }

    private void setupEventHandlers() {
        // Поиск при изменении текста
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            handleSearch();
        });

        // Обновление при выборе поставщика
        supplierFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            handleSearch();
        });
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        Supplier selectedSupplier = supplierFilter.getValue();

        List<Product> products;
        if (selectedSupplier == null || selectedSupplier.getName().equals("Все поставщики")) {
            products = productService.findByNameContainingIgnoreCase(searchText);
        } else {
            products = productService.findBySupplierAndNameContainingIgnoreCase(selectedSupplier, searchText);
        }

        productsTable.setItems(FXCollections.observableArrayList(products));
    }

    @FXML
    private void refreshProducts() {
        searchField.clear();
        supplierFilter.getSelectionModel().selectFirst();
        List<Product> products = productService.getAllAvailableProducts();
        productsTable.setItems(FXCollections.observableArrayList(products));
    }

    @FXML
    private void openAddProductDialog() {
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/products/add_product.fxml"));
                loader.setControllerFactory(context::getBean);
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));

                AddProductController controller = loader.getController();
                controller.setStage(stage);

                stage.setTitle("Добавить товар");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                refreshProducts();
        } catch (IOException e) {
            FxUtil.showErrorAlert("Ошибка", "Не удалось открыть диалог добавления товара");
            e.printStackTrace();
        }

    }
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        configureUI(); // Обновляем интерфейс
    }

    private void configureUI() {
        if (authService.isCustomer(currentUser)) { // Теперь проверяем явно
            addButton.setDisable(true);
            addButton.setStyle("-fx-opacity: 0.5; -fx-cursor: default;");
        } else {
            addButton.setDisable(false);
            addButton.setStyle("");
        }
    }
}