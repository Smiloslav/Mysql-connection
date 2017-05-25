package database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Main sample routine to show how to do basic operations with the package.
 *
 * <p>
 * <b>NOTE:</b> We use asserts in a couple of places to verify the results but if this were actual production code, we
 * would have proper error handling.
 * </p>
 */
public class TestDatabase {
    private final static String DATABASE_URL = "jdbc:sqlite:test.db";
    private Dao<User, Integer> userDao;

    public TestDatabase() throws Exception {
        ConnectionSource connectionSource = null;
        try {
            // create our data-source for the database
            connectionSource = new JdbcConnectionSource(DATABASE_URL);
            // setup our database and DAOs
            setupDatabase(connectionSource);
            // read and write some data
            readWriteData();
            // do a bunch of bulk operations
            readWriteBunch();
            // show how to use the SelectArg object
            useSelectArgFeature();
            // show how to use the SelectArg object
            useTransactions(connectionSource);
            System.out.println("\n\nIt seems to have worked\n\n");
        } finally {
            // destroy the data source which should close underlying connections
            if (connectionSource != null) {
                connectionSource.close();
            }
        }
    }

    private void setupDatabase(ConnectionSource connectionSource) throws Exception {
        userDao = DaoManager.createDao(connectionSource, User.class);

        TableUtils.createTableIfNotExists(connectionSource, User.class);
    }

    private void readWriteData() throws Exception {

        String name = "Jan Kowalski";
        User user = new User(name);

        userDao.create(user);
        int id = user.getId();
        verifyDb(id, user);

        user.setPassword("_secret");

        userDao.update(user);
        verifyDb(id, user);

        List<User> users = userDao.queryForAll();
        assertEquals("Should have found 1 user matching our query", 1, users.size());
        verifyUser(user, users.get(0));

        int userC = 0;
        for(User user2 : userDao) {
            verifyUser(user, user2);
            userC++;
        }
        assertEquals("Should have found 1 user in for loop", 1, userC);

        QueryBuilder<User, Integer> statementBuilder = userDao.queryBuilder();

        statementBuilder.where().like(User.NAME_FIELD_NAME, "hello");
        users = userDao.query(statementBuilder.prepare());
        assertEquals("Should not have found any accounts matching our query", 0, users.size());

        statementBuilder.where().like(User.NAME_FIELD_NAME, name.substring(0, 3) + "%");
        users = userDao.query(statementBuilder.prepare());
        assertEquals("Should have found 1 account matching our query", 1, users.size());
        verifyUser(user, users.get(0));

        userDao.delete(user);

        assertNull("account was deleted, shouldn't find any", userDao.queryForId(id));
    }

    private void readWriteBunch() throws Exception {

        Map<String, User> users = new HashMap<String, User>();
        for(int i = 1; i <= 100; i++) {
            String name = Integer.toString(i);
            User user = new User(name);

            userDao.create(user);
            users.put(name, user);
        }

        List<User> all = userDao.queryForAll();
        assertEquals("Should have found same number of users in map", users.size(), all.size());
        for(User user : all) {
            assertTrue("Should have found user in map", users.containsValue(user));
            verifyUser(users.get(user.getName()), user);
        }

        int userC = 0;
        for(User user : userDao) {
            assertTrue("Should have found user in map", users.containsValue(user));
            verifyUser(users.get(user.getName()), user);
            userC++;
        }
        assertEquals("Should hav found the right number of users in for loop", users.size(), userC);
    }

    private void useSelectArgFeature() throws Exception {

        String name1 = "foo";
        String name2 = "bar";
        String name3 = "baz";
        assertEquals(1, userDao.create(new User(name1)));
        assertEquals(1, userDao.create(new User(name2)));
        assertEquals(1, userDao.create(new User(name3)));

        QueryBuilder<User, Integer> statementBuilder = userDao.queryBuilder();
        SelectArg selectArg = new SelectArg();

        statementBuilder.where().like(User.NAME_FIELD_NAME, selectArg);
        PreparedQuery<User> preparedQuery = statementBuilder.prepare();

        selectArg.setValue(name1);
        List<User> results = userDao.query(preparedQuery);
        assertEquals("Should have found 1 user matching our query", 1, results.size());
        assertEquals(name1, results.get(0).getName());

        selectArg.setValue(name2);
        results = userDao.query(preparedQuery);
        assertEquals("Should have found 1 user matching our query", 1, results.size());
        assertEquals(name2, results.get(0).getName());

        selectArg.setValue(name3);
        results = userDao.query(preparedQuery);
        assertEquals("Should have found 1 user matching our query", 1, results.size());
        assertEquals(name3, results.get(0).getName());


    }

    private void useTransactions(ConnectionSource connectionSource) throws Exception {
        String name = "trans1";
        final User user = new User(name);
        assertEquals(1, userDao.create(user));

        TransactionManager transactionManager = new TransactionManager(connectionSource);
        try {

            transactionManager.callInTransaction(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    assertEquals(1, userDao.delete(user));
                    assertNull(userDao.queryForId(user.getId()));

                    throw new Exception("We throw to roll back!!");
                }
            });
            fail("This should have thrown");
        }catch (SQLException e) {
            // expected
        }

        assertNotNull(userDao.queryForId(user.getId()));
    }

    private void verifyDb(int id, User expected) throws SQLException, Exception {

        User user2 = userDao.queryForId(id);
        if(user2 == null) {
            throw new Exception("Should have found id '" + id + "' in the database");
        }
        verifyUser(expected, user2);
    }

    private static void verifyUser(User expected, User user2) {
        assertEquals("expected name does not equal user name", expected, user2);
        assertEquals("expected password does not equal user name", expected.getPassword(), user2.getPassword());
    }
}
