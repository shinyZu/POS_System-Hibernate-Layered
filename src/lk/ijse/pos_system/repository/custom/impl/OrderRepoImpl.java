package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.entity.Customer;
import lk.ijse.pos_system.repository.custom.OrderRepo;
import lk.ijse.pos_system.entity.Orders;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.sql.SQLException;
import java.util.List;

public class OrderRepoImpl implements OrderRepo {

    private Session session;

    @Override
    public boolean add(Orders newOrder) throws SQLException, ClassNotFoundException {
        boolean isSaved = true;
        if (newOrder.getCustomer().getCustID().equals("")){
            isSaved = session.createSQLQuery("INSERT INTO Orders (orderID,orderDate,orderCost) VALUES (:id,:date,:cost)")
                    .setParameter("id", newOrder.getOrderID())
                    .setParameter("date", newOrder.getDate())
                    .setParameter("cost", newOrder.getOrderCost()).executeUpdate() > 0;
        } else {
            session.save(newOrder);
        }
        return isSaved;
    }

    @Override
    public boolean delete(Orders orderToRemove) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.delete(orderToRemove);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(Orders orderToBeUpdated) throws SQLException, ClassNotFoundException {
        session.update(orderToBeUpdated);
        return true;
    }

    @Override
    public Orders getOrder(String orderId) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        Orders order = session.load(Orders.class, orderId);
        transaction.commit();
        session.close();
        return order;
    }

    @Override
    public String getOrderId() throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

            NativeQuery query = session.createSQLQuery("SELECT orderID FROM Orders ORDER BY orderId DESC LIMIT 1");
            String newOrderId = (String) query.uniqueResult();
            transaction.commit();
            session.close();

        if (newOrderId != null) {
            int tempId = Integer.parseInt(newOrderId.split("-")[1]);
            tempId = tempId+1;

            if (tempId <= 9){
                return "O-00" + tempId;
            }else if (tempId <= 99){
                return "O-0" + tempId;
            }else {
                return "O-" + tempId;
            }
        } else {
            return "O-001";
        }
    }

    @Override
    public List<String> searchOrdersByCustID(String custID) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        List<String> orderList = session.createQuery("SELECT orderID FROM Orders WHERE customer = :customerId")
                .setParameter("customerId", session.load(Customer.class,custID)).list();

        transaction.commit();
        session.close();
        return orderList;
    }

    @Override
    public boolean updateOrderCost(String orderIdSelected, double newOrderCost) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        Orders order = new Orders(
                orderIdSelected,
                newOrderCost
        );
        session.update(order);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public String generateInvoiceId(String invoiceNo) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        NativeQuery query = session.createSQLQuery("SELECT orderID FROM Orders ORDER BY orderId DESC LIMIT 1");
        String invoiceId = (String) query.uniqueResult();
        transaction.commit();
        session.close();

        if (invoiceId != null) {
            String id = invoiceId.split("-")[1];
            id = "INV-"+id;
            return id;
        } else {
            return "INV-001";
        }
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
