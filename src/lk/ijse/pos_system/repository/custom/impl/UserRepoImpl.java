package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.entity.UserDetail;
import lk.ijse.pos_system.util.CrudUtil;
import lk.ijse.pos_system.repository.custom.UserRepo;
import lk.ijse.pos_system.util.FactoryConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepoImpl implements UserRepo {

    private Session session;

    @Override
    public boolean verifyUser(String userRole, String userName, String tPassword, String fPassword) throws SQLException, ClassNotFoundException {

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();

        UserDetail userDetail = (UserDetail) session.createQuery("FROM UserDetail WHERE userName = :username")
                .setParameter("username", userName).uniqueResult();

        if (userName.equals("") || fPassword.equals("")) {
            //new Alert(Alert.AlertType.WARNING,"Please fill the required fields...", ButtonType.OK).show();
            return false;
        }

        boolean isVerified = false;
        if (userDetail != null) {
            if (userRole.equals("Admin")) {
                if (userDetail.getUserType().equals("ADMIN")) {
                    if (userDetail.getUserName().equals(userName)
                            && (userDetail.getUserPassword().equals(fPassword)) | userDetail.getUserPassword().equals(tPassword)){
                        //return true;
                        isVerified = true;

                    } else {
                        //new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                        //return false;
                    }
                } else {
                    //new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                    //return false;
                }


            } else if (userRole.equals("Cashier")) {
                if (userDetail.getUserType().equals("CASHIER")) {
                    if (userDetail.getUserName().equals(userName)
                            && (userDetail.getUserPassword().equals(fPassword)) | userDetail.getUserPassword().equals(tPassword)) {
                        //return true;
                        isVerified = true;

                    } else {
                        // new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                        //return false;
                    }
                } else {
                    //new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                    //return false;
                }

            } else {
                //new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                //return false;
            }
        }
        transaction.commit();
        session.close();
        return isVerified;

    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
