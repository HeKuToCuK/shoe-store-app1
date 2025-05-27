package com.shoeshop.service;

import com.shoeshop.model.Order;
import com.shoeshop.model.OrderDetail;
import com.shoeshop.model.Product;
import com.shoeshop.model.User;
import com.shoeshop.repository.OrderRepository;
import com.shoeshop.repository.ProductRepository;
import com.shoeshop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order createOrder(Order order, List<OrderDetail> details) {
        if (order.getOrderDetails() == null) {
            order.setOrderDetails(new ArrayList<>());
        }

        details.forEach(detail -> {
            detail.setOrder(order);
            order.getOrderDetails().add(detail);
        });

        double total = details.stream()
                .mapToDouble(d -> d.getQuantity() * d.getPrice())
                .sum();
        order.setTotalPrice(total);

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByPeriodWithDetails(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return orderRepository.findByOrderDateBetweenWithDetails(start, end);
    }

    public List<Order> getOrdersForCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return orderRepository.findByCustomer(customer);
    }

    public List<Order> getOrdersByPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        return orderRepository.findByOrderDateBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );
    }
    @Transactional
    public Order getOrderWithDetails(Long orderId) {
        return orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

}