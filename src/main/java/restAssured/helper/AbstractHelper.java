package restAssured.helper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract public class AbstractHelper {

    protected Connection connection;

    public Connection getConnection() throws SQLException, IOException {
        EnvHelper envHelper = new EnvHelper();
        String connectionString = envHelper.getConnectionString();
        String login = envHelper.getLogin();
        String password = envHelper.getPassword();

        return DriverManager.getConnection(connectionString, login, password);
    }
}