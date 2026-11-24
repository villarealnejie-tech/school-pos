package pos.controller;

import pos.model.User;
import pos.model.dao.UserDao;
import pos.view.LoginFrame;

import java.sql.SQLException;

/** Handles the login screen: checks the credentials and opens the main window. */
public class LoginController {

    private final LoginFrame view;

    public LoginController(LoginFrame view) {
        this.view = view;
        view.setController(this);
    }

    public void show() {
        view.setVisible(true);
    }

    public void login(String username, String password) {
        username = username.trim();
        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Enter your username and password.");
            return;
        }
        try {
            User user = UserDao.login(username, password);
            if (user == null) {
                view.showError("Wrong username or password.");
                view.clearPassword();
                return;
            }
            view.dispose();
            new MainController(user).show();
        } catch (SQLException ex) {
            view.showError("Cannot connect to the database.\n\n"
                    + ex.getMessage()
                    + "\n\nCheck that MySQL is running, that you ran database/schema.sql in Workbench,\n"
                    + "and that the password in DB.java is correct.");
        }
    }
}
