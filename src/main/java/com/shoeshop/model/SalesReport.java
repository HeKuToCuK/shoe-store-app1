package com.shoeshop.model;

import java.util.List;

public class SalesReport {
    private List<Order> orders;
    private double totalRevenue;

    public SalesReport(List<Order> orders, double totalRevenue) {
        this.orders = orders;
        this.totalRevenue = totalRevenue;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}