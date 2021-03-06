package ru.stqa.basetest;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lenovo on 08.01.2017.
 */
public class BaseTest {
    public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    public WebDriver driver;
    public WebDriverWait wait;

    public void loginToAdmin () {
        driver.navigate().to("http://localhost/litecart/admin/");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.name("login")).click();
    }

    private boolean isElementPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    public boolean areElementsPresent(WebDriver driver, By locator) {
        return driver.findElements(locator).size() > 0;
    }

    public boolean isElementNotPresent(WebDriver driver, By locator){
        try{
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            return  driver.findElements(locator).size()==0;
        }
        finally{
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
    }

    public void clickElementByLocator(String text) {
        driver.findElement(By.cssSelector(text)).click();
    }

    public void fillText(String text, String locatorSearch){
        WebElement locator = driver.findElement(By.cssSelector(locatorSearch));
        locator.clear();
        locator.sendKeys(text);
    }
    public void setText(String text, String locatorSearch){
        WebElement locator = driver.findElement(By.cssSelector(locatorSearch));
        locator.sendKeys(text);
    }
    public void fillSelectForm(String code, String locatorSearch) {
        Select menuItem = new Select(driver.findElement(By.cssSelector(locatorSearch)));
        menuItem.selectByValue(code);
    }
    public void fillSelectVisibleForm(String code, String locatorSearch) {
        Select menuItem = new Select(driver.findElement(By.cssSelector(locatorSearch)));
        menuItem.selectByVisibleText(code);
    }

    public void setElementByName(String name, String locator, String tagName){
        List<WebElement> elements = driver.findElements(By.cssSelector(locator));

        for (int i=0; i < elements.size(); i++) {
                if (name.equals(elements.get(i).getAttribute(tagName))) {
                    if (elements.get(i).getTagName().equals("input")) {
                         elements.get(i).click();
                    }
                    else {
                        elements.get(i-1).findElement(By.tagName("input")).click();
                    }
                }
        }
    }

    public void switchToNewWindowCloseAndReturn(WebElement link) {

        String mainWindow = driver.getWindowHandle();
        Set<String> oldWindows = driver.getWindowHandles();
        link.click();
        String newWindow = wait.until(thereIsWindowOtherThan(oldWindows));
        driver.switchTo().window(newWindow);
//       System.out.println(driver.getCurrentUrl());
        driver.close();
        driver.switchTo().window(mainWindow);

    }

        public ExpectedCondition<String> thereIsWindowOtherThan(Set<String> oldWindows){
            return new ExpectedCondition<String>() {

                public String apply(WebDriver webDriver) {
                    Set<String> handles = driver.getWindowHandles();
                    handles.removeAll(oldWindows);
                    return handles.size() > 0 ? handles.iterator().next() : null;
                }
            };
    }

    @Before
    public void start() {
        if (tlDriver.get() != null) {
            driver = tlDriver.get();
            wait = new WebDriverWait(driver, 10);
            return;
        }

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        tlDriver.set(driver);
        wait = new WebDriverWait(driver, 10);

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {driver.quit(); driver = null; }));
    }

    @After
     public void stop() {
//         driver.quit();
//         driver = null;
     }
}
