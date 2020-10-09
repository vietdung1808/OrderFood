package com.nasugar.orderfood.model;

import java.util.List;

public class Orders {
    private String orderId;
    private String userId;
    private String userName;
    private String orderDate;
    private String address;
    private String totalAmount;
    private int status;
    private List<Cart> foodList;

    public Orders() {
    }

    public Orders(String orderId, String userId, String userName, String orderDate, String address, String totalAmount, int status, List<Cart> foodList) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.orderDate = orderDate;
        this.address = address;
        this.totalAmount = totalAmount;
        this.status = status;
        this.foodList = foodList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Cart> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Cart> foodList) {
        this.foodList = foodList;
    }
}
