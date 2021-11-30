package lk.ijse.pos_system.dto;

import lk.ijse.pos_system.entity.Item;
import lk.ijse.pos_system.entity.Orders;

public class OrderDetailDTO {

    private String orderDetailId;
    private Orders order;
    private Item item;
    private String orderID;
    private String itemCode;
    private int orderQTY;
    private double discount;

    public OrderDetailDTO() {}

    public OrderDetailDTO(String orderID, String itemCode) {
        this.setOrderID(orderID);
        this.setItemCode(itemCode);
    }

    public OrderDetailDTO(Orders order, Item item) {
        this.setOrder(order);
        this.setItem(item);
    }

    public OrderDetailDTO(String orderID, String itemCode, int orderQTY, double discount) {
        this.orderID = orderID;
        this.itemCode = itemCode;
        this.orderQTY = orderQTY;
        this.discount = discount;
    }

    public OrderDetailDTO(String orderDetailId, Orders order, Item item, int orderQTY, double discount) {
        this.setOrderDetailId(orderDetailId);
        this.setOrder(order);
        this.setItem(item);
        this.setOrderQTY(orderQTY);
        this.setDiscount(discount);
    }

    public String getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(String orderDetailId) {
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

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
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
    public String toString() {
        return "OrderDetailDTO{" +
                "orderDetailId='" + orderDetailId + '\'' +
                ", order=" + order +
                ", item=" + item +
                ", orderID='" + orderID + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", orderQTY=" + orderQTY +
                ", discount=" + discount +
                '}';
    }
}
