package lk.ijse.pos_system.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;

public class SessionUtil {

    public static Session getSession() throws SQLException, ClassNotFoundException {
        return FactoryConfiguration.getInstance().getSession();
    }
}

