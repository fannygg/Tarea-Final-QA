package com.opencart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ProductPage extends BasePage {

    private By quantityInput = By.id("input-quantity");
    private By addToCartBtn = By.id("button-cart");
    private By successAlert = By.cssSelector(".alert-success");
    private By productSelectOption = By.cssSelector("#product select");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public void setQuantity(String qty) {
        String valor = qty;
        try {
            double d = Double.parseDouble(qty);
            if (d % 1 == 0) {
                valor = String.valueOf((int) d);
            }
        } catch (Exception e) {
        }
        type(quantityInput, valor);
    }

    public void selectFirstOptionIfAvailable() {
        List<WebElement> selects = driver.findElements(productSelectOption);

        if (!selects.isEmpty()) {
            
            for (WebElement dropdown : selects) {
                try {
                    Select select = new Select(dropdown);
                  
                    select.selectByIndex(1);
                } catch (Exception e) {
                    System.out.println("No se pudo seleccionar la opción: " + e.getMessage());
                }
            }
        }
    }

    public void addToCart() {
        click(addToCartBtn);
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.esperavisible(successAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessMessageText() {
        try {
            return getText(successAlert);
        } catch (Exception e) {
            return "";
        }
    }
}