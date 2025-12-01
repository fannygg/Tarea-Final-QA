package com.opencart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegisterPage extends BasePage {

    private By firstNameInput = By.id("input-firstname");
    private By lastNameInput = By.id("input-lastname");
    private By emailInput = By.id("input-email");
    private By telephoneInput = By.id("input-telephone");
    private By passwordInput = By.id("input-password");
    private By confirmPasswordInput = By.id("input-confirm");
    private By privacyPolicyCheckbox = By.name("agree");
    private By continueButton = By.cssSelector("input[type='submit'][value='Continue'], button[type='submit']");

    private By errorAlert = By.cssSelector(".alert-danger");
    private By successHeading = By.cssSelector("#content h1"); 

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    
    public void open(String baseUrl) {
        navigateTo(baseUrl + "/index.php?route=account/register");
    }


    public void setFirstName(String firstName) {
        WebElement element = wait.esperavisible(firstNameInput);
        element.clear();
        element.sendKeys(firstName);
    }

    public void setLastName(String lastName) {
        WebElement element = wait.esperavisible(lastNameInput);
        element.clear();
        element.sendKeys(lastName);
    }

    public void setEmail(String email) {
        WebElement element = wait.esperavisible(emailInput);
        element.clear();
        element.sendKeys(email);
    }

    public void setTelephone(String telephone) {
        WebElement element = wait.esperavisible(telephoneInput);
        element.clear();
        element.sendKeys(telephone);
    }

    public void setPassword(String password) {
        WebElement element = wait.esperavisible(passwordInput);
        element.clear();
        element.sendKeys(password);
    }

    public void setConfirmPassword(String passwordConfirm) {
        WebElement element = wait.esperavisible(confirmPasswordInput);
        element.clear();
        element.sendKeys(passwordConfirm);
    }

    public void acceptPrivacyPolicy() {
        WebElement checkbox = wait.esperavisible(privacyPolicyCheckbox);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    public void submitForm() {
        WebElement button = wait.esperavisible(continueButton);
        button.click();
    }

    // Mensajes de éxito / error

    public String getSuccessMessage() {
        return wait.esperavisible(successHeading).getText();
    }

    public boolean isErrorAlertDisplayed() {
        try {
            return wait.esperavisible(errorAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorAlertText() {
        return getText(errorAlert);
    }

    public String getErrorMessage() {
        if (isErrorAlertDisplayed()) {
            return getErrorAlertText();
        }
        return "";
    }
}