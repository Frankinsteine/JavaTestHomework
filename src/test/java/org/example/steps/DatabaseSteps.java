package org.example.steps;

import io.cucumber.java.ru.И;

import java.sql.*;

public class DatabaseSteps {

    DriverManager driverManager;
    Connection connection;

    @И("Запущен драйвер и создано соединение с БД")
    public void before() throws SQLException {
        DriverManager.registerDriver(new org.h2.Driver());
        connection = DriverManager.getConnection("jdbc:h2:tcp://149.154.71.152:9092/mem:testdb",
                "user",
                "pass");

//        connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb",
//                "user",
//                "pass");
    }

    @И("Добавлен продукт с параметрами {string}, {string}, {int}")
    public void addProduct(String foodName, String foodType, int foodExotic) throws SQLException {
        String insert = "INSERT INTO FOOD (FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) VALUES(?, ?, ?);";
        PreparedStatement pstIntert = connection.prepareStatement(insert);
        pstIntert.setString(1, foodName);
        pstIntert.setString(2, foodType);
        pstIntert.setInt(3, foodExotic);
        pstIntert.executeUpdate();
    }

    @И("Вывод таблицы после добавления записей")
    public void showTable() throws SQLException {
        String query = "SELECT * FROM FOOD";
        PreparedStatement pstSelect = connection.prepareStatement(query);
        ResultSet resultSet = pstSelect.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("FOOD_ID");
            String name = resultSet.getString("FOOD_NAME");
            String type = resultSet.getString("FOOD_TYPE");
            int exotic = resultSet.getInt("FOOD_EXOTIC");
            System.out.printf("%d, %s, %s, %d%n", id, name, type, exotic);
        }
    }

    @И("Проверка продукта с параметрами  {string}, {string}, {int} в базе данных")
    public void checkProduct(String foodName, String foodType, int foodExotic) throws SQLException {
        String testSelect = "SELECT * FROM FOOD" +
                " WHERE FOOD_NAME = ? and FOOD_TYPE = ? and FOOD_EXOTIC = ? ORDER BY FOOD_ID DESC LIMIT 1;";
        PreparedStatement pstTest = connection.prepareStatement(testSelect);
        pstTest.setString(1, "Киви");
        pstTest.setString(2, "FRUIT");
        pstTest.setInt(3, 1);
        ResultSet resultSetTest1 = pstTest.executeQuery();
        if (!resultSetTest1.isBeforeFirst()) {
            System.out.println("Продукт 'Киви' был добавлен в базу данных неверно");
        }
    }

    @И("Удаление товаров после теста БД")
    public void deleteProduct() throws SQLException {
        String delete = "DELETE FROM FOOD WHERE FOOD_NAME = 'Киви' OR FOOD_NAME = 'Огурец'";
        PreparedStatement pstDelete = connection.prepareStatement(delete);
        pstDelete.execute();
    }

    @И("Закрытие соединения")
    public void after() throws SQLException {
        connection.close();
    }

}
