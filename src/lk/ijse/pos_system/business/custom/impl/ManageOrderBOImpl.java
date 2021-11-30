package lk.ijse.pos_system.business.custom.impl;

import lk.ijse.pos_system.business.custom.ManageOrderBO;
import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.dto.OrderDTO;
import lk.ijse.pos_system.dto.OrderDetailDTO;
import lk.ijse.pos_system.entity.Customer;
import lk.ijse.pos_system.entity.Item;
import lk.ijse.pos_system.entity.OrderDetail;
import lk.ijse.pos_system.entity.Orders;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.*;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageOrderBOImpl implements ManageOrderBO {

    private final CustomerRepo customerRepo = (CustomerRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.CUSTOMER);
    private final ItemRepo itemRepo = (ItemRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ITEM);
    private final OrderRepo orderRepo = (OrderRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDER);
    private final OrderDetailRepo orderDetailRepo = (OrderDetailRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDERDETAIL);
    private final QueryRepo queryRepo = (QueryRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.QUERY);
    private final DiscountRepo discountRepo = (DiscountRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.DISCOUNT);

    @Override
    public List<String> searchOrdersByCustID(String custID) throws SQLException, ClassNotFoundException {
        return orderRepo.searchOrdersByCustID(custID);
    }

    @Override
    public ArrayList<CustomDTO> getOrderedItems(String orderSelected, CustomDTO itemSelected) throws SQLException, ClassNotFoundException {
        return queryRepo.getOrderedItems(orderSelected, itemSelected);
    }

    @Override
    public ArrayList<Double> getOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException {
        return queryRepo.getOldPaymentInfo(orderSelected);
    }

    @Override
    public int splitPackSize(String itemCode, String packSize) throws SQLException, ClassNotFoundException {
        return itemRepo.splitPackSize(itemCode, packSize);
    }

    @Override
    public boolean deleteItemFromOrder(OrderDetailDTO dto) throws SQLException, ClassNotFoundException {
        return orderDetailRepo.delete(new OrderDetail(orderRepo.getOrder(dto.getOrderID()), itemRepo.getItem(dto.getItemCode())));
    }

    @Override
    public boolean editQtyOnHand(String itemCode, int qtyToRestock) throws SQLException, ClassNotFoundException {
        return itemRepo.editQtyOnHand(itemCode, qtyToRestock);
    }

    @Override
    public void deleteOrder(OrderDTO dto) throws SQLException, ClassNotFoundException {
        orderRepo.delete(new Orders(dto.getOrderID()));
    }

    @Override
    public boolean updateOrder(OrderDTO orderDTO, OrderDetailDTO orderDetailDTO, int newQtyOnHand) throws SQLException, ClassNotFoundException {

        OrderDetail orderDetail = new OrderDetail(
                new Orders(orderDTO.getOrderID()),
                new Item(orderDetailDTO.getItemCode()),
                orderDetailDTO.getOrderQTY(),
                orderDetailDTO.getDiscount()
        );

        Orders order = new Orders(
                orderDTO.getOrderID(),
                orderDTO.getDate(),
                orderDTO.getOrderCost(),
                new Customer(orderDTO.getCustID())
        );

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        /*boolean isOrderUpdated;
        if (orderRepo.update(order)) {
            if (orderDetailRepo.update(orderDetail)) {
                //isOrderUpdated = itemRepo.updateEditedQtyOnHand(orderDetailDTO.getItemCode(), newQtyOnHand);
                if (itemRepo.updateEditedQtyOnHand(orderDetailDTO.getItemCode(), newQtyOnHand)) {
                    isOrderUpdated = true;
                }
            }
        }*/
        try {
            orderRepo.setSession(session);
            orderDetailRepo.setSession(session);
            itemRepo.setSession(session);

            orderRepo.update(order);
            orderDetailRepo.update(orderDetail);
            itemRepo.updateEditedQtyOnHand(orderDetailDTO.getItemCode(), newQtyOnHand);
            return true;

        } finally {
            transaction.commit();
            session.close();
        }
    }

    @Override
    public boolean updateOrderAndOrderDetail(OrderDTO orderDTO, OrderDetailDTO orderDetailDTO) throws SQLException, ClassNotFoundException {
        OrderDetail orderDetail = new OrderDetail(
                new Orders(orderDTO.getOrderID()),
                new Item(orderDetailDTO.getItemCode()),
                orderDetailDTO.getOrderQTY(),
                orderDetailDTO.getDiscount()
        );

        Orders order = new Orders(
                orderDTO.getOrderID(),
                orderDTO.getDate(),
                orderDTO.getOrderCost(),
                orderDTO.getCustomer()
        );

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        /*boolean isOrderUpdated;
        if (orderRepo.update(order)) {
            isOrderUpdated = orderDetailRepo.update(orderDetail);

        }*/
        try {
            orderRepo.setSession(session);
            orderDetailRepo.setSession(session);

            orderRepo.update(order);
            orderDetailRepo.update(orderDetail);
            return true;

        } finally {
            transaction.commit();
            session.close();
        }

    }

    @Override
    public String getDiscount(String itemCode) throws SQLException, ClassNotFoundException {
        return discountRepo.getDiscount(itemCode);
    }

    @Override
    public boolean isCustomerExists(String custID) throws SQLException, ClassNotFoundException {
        return customerRepo.isCustomerExists(custID);
    }
}
