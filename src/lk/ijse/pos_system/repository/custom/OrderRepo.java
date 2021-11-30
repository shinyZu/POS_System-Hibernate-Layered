package lk.ijse.pos_system.repository.custom;

import lk.ijse.pos_system.entity.Orders;
import lk.ijse.pos_system.repository.CrudRepo;

import java.sql.SQLException;
import java.util.List;

public interface OrderRepo extends CrudRepo<Orders, String> {

    Orders getOrder(String orderId) throws SQLException, ClassNotFoundException;

    String getOrderId() throws SQLException, ClassNotFoundException;

    //String getOrderDetailId() throws SQLException, ClassNotFoundException;

    List<String> searchOrdersByCustID(String custID) throws SQLException, ClassNotFoundException;

    boolean updateOrderCost(String orderIdSelected, double newOrderCost) throws SQLException, ClassNotFoundException;

    String generateInvoiceId(String invoiceNo) throws SQLException, ClassNotFoundException;

}
