package org.example.steps;

import io.qameta.allure.Step;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;

import java.sql.*;


public class DatabaseTest {

    DriverManager driverManager;
    Connection connection;

    @BeforeEach
    public void before() throws SQLException {
        DriverManager.registerDriver(new org.h2.Driver());
        connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb",
                "user",
                "pass");
    }


    @Test
    @Description("Проверяем правильность добавления продукта в базу данных")
    @DisplayName("Проверка работы БД")
    public void DatabaseTest() throws SQLException {
        //adding first food
        addFood("Киви", "FRUIT", 1);
        //adding second food
        addFood("Огурец", "VEGETABLE", 0);

        //check results
        checkTable();

        //checking added food
        checkFood("Киви", "FRUIT", 1);
        checkFood("Огурец", "VEGETABLE", 0);

        //delete added products after test
        deleteAddedFood();
    }

    @AfterEach
    public void after() throws SQLException {
        connection.close();
    }

    @Step("Добавление продукта {foodName} в базу данных")
    public void addFood(String foodName, String foodType, int foodExotic) throws SQLException {
        String insert = "INSERT INTO FOOD (FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) VALUES(?, ?, ?);";
        PreparedStatement pstIntert = connection.prepareStatement(insert);
        //adding first food
        pstIntert.setString(1, foodName);
        pstIntert.setString(2, foodType);
        pstIntert.setInt(3, foodExotic);
        pstIntert.executeUpdate();
    }

    @Step("Вывод таблицы после добавления товаров")
    public void checkTable() throws SQLException {
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

    @Step("Проверяем добавленный продукт {foodName} в базе данных")
    public void checkFood(String foodName, String foodType, int foodExotic) throws SQLException {
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

    @Step("Удаление добавленных товаров после теста")
    public void deleteAddedFood() throws SQLException {
        String delete = "DELETE FROM FOOD WHERE FOOD_NAME = 'Киви' OR FOOD_NAME = 'Огурец'";
        PreparedStatement pstDelete = connection.prepareStatement(delete);
        pstDelete.execute();
    }

}
