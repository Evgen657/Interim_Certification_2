package Certification_3;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseDemo {
    public static void main(String[] args) throws SQLException {
        String connectionString = "jdbc:postgresql://dpg-d2o8fqjipnbc73begg4g-a.oregon-postgres.render.com/employee_postgres_2";
        String username = "admin";
        String password = "azaHlBwfeeGxwEUt07mK6xtGrwBC1DCK";

        String SELECT_NAME_SURNAME = "SELECT name, surname FROM employee limit 10;";
        Connection connection = DriverManager.getConnection(connectionString, username, password);
        ResultSet resultSet = connection.createStatement().executeQuery(SELECT_NAME_SURNAME);
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
            System.out.println(resultSet.getString("surname"));
            System.out.println("_________________________");
        }
    }
}