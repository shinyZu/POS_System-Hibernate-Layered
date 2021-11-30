package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.repository.custom.CustomerRepo;
import lk.ijse.pos_system.dto.CustomerDTO;
import lk.ijse.pos_system.entity.Customer;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.List;

public class CustomerRepoImpl implements CustomerRepo {

    private Session session;

    @Override
    public boolean add(Customer newCust) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.save(newCust);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Customer customer) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not Supported Yet");
    }

    @Override
    public boolean update(Customer customer) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not Supported Yet");
    }

    @Override
    //@Transactional
    public Customer getCustomer(String id) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        /*Customer customer = (Customer) session.createQuery("FROM Customer WHERE custID = :customerId")
                .setParameter("customerId", id).uniqueResult();*/

        Customer customer = session.get(Customer.class, id);

        /*Customer proxy = session.load(Customer.class, id);
        System.out.println(customer);
        System.out.println(customer.getOrderList());
        System.out.println("proxy : "+proxy);
        System.out.println("proxy_Id : "+proxy.getCustID());
        System.out.println("proxy_Name : "+proxy.getCustName());
        System.out.println("proxy_List : "+proxy.getOrderList());*/

        transaction.commit();
        session.close();

        /*System.out.println(customer);
        System.out.println(customer.getOrderList());
        System.out.println("proxy : "+proxy);
        System.out.println("proxy_Name : "+proxy.getCustName());*/

        return customer;
    }

    @Override
    public List<String> getCustomerIds() throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        List<String> resultSet = session.createQuery("SELECT custID FROM Customer").list();
        transaction.commit();
        session.close();
        return resultSet;
    }

    @Override
    public String generateCustomerID() throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        String custID = (String) session.createSQLQuery("SELECT customerId FROM Customer ORDER BY customerId DESC LIMIT 1").uniqueResult();
        transaction.commit();
        session.close();

        if (custID != null) {
            int tempId = Integer.parseInt(custID.split("-")[1]);
            tempId = tempId+1;

            if (tempId <= 9){
                return "C-00" + tempId;
            }else if (tempId <= 99){
                return "C-0" + tempId;
            }else {
                return "C-" + tempId;
            }
        } else {
            return "C-001";
        }
    }

    @Override
    public CustomerDTO getCustomerOfOrder(String orderId) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        /*String[] result = (String[]) session.createQuery("SELECT c.custID, c.custTitle, c.custName " +
                "FROM Customer c INNER JOIN Orders o " +
                "ON c.custID = o.customer " +
                "WHERE o.orderID = :orderId").setParameter("orderId", orderId).uniqueResult();*/
        Customer customer = session.get(Customer.class, orderId);

        transaction.commit();
        session.close();

        /*if (result != null) {
            return new CustomerDTO(result[0],result[1],result[2]);
        }*/

        if (customer != null) {
            return new CustomerDTO(
                    customer.getCustID(),
                    customer.getCustTitle(),
                    customer.getCustName()
            );
        }
        return null;
    }

    @Override
    public boolean isCustomerExists(String custID) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        Customer customer = (Customer) session.createQuery("FROM Customer WHERE custID = :customerId")
                .setParameter("customerId", custID).uniqueResult();
        transaction.commit();
        session.close();
        return customer != null;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
