package lk.ijse.pos_system.util;

import lk.ijse.pos_system.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.security.acl.Owner;
import java.sql.SQLException;
import java.util.Properties;

public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfiguration;
    private final SessionFactory sessionFactory;

    private FactoryConfiguration() throws ClassNotFoundException, SQLException {

        /*Configuration configuration = new Configuration().configure()
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Orders.class);
        sessionFactory = configuration.buildSessionFactory();*/

        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream("hibernate.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Configuration configuration = new Configuration().mergeProperties(properties);
        configuration.addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Orders.class)
                .addAnnotatedClass(Item.class)
                .addAnnotatedClass(OrderDetail.class)
                .addAnnotatedClass(Discount.class)
                .addAnnotatedClass(UserDetail.class);

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static FactoryConfiguration getInstance() throws SQLException, ClassNotFoundException {
        return (factoryConfiguration == null) ? factoryConfiguration = new FactoryConfiguration() : factoryConfiguration;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }
}
