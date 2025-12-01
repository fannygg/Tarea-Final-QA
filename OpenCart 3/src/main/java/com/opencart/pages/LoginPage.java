package com.opencart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private By emailInput = By.id("input-email");
    private By passwordInput = By.id("input-password");
    private By loginButton = By.cssSelector("input[type='submit'][value='Login'], button[type='submit']");
    private By warningAlert = By.cssSelector(".alert-danger");
    private By myAccountHeading = By.cssSelector("#content h2");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open(String baseUrl) {
        navigateTo(baseUrl + "/index.php?route=account/login");
    }

    public void setEmail(String email) {
        type(emailInput, email);
    }

    public void setPassword(String password) {
        type(passwordInput, password);
    }

    public void clickLogin() {
        click(loginButton);
    }

    public boolean isWarningDisplayed() {
        try {
            return wait.esperavisible(warningAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getWarningText() {
        return getText(warningAlert);
    }

    public boolean isMyAccountVisible() {
        return isDisplayed(myAccountHeading);
    }
}
