package user;

import dbconnection.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    private DBConnection connection;

    public UsersDAO(DBConnection connection) {
        this.connection = connection;
    }

    public boolean addUser(User user) {
        String login = user.getLogin();
        String password = user.getPassword();

        int result = connection.update(
                "INSERT INTO users (`login`, `password`) VALUES (\'" + login + "\', \'" + password + "\');"
        );

        return result == 1;
    }

    public boolean removeUserByLogin(String login) {
        int result = connection.update(
                "DELETE FROM users WHERE `login` = \'" + login + "\';"
        );

        return result == 1;
    }

    public List<User> getUsersList() {
        try {
            List<User> usersList = new ArrayList<User>();

            ResultSet result = connection.query("SELECT * FROM users");

            while(result.next()) {
                String login = result.getString("login");
                String password = result.getString("password");

                User user = new User(login,password);

                usersList.add(user);
            }

            connection.closeConnection();

            return usersList;
        }catch (SQLException ex) {
            System.err.println("Error: " + ex);
            return null;
        }
    }

}
