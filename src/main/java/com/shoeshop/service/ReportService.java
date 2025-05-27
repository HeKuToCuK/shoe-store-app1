package com.shoeshop.service;

import com.shoeshop.model.Order;
import com.shoeshop.model.SalesReport;
import com.shoeshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getOrdersByPeriod(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByOrderDateBetween(start, end);
    }

    public double getTotalSalesByPeriod(LocalDateTime start, LocalDateTime end) {
        return orderRepository.sumTotalByPeriod(start, end);
    }

    public Map<LocalDate, Double> getDailySales() {
        return orderRepository.findDailySalesGroupedByDate();
    }
}
