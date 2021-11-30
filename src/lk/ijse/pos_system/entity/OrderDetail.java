package lk.ijse.pos_system.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int orderDetailId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "orderID", referencedColumnName = "orderID")
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "itemCode", referencedColumnName = "itemCode")
    private Item item;

    private int orderQTY;
    @Column(columnDefinition = "double default '0.0'")
    private double discount;

    public OrderDetail() {}

    public OrderDetail(Orders order, Item item) {
        this.setOrder(order);
        this.setItem(item);
    }

    public OrderDetail(int orderDetailId, Orders order, Item item, int orderQTY, double discount) {
        this.setOrderDetailId(orderDetailId);
        this.setOrder(order);
        this.setItem(item);
        this.setOrderQTY(orderQTY);
        this.setDiscount(discount);
    }

    public OrderDetail(Orders order, Item item, int orderQTY, double discount) {
        this.order = order;
        this.item = item;
        this.orderQTY = orderQTY;
        this.discount = discount;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getOrderQTY() {
        return orderQTY;
    }

    public void setOrderQTY(int orderQTY) {
        this.orderQTY = orderQTY;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetail that = (OrderDetail) o;
        return orderDetailId == that.orderDetailId &&
                orderQTY == that.orderQTY &&
                Double.compare(that.discount, discount) == 0 &&
                Objects.equals(order, that.order) &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderDetailId, order, item, orderQTY, discount);
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailId=" + orderDetailId +
                ", order=" + order +
                ", item=" + item +
                ", orderQTY=" + orderQTY +
                ", discount=" + discount +
                '}';
    }
}
