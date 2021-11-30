package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.entity.Item;
import lk.ijse.pos_system.entity.Orders;
import lk.ijse.pos_system.repository.custom.OrderDetailRepo;
import lk.ijse.pos_system.entity.OrderDetail;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;

public class OrderDetailRepoImpl implements OrderDetailRepo {

    private Session session;

    @Override
    public boolean add(OrderDetail orderDetail) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(OrderDetail orderToRemove) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(OrderDetail orderDetailToBeUpdated) throws SQLException, ClassNotFoundException {

        int id = (int) session.createQuery("SELECT orderDetailId FROM OrderDetail WHERE order = :orderId and item = :itemCode")
                .setParameter("orderId", session.load(Orders.class, orderDetailToBeUpdated.getOrder().getOrderID()))
                .setParameter("itemCode", session.load(Item.class, orderDetailToBeUpdated.getItem().getItemCode()))
                .setMaxResults(1).uniqueResult();

        orderDetailToBeUpdated.setOrderDetailId(id);
        session.update(orderDetailToBeUpdated);
        return true;
    }

    @Override
    public int getOrderQTY(String itemCode) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        int orderQty = (int) session.createQuery("SELECT orderQTY FROM OrderDetail WHERE item = :itemCode")
                .setParameter("itemCode", itemCode).uniqueResult();
        transaction.commit();
        session.close();
        return orderQty;
    }

    @Override
    public boolean updateOrderQty(String orderId, String itemCode, int newOrderQty, double unitPrice, int packSize, int discountPerUnit) throws SQLException, ClassNotFoundException {
        double newDiscount = unitPrice * packSize * newOrderQty * discountPerUnit / 100;

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        OrderDetail orderDetail = new OrderDetail(
                session.load(Orders.class,orderId),
                session.load(Item.class,itemCode),
                newOrderQty,
                newDiscount
        );
        session.update(orderDetail);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
