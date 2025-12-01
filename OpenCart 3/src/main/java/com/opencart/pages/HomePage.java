package com.opencart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private By myAccountDropdown = By.xpath("//span[text()='My Account']");
    private By registerLink = By.linkText("Register");
    private By loginLink = By.linkText("Login");
    private By logoutLink = By.linkText("Logout");
    private By searchInput = By.name("search");
    private By searchBtn = By.cssSelector("#search button");
    private By shoppingCartLink = By.xpath("//span[text()='Shopping Cart']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void goToRegister() {
        click(myAccountDropdown);
        click(registerLink);
    }

    public void goToLogin() {
        click(myAccountDropdown);
        click(loginLink);
    }

    public void logout() {
        click(myAccountDropdown);
        if(isDisplayed(logoutLink)){
            click(logoutLink);
        }
    }

    public void searchProduct(String productName) {
        type(searchInput, productName);
        click(searchBtn);
    }

    public void openProductFromResults(String productName) {
        org.openqa.selenium.By productLink = By.linkText(productName);
        click(productLink);
    }

    public void goToCart() {
        click(shoppingCartLink);
    }
}