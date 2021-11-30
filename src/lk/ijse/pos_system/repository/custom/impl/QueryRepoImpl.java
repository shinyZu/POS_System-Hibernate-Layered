package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.entity.Orders;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.ItemRepo;
import lk.ijse.pos_system.repository.custom.QueryRepo;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryRepoImpl implements QueryRepo {

    private final ItemRepo itemRepo = (ItemRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ITEM);
    private Session session;

    @Override
    public ArrayList<CustomDTO> getCustomerWiseIncome(String date) throws SQLException, ClassNotFoundException {
        ArrayList<CustomDTO> custIncomeTable = new ArrayList<>();
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        List<CustomDTO> resultSet = session.createQuery("SELECT o.customer, c.custTitle, c.custName, c.city, SUM(o.orderCost)\n" +
                "FROM Orders o INNER JOIN Customer c\n" +
                "ON c.custID = o.customer\n" +
                "WHERE o.date = :orderDate\n" +
                "GROUP BY o.customer").setParameter("orderDate", date).list();
        transaction.commit();
        session.close();

        for (CustomDTO dto : resultSet) {
            custIncomeTable.add(dto);
        }
        return custIncomeTable;
    }

    @Override
    public ArrayList<CustomDTO> getOrderedItems(String orderSelected, CustomDTO itemSelected) throws SQLException, ClassNotFoundException {

        ArrayList<CustomDTO> listOfOrderedItems = new ArrayList<>();
        int discountAsPercentage = 0;

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        if (itemSelected != null) {
            discountAsPercentage = Integer.valueOf((String) session.createQuery("SELECT discount FROM Discount WHERE itemCode = :code")
                    .setParameter("code", itemSelected.getItemCode()).uniqueResult());
        }

        List<Object[]> resultSet = session.createQuery("SELECT i.itemCode, i.description, i.packSize, i.unitPrice, od.orderQTY, od.discount\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.item\n" +
                "WHERE od.order = :orderId").setParameter("orderId", session.load(Orders.class, orderSelected)).list();

        double discount = 0;

        for (Object[] obj : resultSet) {
            int packSize = itemRepo.splitPackSize(String.valueOf(obj[0]), String.valueOf(obj[2]));
            double unitPrice = Double.parseDouble(String.valueOf(obj[3]));
            int orderQty = Integer.parseInt(String.valueOf(obj[4]));
            double subTotal = unitPrice * packSize * orderQty;

            if (discountAsPercentage != 0) {
                discount = subTotal * discountAsPercentage / 100;
            } else {
                discount = Double.parseDouble(String.valueOf(obj[5]));
            }

            double total = subTotal - discount;

            listOfOrderedItems.add(new CustomDTO(
                    (String) obj[0],
                    (String) obj[1],
                    (String) obj[2],
                    unitPrice,
                    orderQty,
                    subTotal,
                    discount,
                    total
            ));
        }
        transaction.commit();
        session.close();
        return listOfOrderedItems;
    }

    @Override
    public ArrayList<Double> getOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException {

        ArrayList<Double> oldPaymentInfo = new ArrayList<>();

        double orderSubtotal = 0;
        double orderDiscount = 0;
        double orderTotal = 0;

        double unitPrice = 0;
        int packSize = 0;
        int orderQty = 0;

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        List<Object[]> resultSet = session.createQuery("SELECT od.order, i.itemCode, i.unitPrice, i.packSize, od.orderQTY, od.discount\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.item\n" +
                "WHERE od.order = :orderId AND i.itemCode IN (SELECT item FROM OrderDetail WHERE order = :orderID)")
                .setParameter("orderId", session.load(Orders.class, orderSelected))
                .setParameter("orderID", session.load(Orders.class, orderSelected)).list();

        /*for (CustomDTO dto : resultSet) {
            unitPrice = dto.getUnitPrice();
            packSize = Integer.parseInt(dto.getPackSize().split(" ")[0]);
            orderQty = dto.getOrderQTY();

            orderDiscount += dto.getDiscount();
            orderSubtotal += (unitPrice * packSize * orderQty);
            orderTotal += (orderSubtotal - orderDiscount);
        }*/

        for (Object[] obj : resultSet) {
            unitPrice = Double.parseDouble(String.valueOf(obj[2]));
            packSize = Integer.parseInt(String.valueOf(obj[3]).split(" ")[0]);
            orderQty = Integer.parseInt(String.valueOf(obj[4]));

            orderDiscount += Double.parseDouble(String.valueOf(obj[5]));
            orderSubtotal += (unitPrice * packSize * orderQty);
            orderTotal += (orderSubtotal - orderDiscount);
        }

        orderTotal = (double) session.createQuery("SELECT orderCost FROM Orders WHERE orderID = :orderId")
                .setParameter("orderId", orderSelected).uniqueResult();

        oldPaymentInfo.add(0, orderSubtotal);
        oldPaymentInfo.add(1, orderDiscount);
        oldPaymentInfo.add(2, orderTotal);

        transaction.commit();
        session.close();
        return oldPaymentInfo;
    }

    @Override
    public ArrayList<CustomDTO> getAllItems() throws SQLException, ClassNotFoundException {  // INNER JOIN
        ArrayList<CustomDTO> itemsWithDiscountList = new ArrayList<>();

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        List<Object[]> itemList = session.createQuery("SELECT i.itemCode, i.description,i.packSize,i.unitPrice,d.discount,i.qtyOnHand\n" +
                "FROM Item i LEFT JOIN Discount d\n" +
                "ON i.itemCode = d.itemCode").list();

        transaction.commit();
        session.close();

        /*for (CustomDTO dto : itemList) {
            itemsWithDiscountList.add(new CustomDTO(
                    dto.getItemCode(),
                    dto.getDescription(),
                    dto.getPackSize(),
                    dto.getUnitPrice(),
                    dto.getDiscount(),
                    dto.getQtyOnHand())
            );
        }*/

        for (Object[] obj : itemList) {
            itemsWithDiscountList.add(new CustomDTO(
                    (String) obj[0],
                    (String) obj[1],
                    (String) obj[2],
                    Double.parseDouble(String.valueOf(obj[3])),
                    Double.parseDouble(String.valueOf(obj[4])),
                    Integer.parseInt(String.valueOf(obj[5]))
            ));
        }
        return itemsWithDiscountList;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

   /* @Override
    public ArrayList<CustomDTO> getDailyReport(String date) throws SQLException, ClassNotFoundException {
        ArrayList<CustomDTO> dailyReportTable = new ArrayList<>();

        ResultSet rst1 = CrudUtil.executeQuery("SELECT i.itemCode, i.unitPrice, i.packSize,  SUM(od.orderQTY)\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "WHERE o.orderDate = ?\n" +
                "GROUP BY od.itemCode\n" +
                "ORDER BY od.itemCode", date);

            ResultSet rst2 = CrudUtil.executeQuery("SELECT i.itemCode, i.description, SUM(i.unitPrice * i.packSize * od.orderQTY * (100-d.discount) / 100)\n" +
                    "FROM Item i LEFT JOIN Discount d\n" +
                    "ON i.itemCode = d.itemCode\n" +
                    "INNER JOIN OrderDetail od\n" +
                    "ON i.itemCode = od.itemCode\n" +
                    "INNER JOIN Orders o\n" +
                    "ON o.orderID = od.orderId\n" +
                    "WHERE o.orderDate = ?\n" +
                    "GROUP BY i.itemCode\n" +
                    "ORDER BY i.itemCode", date);

        while (rst2.next()) {
            if (rst1.next()) {
                if (rst1.getString(1).equals(rst2.getString(1))) {
                    // dailyReportTable.add(rst2.getString(1), rst2.getString(2), rst1.getInt(2),rst2.getDouble(3));
                    String itemCode = rst2.getString(1);
                    String description = rst2.getString(2);
                    int qtySold = rst1.getInt(4);
                    double income = rst2.getDouble(3);

                    double unitPrice = rst1.getDouble(2);
                    int packSize = itemRepo.splitPackSize(itemCode,rst1.getString(3));
                    int orderQTY = rst1.getInt(4);

                    if (income == 0) {
                        income = unitPrice * packSize * orderQTY;
                    }

                    dailyReportTable.add(new CustomDTO(
                            itemCode,
                            description,
                            qtySold,
                            income
                    ));
                }
            }
        }
        return dailyReportTable;
    }

    @Override
    public ArrayList<CustomDTO> getMonthlyReport(String date) throws SQLException, ClassNotFoundException {
        String str = String.valueOf(date);
        String[] arrOfStr = str.split("-");
        String d = arrOfStr[1];

        ArrayList<CustomDTO> monthlyReportTable = new ArrayList<>();
        ResultSet rst1 = CrudUtil.executeQuery("SELECT i.itemCode, i.unitPrice, i.packSize, SUM(od.orderQTY)\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "where orderDate LIKE '"+"____-"+d+"-__'\n"+
                "GROUP BY od.itemCode\n" +
                "ORDER BY od.itemCode");

        ResultSet rst2 = CrudUtil.executeQuery("SELECT i.itemCode, i.description, SUM(i.unitPrice * i.packSize * od.orderQTY * (100-d.discount) / 100)\n" +
                "FROM Item i LEFT JOIN Discount d\n" +
                "ON i.itemCode = d.itemCode\n" +
                "INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "where orderDate LIKE '"+"____-"+d+"-__'\n" +
                "GROUP BY i.itemCode\n" +
                "ORDER BY i.itemCode");

        while (rst2.next()) {
            if (rst1.next()) {
                if (rst1.getString(1).equals(rst2.getString(1))) {
                    // dailyReportTable.add(rst2.getString(1), rst2.getString(2), rst1.getInt(2),rst2.getDouble(3));
                    String itemCode = rst2.getString(1);
                    String description = rst2.getString(2);
                    int qtySold = rst1.getInt(4);
                    double income = rst2.getDouble(3);

                    double unitPrice = rst1.getDouble(2);
                    int packSize = itemRepo.splitPackSize(itemCode,rst1.getString(3));
                    int orderQTY = rst1.getInt(4);

                    if (income == 0) {
                        income = unitPrice * packSize * orderQTY;
                    }

                    monthlyReportTable.add(new CustomDTO(
                            itemCode,
                            description,
                            qtySold,
                            income
                    ));
                }
            }
        }
        return monthlyReportTable;
    }

    @Override
    public ArrayList<CustomDTO> getAnnualReport(String date) throws SQLException, ClassNotFoundException {
        String str = String.valueOf(date);
        String[] arrOfStr = str.split("-");
        String yy = arrOfStr[0];

        ArrayList<CustomDTO> annualReportTable = new ArrayList<>();
        ResultSet rst1 = CrudUtil.executeQuery("SELECT i.itemCode, i.unitPrice, i.packSize, SUM(od.orderQTY)\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "where orderDate LIKE '"+yy+"-__-__'\n" +
                "GROUP BY od.itemCode\n" +
                "ORDER BY od.itemCode");

        ResultSet rst2 = CrudUtil.executeQuery("SELECT i.itemCode, i.description, SUM(orderCost) \n" +
                "FROM Item i LEFT JOIN Discount d\n" +
                "ON i.itemCode = d.itemCode\n" +
                "INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "where o.orderDate LIKE '"+yy+"-__-__'\n" +
                "GROUP BY i.itemCode\n" +
                "ORDER BY i.itemCode");

        while (rst2.next()) {
            if (rst1.next()) {
                if (rst1.getString(1).equals(rst2.getString(1))) {
                    // dailyReportTable.add(rst2.getString(1), rst2.getString(2), rst1.getInt(2),rst2.getDouble(3));
                    String itemCode = rst2.getString(1);
                    String description = rst2.getString(2);
                    int qtySold = rst1.getInt(4);
                    double income = rst2.getDouble(3);

                    double unitPrice = rst1.getDouble(2);
                    int packSize = itemRepo.splitPackSize(itemCode,rst1.getString(3));
                    int orderQTY = rst1.getInt(4);

                    if (income == 0) {
                        income = unitPrice * packSize * orderQTY;
                    }

                    annualReportTable.add(new CustomDTO(
                            itemCode,
                            description,
                            qtySold,
                            income
                    ));
                }
            }
        }
        return annualReportTable;
    }

    @Override
    public String getMostMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException {
        String mostMovableItem = "I-000";

        String[] arrOfStr = date.split("-");
        String mm = null;
        String yy = null;

        ResultSet rst = null;

        if (reportType.equals("Daily Report")) {
            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate = ? \n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) DESC LIMIT 1;",date);

        } else if (reportType.equals("Monthly Report")) {
            mm = arrOfStr[1];

            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate LIKE '"+"____-"+mm+"-__'\n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) DESC LIMIT 1;");

        } else if (reportType.equals("Annual Report")) {
            yy = arrOfStr[0];

            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate LIKE '"+yy+"-__-__'\n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) DESC LIMIT 1;");
        }

        if (rst.next()) {
            mostMovableItem = rst.getString(1);
            return mostMovableItem;
        }
        return mostMovableItem;
    }

    @Override
    public String getLeastMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException {
        String leastMovableItem = "I-000";

        String[] arrOfStr = date.split("-");
        String mm = null;
        String yy = null;

        ResultSet rst = null;

        if (reportType.equals("Daily Report")) {
            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate = ? \n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) \n" +
                    "ASC LIMIT 1;",date);

        } else if (reportType.equals("Monthly Report")) {
            mm = arrOfStr[1];

            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate LIKE '"+"____-"+mm+"-__'\n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) \n" +
                    "ASC LIMIT 1;");

        } else if (reportType.equals("Annual Report")) {
            yy = arrOfStr[0];

            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate LIKE '"+yy+"-__-__'\n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) \n" +
                    "ASC LIMIT 1;");
        }

        if (rst.next()) {
            leastMovableItem = rst.getString(1);
            return leastMovableItem;
        }
        return leastMovableItem;
    }*/
}
