package lk.ijse.pos_system.business.custom.impl;

import lk.ijse.pos_system.business.custom.PurchaseOrderBO;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.*;
import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.dto.CustomerDTO;
import lk.ijse.pos_system.dto.ItemDTO;
import lk.ijse.pos_system.dto.OrderDTO;
import lk.ijse.pos_system.dto.OrderDetailDTO;
import lk.ijse.pos_system.entity.Customer;
import lk.ijse.pos_system.entity.Item;
import lk.ijse.pos_system.entity.OrderDetail;
import lk.ijse.pos_system.entity.Orders;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderBOImpl implements PurchaseOrderBO {

    private final CustomerRepo customerRepo = (CustomerRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.CUSTOMER);
    private final ItemRepo itemRepo = (ItemRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ITEM);
    private final OrderRepo orderRepo = (OrderRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDER);
    private final OrderDetailRepo orderDetailRepo = (OrderDetailRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDERDETAIL);
    private final DiscountRepo discountRepo = (DiscountRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.DISCOUNT);

    @Override
    public boolean purchaseOrder(OrderDTO orderDTO, ArrayList<OrderDetailDTO> items) throws SQLException, ClassNotFoundException {

        Customer customer = new Customer(orderDTO.getCustID());

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailDTO odt : items) {
            orderDetails.add(new OrderDetail(
                    new Orders(orderDTO.getOrderID()),
                    new Item(odt.getItemCode()),
                    odt.getOrderQTY(),
                    odt.getDiscount()
            ));
        }

        Orders order = new Orders(
                orderDTO.getOrderID(),
                orderDTO.getDate(),
                orderDTO.getOrderCost(),
                customer,
                orderDetails
        );

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        /*boolean isOrderPlaced = false;
        if (orderRepo.add(order)) {
            for (OrderDetail odt : orderDetails) {
                isOrderPlaced =  itemRepo.updateQtyOnHand(odt.getItem().getItemCode(),odt.getOrderQTY());
            }
        }*/

        try {
            orderRepo.setSession(session);
            itemRepo.setSession(session);

            orderRepo.add(order);
            for (OrderDetail odt : orderDetails) {
                itemRepo.updateQtyOnHand(odt.getItem().getItemCode(), odt.getOrderQTY());
            }
            return true;

        } finally {
            transaction.commit();
            session.close();
        }
    }

    @Override
    public String getOrderId() throws SQLException, ClassNotFoundException {
        return orderRepo.getOrderId();
    }

    @Override
    public List<String> getCustomerIds() throws SQLException, ClassNotFoundException {
        return customerRepo.getCustomerIds();
    }

    @Override
    public List<String> getItemCodes() throws SQLException, ClassNotFoundException {
        return itemRepo.getItemCodes();
    }

    @Override
    public List<String> getItemDescriptions() throws SQLException, ClassNotFoundException {
        return itemRepo.getItemDescriptions();
    }

    @Override
    public ItemDTO getItem(String itemCode) throws SQLException, ClassNotFoundException {
        Item item = itemRepo.getItem(itemCode);
        return new ItemDTO(
                item.getItemCode(),
                item.getDescription(),
                item.getPackSize(),
                item.getUnitPrice(),
                item.getQtyOnHand()
        );
    }

    @Override
    public String getDiscount(String itemCode) throws SQLException, ClassNotFoundException {
        return discountRepo.getDiscount(itemCode);
    }

    @Override
    public String getItemCode(String description) throws SQLException, ClassNotFoundException {
        return itemRepo.getItemCode(description);
    }

    @Override
    public CustomerDTO getCustomer(String custIdForSearch) throws SQLException, ClassNotFoundException {
        Customer customer = customerRepo.getCustomer(custIdForSearch);
        System.out.println("customer impl : "+customer);
        return new CustomerDTO(
                customer.getCustID(),
                customer.getCustTitle(),
                customer.getCustName(),
                customer.getCustAddress(),
                customer.getCity(),
                customer.getProvince(),
                customer.getPostalCode()
        );
    }

    @Override
    public boolean addCustomer(CustomerDTO odt) throws SQLException, ClassNotFoundException {
        System.out.println("newCust 3 : "+odt);
        return customerRepo.add(new Customer(
                odt.getCustID(),
                odt.getCustTitle(),
                odt.getCustName(),
                odt.getCustAddress(),
                odt.getCity(),
                odt.getProvince(),
                odt.getPostalCode()
        ));
    }

    @Override
    public int splitPackSize(String itemCode, String packSize) throws SQLException, ClassNotFoundException {
        return itemRepo.splitPackSize(itemCode, packSize);
    }

    @Override
    public String generateCustomerID() throws SQLException, ClassNotFoundException {
        return customerRepo.generateCustomerID();
    }

    @Override
    public String generateInvoiceId(String invoiceNo) throws SQLException, ClassNotFoundException {
        return orderRepo.generateInvoiceId(invoiceNo);
    }

    @Override
    public CustomerDTO getCustomerOfOrder(String orderId) throws SQLException, ClassNotFoundException {
        return customerRepo.getCustomerOfOrder(orderId);
    }
}
