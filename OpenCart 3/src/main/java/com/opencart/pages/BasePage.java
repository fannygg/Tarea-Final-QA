package com.opencart.pages;

import com.opencart.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasePage {

    protected WebDriver driver;
    protected WaitUtil wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Crear una variable de Tiempo de espera (10 segundos)
        this.wait = new WaitUtil(driver, 10);
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public String tituloPage() {
        return driver.getTitle();
    }

    // ==== Métodos de ayuda reutilizables para las pages ====

    public void click(By locator) {
        WebElement element = wait.esperaClickeable(locator);
        element.click();
    }

    public void type(By locator, String text) {
        WebElement element = wait.esperavisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        return wait.esperavisible(locator).getText();
    }

    public boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}