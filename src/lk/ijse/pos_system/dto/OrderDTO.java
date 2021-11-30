package lk.ijse.pos_system.dto;

import lk.ijse.pos_system.entity.Customer;

import java.util.Date;

public class OrderDTO {

    private String orderID;
    private Date date;
    private String custID;
    private double orderCost;

    private Customer customer;

    public OrderDTO() {
    }

    public OrderDTO(String orderID) {
        this.setOrderID(orderID);
    }

    public OrderDTO(String orderID, String custID, double orderCost) {
        this.orderID = orderID;
        this.custID = custID;
        this.orderCost = orderCost;
    }

    public OrderDTO(String orderID, Date date, String custID, double orderCost) {
        this.setOrderID(orderID);
        this.setDate(date);
        this.setCustID(custID);
        this.setOrderCost(orderCost);
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public double getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(double orderCost) {
        this.orderCost = orderCost;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderID='" + orderID + '\'' +
                ", date=" + date +
                ", custID='" + custID + '\'' +
                ", orderCost=" + orderCost +
                ", customer=" + customer +
                '}';
    }
}
