package lk.ijse.pos_system.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Orders {
    @Id
    private String orderID;
    @CreationTimestamp
    private Date date;
    private double orderCost;
    @ManyToOne
    @JoinColumn(name = "customerId"/*, referencedColumnName = "customerId"*/)
    private Customer customer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public Orders() {
    }

    public Orders(String orderID) {
        this.setOrderID(orderID);
    }

    public Orders(String orderID, Date date, double orderCost, Customer customer) {
        this.orderID = orderID;
        this.date = date;
        this.orderCost = orderCost;
        this.customer = customer;
    }

    public Orders(String orderID, double orderCost) {
        this.orderID = orderID;
        this.orderCost = orderCost;
    }

    public Orders(String orderID, Date date, double orderCost, Customer customer, List<OrderDetail> orderDetails) {
        this.setOrderID(orderID);
        this.setDate(date);
        this.setOrderCost(orderCost);
        this.setCustomer(customer);
        this.setOrderDetails(orderDetails);
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

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderID='" + orderID + '\'' +
                ", date=" + date +
                ", orderCost=" + orderCost +
                ", customer=" + customer +
                '}';
    }
}
