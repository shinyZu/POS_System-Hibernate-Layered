package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.repository.custom.ItemRepo;
import lk.ijse.pos_system.entity.Item;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRepoImpl implements ItemRepo {

    private Session session;

    @Override
    public boolean add(Item newItem) throws SQLException, ClassNotFoundException {

        if (duplicateEntryExists(newItem)) {
            return false;
        }

        /*return CrudUtil.executeUpdate("INSERT INTO Item VALUES(?,?,?,?,?)",
                newItem.getItemCode(),
                newItem.getDescription(),
                newItem.getPackSize(),
                newItem.getUnitPrice(),
                newItem.getQtyOnHand()
        );*/
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.save(newItem);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Item item) throws SQLException, ClassNotFoundException {

        /*if (CrudUtil.executeUpdate("DELETE FROM Item WHERE itemCode = ?",item.getItemCode())){
            return true;
        }else{
            return false;
        }*/

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.delete(session.load(Item.class,item.getItemCode()));
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(Item editItem) throws SQLException, ClassNotFoundException {

        /*return CrudUtil.executeUpdate("UPDATE Item SET description=?, packSize=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?",
                editItem.getDescription(),
                editItem.getPackSize(),
                editItem.getUnitPrice(),
                editItem.getQtyOnHand(),
                editItem.getItemCode()
        );*/
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.update(editItem);
        transaction.commit();
        session.close();
        return true;

    }

    @Override
    public String generateItemCode() throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        NativeQuery query = session.createSQLQuery("SELECT i.itemCode FROM Item i ORDER BY i.itemCode DESC LIMIT 1");
        String newItemCode = (String) query.uniqueResult();
        transaction.commit();
        session.close();

        if (newItemCode != null) {
            int tempId = Integer.parseInt(newItemCode.split("-")[1]);
            tempId = tempId + 1;

            if (tempId <= 9) {
                return "I-00" + tempId;
            } else if (tempId <= 99) {
                return "I-0" + tempId;
            } else {
                return "I-" + tempId;
            }
        } else {
            return "I-001";
        }

        /*ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Item ORDER BY itemCode DESC LIMIT 1");
        if (rst.next()){
            int tempId = Integer.parseInt(rst.getString(1).split("-")[1]);
            tempId = tempId+1;

            if (tempId <= 9){
                return "I-00" + tempId;
            }else if (tempId <= 99){
                return "I-0" + tempId;
            }else {
                return "I-" + tempId;
            }

        }else {
            return "I-001";
        }*/
    }

    @Override
    public Item getItem(String itemCode) throws SQLException, ClassNotFoundException {

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        Item item = session.get(Item.class, itemCode);
        transaction.commit();
        session.close();

        if (item != null) {
            return item;
        }
        return null;
        /*ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Item WHERE itemCode = ?", itemCode);

        if (rst.next()) {
            return new Item(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getInt(5)
            );
        } else {
            return null;
        }*/
    }

    @Override
    public ArrayList<String> getItemCodes() throws SQLException, ClassNotFoundException {
        ArrayList<String> itemCodeList = new ArrayList<>();

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        List<Item> itemList = session.createQuery("FROM Item").list();
        transaction.commit();
        session.close();

        for (Item item : itemList) {
            itemCodeList.add(item.getItemCode());
        }
        return itemCodeList;

        /*ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Item");

        ArrayList<String> itemCodeList = new ArrayList<>();

        if (rst == null) {
            return null;
        } else {
            while (rst.next()) {
                itemCodeList.add(
                        rst.getString(1)
                );
            }
        }
        return itemCodeList;*/
    }

    @Override
    public List<String> getItemDescriptions() throws SQLException, ClassNotFoundException {
        ArrayList<String> itemDescrpList = new ArrayList<>();
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        List<String> resultSet = session.createQuery("SELECT description FROM Item").list();
        transaction.commit();
        session.close();

        for (String description : resultSet) {
            itemDescrpList.add(description);
        }
        return itemDescrpList;

        /*ResultSet rst = CrudUtil.executeQuery("SELECT description FROM Item");
        ArrayList<String> itemDescrpList = new ArrayList<>();

        if (rst == null) {
            return null;
        } else {
            while (rst.next()) {
                itemDescrpList.add(
                        rst.getString(1)
                );
            }
        }
        return itemDescrpList;*/
    }

    @Override
    public String getItemCode(String description) throws SQLException, ClassNotFoundException {

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("SELECT itemCode FROM Item WHERE description = :description");
        String itemCode = (String) query.setParameter("description", description).uniqueResult();
        transaction.commit();
        session.close();
        return itemCode;

        /*ResultSet resultSet = CrudUtil.executeQuery("SELECT itemCode FROM Item WHERE description = ?", description);

        if (resultSet.next()) {
            return resultSet.getString(1);

        } else {
            return null;
        }*/
    }

    @Override
    public boolean duplicateEntryExists(Item newItem) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        Item item = (Item) session.createQuery("FROM Item WHERE itemCode = :itemCode")
                .setParameter("itemCode", newItem.getItemCode()).uniqueResult();
        transaction.commit();
        session.close();

        return item != null;

        /*ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Item WHERE itemCode = ?", newItem.getItemCode());

        if (rst.next()) {
            return true;
        }
        return false;*/
    }

    @Override
    public boolean updateQtyOnHand(String itemCode, int orderQty) throws SQLException, ClassNotFoundException {

        /*Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();*/

        int currentQtyOnHand = (int) session.createQuery("SELECT qtyOnHand FROM Item WHERE itemCode = :itemCode")
                .setParameter("itemCode", itemCode).uniqueResult();

        boolean isUpdated = session.createQuery("UPDATE Item SET qtyOnHand = :qtyOnHand WHERE itemCode = :itemCode")
                .setParameter("qtyOnHand", (currentQtyOnHand - orderQty))
                .setParameter("itemCode", itemCode).executeUpdate() > 0;

        /*transaction.commit();
        session.close();*/

        return isUpdated;

        /*ResultSet rst = CrudUtil.executeQuery("SELECT qtyOnHand FROM Item WHERE itemCode = ?", itemCode);

        int currentQtyOnHand = 0;
        if (rst.next()) {
            currentQtyOnHand = Integer.parseInt(rst.getString(1));
        }

        return CrudUtil.executeUpdate("UPDATE Item SET qtyOnHand = ? WHERE itemCode = ?",(currentQtyOnHand - orderQty),itemCode);*/
    }

    @Override
    public String getQtyOnHand(String itemCode) throws SQLException, ClassNotFoundException {

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        int qtyOnHand = (int) session.createQuery("SELECT qtyOnHand FROM Item WHERE itemCode = :itemCode")
                .setParameter("itemCode", itemCode).uniqueResult();
        transaction.commit();
        session.close();
        if (qtyOnHand != 0) {
            return String.valueOf(qtyOnHand);
        }
        return null;

        /*ResultSet resultSet = CrudUtil.executeQuery("SELECT qtyOnHand FROM Item WHERE itemCode = ?", itemCode);

        if (resultSet.next()) {
            return resultSet.getString(1);

        } else {
            return null;
        }*/
    }

    @Override
    public boolean editQtyOnHand(String itemCode, int qtyBackToStock) throws SQLException, ClassNotFoundException {
        //return CrudUtil.executeUpdate("UPDATE Item SET qtyOnHand = ? WHERE itemCode = ?",qtyBackToStock,itemCode);
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        boolean isUpdated = session.createQuery("UPDATE Item SET qtyOnHand = :qtyOnHand WHERE itemCode = :itemCode")
                .setParameter("qtyOnHand", qtyBackToStock)
                .setParameter("itemCode", itemCode).executeUpdate() > 0;
        transaction.commit();
        session.close();
        return isUpdated;
    }

    @Override
    public boolean updateEditedQtyOnHand(String itemCode, int newQtyOnHand) throws SQLException, ClassNotFoundException {
        //return CrudUtil.executeUpdate("UPDATE Item SET qtyOnHand = ?  WHERE itemCode = ?",newQtyOnHand,itemCode);
       /* Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();*/
        boolean isUpdated = session.createQuery("UPDATE Item SET qtyOnHand = :qtyOnHand WHERE itemCode = :itemCode")
                .setParameter("qtyOnHand", newQtyOnHand)
                .setParameter("itemCode", itemCode).executeUpdate() > 0;
        /*transaction.commit();
        session.close();*/
        return isUpdated;
    }

    @Override
    public int splitPackSize(String itemCode, String txtPackSize) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        String tempPckSize = (String) session.createQuery("SELECT packSize FROM Item WHERE itemCode = :itemCode")
                .setParameter("itemCode", itemCode).uniqueResult();
        transaction.commit();
        session.close();

        if (tempPckSize != null) {
            return Integer.parseInt(tempPckSize.split("")[0]);
        }
        return 0;

        /*ResultSet rst = CrudUtil.executeQuery("SELECT packSize FROM Item WHERE itemCode = ?", itemCode);

        int tempPckSize;
        if (rst.next()) {
            tempPckSize = Integer.parseInt(rst.getString(1).split(" ")[0]);
            return tempPckSize;
        }
        return 0;*/
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
