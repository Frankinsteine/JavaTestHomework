package org.example.steps;

import dev.failsafe.internal.util.Assert;
import io.cucumber.java.ru.И;
import org.example.managers.TestPropManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static org.example.managers.TestPropManager.getTestPropManager;

public class UISteps {
    private final TestPropManager props = getTestPropManager();
    private WebDriver driver;
    private WebDriverWait wait;
    Map<String , String> xPaths = Map.of(
            "Песочница", "//li[@class='nav-item dropdown']",
            "Товары", "//a[@href='/food']",
            "Добавить", "//button[@data-target='#editModal']",
            "Сброс данных", "//a[@id='reset']"
    );

    public UISteps() throws IOException {
    }


    @И("запущен и настроен вебдрайвер")
    public void before() throws MalformedURLException {
        //for Windows
        System.setProperty("webdriver.chromedriver.driver", "src/test/resources/chromedriver.exe");
        //for Mac and Linux
        //System.setProperty("webdriver.chromedriver.driver", "src/test/resources/chromedriver");

        //webdriver init
        if("remote".equalsIgnoreCase(props.getProperty("type.driver"))) {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(props.getProperty("type.browser"));
            capabilities.setVersion("109.0");
            capabilities.setCapability("selenoid:options", Map.of(
                    "enableVNC", true,
                    "enableVideo", false
            ));
            driver = new RemoteWebDriver(URI.create(props.getProperty("selenoid.url")).toURL(), capabilities);
        } else {
            driver = new ChromeDriver();
            //browser options

        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().window().maximize();
    }

    @И("открыта страница по адресу {string}")
    public void openPage(String string) {
        //go to main page
        driver.get(string);

        //wait until page is loaded
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(webDriver -> ((JavascriptExecutor) driver).
                executeScript("return document.readyState").equals("complete"));
    }

    @И("нажать на кнопку {string}")
    public void clickOnBtn(String btnName) {
        WebElement element = driver.findElement(By.xpath(xPaths.get(btnName)));
        Assert.isTrue(element.isDisplayed(), "Кнопка %s не появилась", btnName);
        element.click();
    }

    @И("дождаться появления всплывающего окна")
    public void waitUntilVisible() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h5[@class='modal-title']")
        ));
    }

    @И("заполнить поля данными {string}, {string}, {int}")
    public void fillFieldsWithData(String foodName, String foodType, int foodExotic) {
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
        if (foodExotic == 1) {
            exoticCheckbox.click();
            Assert.isTrue(exoticCheckbox.isSelected(), "Чекбокс экзотичности продукта выключён");
        } else {
            Assert.isTrue(!exoticCheckbox.isSelected(), "Чекбокс экзотичности продукта включён");
        }

        WebElement addFoodInListBtn = driver.findElement(By.xpath("//button[@id='save']"));
        addFoodInListBtn.click();
    }

    @И("дождаться закрытия всплывающего окна")
    public void waitUntilInvisible() {
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//h5[@class='modal-title']"
                )));
    }

    @И("проверить таблицу с товарами на наличие строки с {string}, {string}, {int}")
    public void checkTable(String foodName, String foodType, int foodExotic) {
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
        if (foodExotic == 1) {
            Assert.isTrue(Objects.equals(addedFoodExotic.getText(), "true"),
                    "Экзотичность продукта не верна");
        } else {
            Assert.isTrue(Objects.equals(addedFoodExotic.getText(), "false"),
                    "Экзотичность продукта не верна");
        }
    }

    @И("закрыть вебдрайвер")
    public void after() {
        driver.quit();
    }
}
