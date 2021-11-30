package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.repository.custom.DiscountRepo;
import lk.ijse.pos_system.entity.Discount;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;

public class DiscountRepoImpl implements DiscountRepo {

    private Session session;

    @Override
    public boolean add(Discount newDiscount) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.save(newDiscount);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Discount discount) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not Supported Yet");
    }

    @Override
    public boolean update(Discount editDiscount) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        Object[] result = (Object[]) session.createQuery("SELECT itemCode,discount FROM Discount WHERE itemCode = :code")
                .setParameter("code", editDiscount.getItemCode()).uniqueResult();

        boolean isUpdated = true;
        if (result != null) {
            if (result[1].equals(editDiscount.getDiscount())) { // if Item is already specified with a discount
                isUpdated = false;
            } else {
                session.update(editDiscount);
            }
        } else { // if Item is specified a discount for the first time/ no discount specified earlier
            session.save(editDiscount);
        }
        transaction.commit();
        session.close();
        return isUpdated;

    }

    @Override
    public String getDiscount(String itemCode) throws SQLException, ClassNotFoundException {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        /*String discount = (String) session.createQuery("SELECT discount FROM Discount WHERE itemCode = :code")
                .setParameter("code", itemCode).uniqueResult();*/
        Discount discount = session.get(Discount.class, itemCode);

        transaction.commit();
        session.close();
        return discount.getDiscount();
        //return discount;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
