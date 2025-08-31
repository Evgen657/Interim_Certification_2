package restAssured.helper;

import restAssured.enitites.EmployeeRequest;
import restAssured.enitites.EmployeeResponse;

import java.io.IOException;
import java.sql.*;

public class EmployeeHelperDB extends AbstractHelper {

    public EmployeeHelperDB() throws SQLException, IOException {
        connection = getConnection();
    }

    public int createEmployee(EmployeeRequest employee) throws SQLException {
        String INSERT_EMPLOYEE = "INSERT INTO employee(name, surname, city, position) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getSurname());
            preparedStatement.setString(3, employee.getCity());
            preparedStatement.setString(4, employee.getPosition());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    return -1;
                }
            }
        }
    }

    public EmployeeResponse getEmployee(int id) throws SQLException {
        String SELECT_EMPLOYEE = "SELECT id, city, name, position, surname FROM employee WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EMPLOYEE)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new EmployeeResponse(
                            resultSet.getString("city"),
                            resultSet.getString("name"),
                            resultSet.getString("position"),
                            resultSet.getString("surname"),
                            resultSet.getInt("id")
                    );
                } else {
                    // Возвращаем null, если сотрудник не найден
                    return null;
                }
            }
        }
    }

    public int countEmployeesByNameAndSurname(String name, String surname) throws SQLException {
        String COUNT_EMPLOYEES = "SELECT COUNT(*) FROM employee WHERE name = ? AND surname = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(COUNT_EMPLOYEES)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, surname);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }

    public void deleteTestEmployees() throws SQLException {
        // Можно расширить список фамилий, которые считаются тестовыми
        String DELETE_TEST_EMPLOYEES = "DELETE FROM employee WHERE surname IN ('Igorin', 'Ivanova', 'Batuev', 'Sergeev', 'Dmitriev', 'Elena', 'Volkov', 'Dmitry', 'Sergey', 'Anna', 'Alexeev')";
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEST_EMPLOYEES)) {
            preparedStatement.executeUpdate();
        }
    }

    public void deleteEmployee(int id) throws SQLException {
        String DELETE_EMPLOYEE = "DELETE FROM employee WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_EMPLOYEE)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
