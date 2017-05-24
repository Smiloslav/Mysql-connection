package app;

import dbconnection.DBConnection;
import ui.UserInterface;
import user.User;
import user.UsersController;
import user.UsersDAO;

import java.util.Scanner;

public class App {
    public static void main(String args[]) {
        Scanner reader = new Scanner(System.in);

        DBConnection dbConnection = new DBConnection();
        UsersDAO usersDAO = new UsersDAO(dbConnection);

        UsersController usersController = new UsersController(usersDAO);

        UserInterface ui = new UserInterface(reader, usersController);


        ui.run();

    }
}
