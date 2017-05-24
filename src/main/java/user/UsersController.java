package user;

import java.util.List;

public class UsersController {

    private UsersDAO dao;

    public UsersController(UsersDAO dao) {
        this.dao = dao;
    }

    public boolean addUser(User user) {
        return dao.addUser(user);
    }

    public boolean removeUserByLogin(String login) {
        return dao.removeUserByLogin(login);
    }

    public List<User> getUsers() {
        return dao.getUsersList();
    }
}
