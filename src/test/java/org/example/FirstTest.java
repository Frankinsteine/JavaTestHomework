package org.example;

import dev.failsafe.internal.util.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.Objects;
import java.util.stream.Stream;


public class FirstTest {

    final int defaultSleepTime = 1;

    public void sleep(int time) {
        try{
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //parameters
    private static Stream<Arguments> foodDesc() {
        return Stream.of(
                Arguments.of("Огурец", "Овощ", false),
                Arguments.of("Киви", "Фрукт", true)
        );
    }


    @ParameterizedTest
    @MethodSource("foodDesc")
    public void test(String foodName, String foodType, boolean foodExotic){

        //webdriver init
        WebDriver driver = new ChromeDriver();
        System.setProperty("webdriver.chromedriver.driver", "src/test/resources/chromedriver.exe");

        //browser option
        driver.manage().window().maximize();

        //go to main page
        driver.get("http://localhost:8080/");

        sleep(defaultSleepTime);
        WebElement sandboxBtn = driver.findElement(By.xpath("//li[@class='nav-item dropdown']"));
        Assert.isTrue(sandboxBtn.isDisplayed(), "Кнопка \"Песочница\" не появилась");
        sandboxBtn.click();

        sleep(defaultSleepTime);
        WebElement foodBtn = driver.findElement(By.xpath("//a[@href='/food']"));
        Assert.isTrue(foodBtn.isDisplayed(), "Кнопка \"Товары\" не появилась");
        foodBtn.click();


        sleep(defaultSleepTime);
        WebElement addFoodBtn = driver.findElement(By.xpath("//button[@data-target='#editModal']"));
        Assert.isTrue(addFoodBtn.isDisplayed(), "Кнопка \"Добавить\" не появилась");
        addFoodBtn.click();

        sleep(defaultSleepTime);
        //check if window add food is displayed
        Assert.isTrue(driver.findElement(By.xpath("//h5[@class='modal-title']")).isDisplayed(),
                "Окно добавления товара не появилось");
        WebElement setFoodNameInput = driver.findElement(By.xpath("//input[@id='name']"));
        WebElement selectFoodType = driver.findElement(By.xpath("//select[@id='type']"));
        setFoodNameInput.sendKeys(foodName);
        selectFoodType.click();

        sleep(defaultSleepTime);
        Assert.isTrue(driver.findElement(By.xpath("//option[@value='FRUIT']")).isDisplayed(),
                "Кнопки выбора типа товара не появились");
        if(Objects.equals(foodType, "Овощ")) {
            WebElement selectedFoodType = driver.findElement(By.xpath("//option[@value='VEGETABLE']"));
            selectedFoodType.click();
        } else if(Objects.equals(foodType, "Фрукт")) {
            WebElement selectedFoodType = driver.findElement(By.xpath("//option[@value='FRUIT']"));
            selectedFoodType.click();
        }



        //checking exotic checkbox
        WebElement exoticCheckbox = driver.findElement(By.xpath("//input[@id='exotic']"));
        if (foodExotic) {
            exoticCheckbox.click();
            Assert.isTrue(exoticCheckbox.isSelected(), "Чекбокс экзотичности продукта выключён");
        } else {
            Assert.isTrue(!exoticCheckbox.isSelected(), "Чекбокс экзотичности продукта включён");
        }
        sleep(defaultSleepTime);

        WebElement addFoodInListBtn = driver.findElement(By.xpath("//button[@id='save']"));
        addFoodInListBtn.click();

        sleep(defaultSleepTime);
        //checking added food
        WebElement addedFoodNumber = driver.findElement(By.xpath("//tbody/tr[5]/th"));
        WebElement addedFoodName = driver.findElement(By.xpath("//tbody/tr[5]/td[1]"));
        WebElement addedFoodType = driver.findElement(By.xpath("//tbody/tr[5]/td[2]"));
        WebElement addedFoodExotic = driver.findElement(By.xpath("//tbody/tr[5]/td[3]"));

        //check product in table
        Assert.isTrue(Objects.equals(addedFoodNumber.getText(), "5"),
                "Порядковый номер пятого продукта не равен 5");
        Assert.isTrue(Objects.equals(addedFoodName.getText(), foodName),
                "Название не совпадает с " + foodName);
        Assert.isTrue(Objects.equals(addedFoodType.getText(), foodType),
                "Тип добавленного продукта не " + foodType);
        if (foodExotic) {
            Assert.isTrue(Objects.equals(addedFoodExotic.getText(), "true"),
                    "Экзотичность продукта не верна");
        } else {
            Assert.isTrue(Objects.equals(addedFoodExotic.getText(), "false"),
                    "Экзотичность продукта не верна");
        }

        //small delay before quit driver
        sleep(3);
        driver.quit();
    }
}
