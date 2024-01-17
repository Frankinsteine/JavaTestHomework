package org.example.steps;

import dev.failsafe.internal.util.Assert;
import io.qameta.allure.Step;
import jdk.jfr.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.stream.Stream;


public class UiTests {
    private WebDriver driver;
    private WebDriverWait wait;

    //parameters
    private static Stream<Arguments> foodDesc() {
        return Stream.of(
                Arguments.of("Огурец", "Овощ", false),
                Arguments.of("Киви", "Фрукт", true)
        );
    }

    @BeforeEach
    public void before() {
        //for Windows
        System.setProperty("webdriver.chromedriver.driver", "src/test/resources/chromedriver.exe");
        //for Mac and Linux
        //System.setProperty("webdriver.chromedriver.driver", "src/test/resources/chromedriver");

        //webdriver init
        driver = new ChromeDriver();

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        //browser option
        driver.manage().window().maximize();

        //go to main page
        driver.get("http://localhost:8080/");

        //wait until page is loaded
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(webDriver -> ((JavascriptExecutor) driver).
                executeScript("return document.readyState").equals("complete"));
    }


    @Description("Проверяем правильность добавления товара в таблицу")
    @DisplayName("Проверка добавления товара в таблицу")
    @ParameterizedTest
    @MethodSource("foodDesc")
    public void test(String foodName, String foodType, boolean foodExotic) {

        click(driver.findElement(By.xpath("//li[@class='nav-item dropdown']")),
                "Кнопка \"Песочница\" не появилась");
        click(driver.findElement(By.xpath("//a[@href='/food']")),
                "Кнопка \"Товары\" не появилась");
        click(driver.findElement(By.xpath("//button[@data-target='#editModal']")),
                "Кнопка \"Добавить\" не появилась");

        //check if window add food is displayed
        waitUntilVisible("//h5[@class='modal-title']");

        //add new food
        fillFoodFields(foodName, foodType, foodExotic);
        //check if window add food is closed
        waitUntilInvisible("//h5[@class='modal-title']");

        //checking added food
        checkAddedFood(foodName, foodType, foodExotic);
    }

    @AfterEach
    public void after() {
        driver.quit();
    }

    @Step("Клик по элементу")
    public void click(WebElement element, String errMsg) {
        Assert.isTrue(element.isDisplayed(), errMsg);
        element.click();
    }

    @Step("Ожидание закрытия элемента")
    public void waitUntilInvisible(String xpath) {
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath(xpath
                )));
    }

    @Step("Ожидание появления элемента")
    public void waitUntilVisible(String xpath) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    @Step("Заполнение продукта {foodName} данными в окне добавления товара")
    public void fillFoodFields(String foodName, String foodType, boolean foodExotic) {
        WebElement setFoodNameInput = driver.findElement(By.xpath("//input[@id='name']"));
        WebElement selectFoodType = driver.findElement(By.xpath("//select[@id='type']"));
        setFoodNameInput.sendKeys(foodName);
        selectFoodType.click();
        Assert.isTrue(driver.findElement(By.xpath("//option[@value='FRUIT']")).isDisplayed(),
                "Кнопки выбора типа товара не появились");
        if (Objects.equals(foodType, "Овощ")) {
            WebElement selectedFoodType = driver.findElement(By.xpath("//option[@value='VEGETABLE']"));
            selectedFoodType.click();
        } else if (Objects.equals(foodType, "Фрукт")) {
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

        WebElement addFoodInListBtn = driver.findElement(By.xpath("//button[@id='save']"));
        addFoodInListBtn.click();
    }

    @Step("Проверка добавленного продукта {foodName} в таблице")
    public void checkAddedFood(String foodName, String foodType, boolean foodExotic) {
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
    }
}
