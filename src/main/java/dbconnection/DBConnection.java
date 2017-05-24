package dbconnection;

import java.sql.*;

public class DBConnection {

    // Database Name
    static final String DB_NAME = "mysqljavaconnectiontest";

    //  JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/" + DB_NAME;

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public DBConnection() {
    }

    //  Connection method
    private Connection connect() {
        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

        } catch (SQLException se) {
            //  JDBC errors handler
            System.out.println(se.getMessage());
            se.printStackTrace();
        } catch (Exception ex) {
            //  Class.forName errors handler
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }finally{
            return connection;
        }
    }

    //  Closing connection methods
    public void closeConnection() {
        try {
            if(resultSet != null && !resultSet.isClosed())
                resultSet.close();

            if(statement != null && !statement.isClosed())
                statement.close();

            if(connection != null && !connection.isClosed())
                connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  Queries methods
    //  SELECT only

    /**
     * Pamiętaj o zamknięciu połączenia metodą closeConnection();
     */
    public ResultSet query(String query) {
        try {
            connection = connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            return resultSet;
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }

        return null;
    }

    // NON-SELECT only
    public int update(String updateQuery) {
        try {
            connection = connect();
            statement = connection.createStatement();
            int result = statement.executeUpdate(updateQuery);

            return result;
        } catch (SQLException ex) {
            System.out.println("Error: " + ex);
        } finally {
            closeConnection();
        }

        return -1;
    }
}
