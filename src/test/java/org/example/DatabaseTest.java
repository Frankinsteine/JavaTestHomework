package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;


public class DatabaseTest {
    @Test
    @DisplayName("testDatabase")
    public void DatabaseTest() throws SQLException {
        DriverManager.registerDriver(new org.h2.Driver());
        Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb",
                "user",
                "pass");

        String insert = "INSERT INTO FOOD (FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) VALUES(?, ?, ?);";
        String query = "SELECT * FROM FOOD";
        String delete = "DELETE FROM FOOD WHERE FOOD_NAME = 'Киви' OR FOOD_NAME = 'Огурец'";
        String testSelect = "SELECT * FROM FOOD" +
                " WHERE FOOD_NAME = ? and FOOD_TYPE = ? and FOOD_EXOTIC = ? ORDER BY FOOD_ID DESC LIMIT 1;";

        PreparedStatement pstIntert = connection.prepareStatement(insert);
        //adding first food
        pstIntert.setString(1,"Киви");
        pstIntert.setString(2,"FRUIT");
        pstIntert.setInt(3,1);
        pstIntert.executeUpdate();

        //adding second food
        pstIntert.setString(1,"Огурец");
        pstIntert.setString(2,"VEGETABLE");
        pstIntert.setInt(3,0);
        pstIntert.executeUpdate();

        //check results
        PreparedStatement pstSelect = connection.prepareStatement(query);
        ResultSet resultSet = pstSelect.executeQuery();
        while(resultSet.next()) {
            int id = resultSet.getInt("FOOD_ID");
            String name = resultSet.getString("FOOD_NAME");
            String type = resultSet.getString("FOOD_TYPE");
            int exotic = resultSet.getInt("FOOD_EXOTIC");
            System.out.printf("%d, %s, %s, %d%n", id, name, type, exotic);
        }

        //checking added food
        PreparedStatement pstTest = connection.prepareStatement(testSelect);
        pstTest.setString(1,"Киви");
        pstTest.setString(2,"FRUIT");
        pstTest.setInt(3,1);
        ResultSet resultSetTest1 = pstTest.executeQuery();
        if (!resultSetTest1.isBeforeFirst() ) {
            System.out.println("Продукт 'Киви' был добавлен в базу данных неверно");
        }

        pstTest.setString(1,"Огурец");
        pstTest.setString(2,"VEGETABLE");
        pstTest.setInt(3,0);
        ResultSet resultSetTest2 = pstTest.executeQuery();
        if (!resultSetTest2.isBeforeFirst() ) {
            System.out.println("Продукт 'Огурец' был добавлен в базу данных неверно");
        }

        //delete added products after test
        PreparedStatement pstDelete = connection.prepareStatement(delete);
        pstDelete.execute();

        connection.close();
    }

}
