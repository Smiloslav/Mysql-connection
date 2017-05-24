package ui;

import user.User;
import user.UsersController;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

    private Scanner reader;
    private UsersController usersController;

    public UserInterface(Scanner reader, UsersController usersController) {
        this.reader = reader;
        this.usersController = usersController;
    }

    public void run() {

        System.out.println("************************");
        System.out.println("*                      *");
        System.out.println("*         MENU         *");
        System.out.println("*                      *");
        System.out.println("************************");

        while (true){
            System.out.println("Wybierz opcję");
            System.out.println("[1] - Dodaj użytkownika");
            System.out.println("[2] - Usuń użytkownika");
            System.out.println("[3] - Wyświetl wszystkich użytkowników");
            System.out.println("[0] - Wyjście");
            System.out.println();

            int input = Integer.parseInt(reader.nextLine());

            if(input == 0)
                break;
            else
                menu(input);
        }
    }

    private void menu(int input) {
        switch (input) {
            case 1:
                addUser();
                break;

            case 2:
                removeUser();
                break;

            case 3:
                getAllUsers();
                break;

        }
    }

    private void addUser() {
        System.out.println("Podaj login:");
        String login = reader.nextLine();

        System.out.println();

        System.out.println("Podaj hasło");
        String password = reader.nextLine();

        User userToAdd = new User(login, password);
        if(usersController.addUser(userToAdd))
            System.out.println("Pomyślnie dodano użytkownika " + login);
        else
            System.out.println("Nie udało się dodać użytkownika");
    }

    private void removeUser() {
        System.out.println("Podaj login");
        String login = reader.nextLine();

        System.out.println();

        System.out.println("Czy na pewno chcesz usunąć użytkownika \"" + login + "\"?");
        System.out.println("(Y/N)");

        String input = reader.nextLine();
        input = input.toLowerCase().trim();
        while (true) {
            if (input.equals("y")) {
                if (usersController.removeUserByLogin(login))
                    System.out.println("Usunięto użytkownika \"" + login + "\" z bazy");
                else
                    System.out.println("Nie udało się usunąć użytkownika o loginie \"" + login + "\"");

                break;
            } else if (input.equals("n")) {
                System.out.println("Przerwano operację");
                System.out.println();
                break;
            }
        }
    }

    private void getAllUsers() {
        List<User> usersList = usersController.getUsers();

        if(usersList == null)
            System.out.println("Nie udało się pobrać listy użytkowników");
        else {
            System.out.println("Lista wszystkich użytkowników");
            System.out.println();
            System.out.println("Login\tPassword");
            System.out.println();

            for (User u : usersList) {
                System.out.println(u.toString());
            }
            System.out.println();
        }
    }
}
