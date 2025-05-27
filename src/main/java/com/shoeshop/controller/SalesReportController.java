package com.shoeshop.controller;

import com.shoeshop.service.ReportService;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;

@Controller
public class SalesReportController {
    @FXML
    private LineChart<String, Number> salesChart;

    @Autowired
    private ReportService reportService;

    @FXML
    private void loadChart() {
        salesChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ежедневные продажи");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        reportService.getDailySales().forEach((date, sum) -> {
            String formattedDate = date.format(formatter);
            series.getData().add(new XYChart.Data<>(formattedDate, sum));
        });

        salesChart.getData().add(series);
    }
}
