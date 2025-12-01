package com.opencart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class CartPage extends BasePage {

    private By cartTableRows = By.cssSelector("#content form table tbody tr");
    private By productNameCell = By.cssSelector("td:nth-child(2) a");
    private By quantityInput = By.cssSelector("td:nth-child(4) input");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public static class CartItem {
        public final String name;
        public final String quantity;

        public CartItem(String name, String quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }

    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        List<WebElement> rows = driver.findElements(cartTableRows);

        for (WebElement row : rows) {
            try {
                String name = row.findElement(productNameCell).getText().trim();
                String qty = row.findElement(quantityInput).getAttribute("value").trim();
                items.add(new CartItem(name, qty));
            } catch (Exception e) {
                // ignorar filas que no cumplan el formato esperado
            }
        }

        return items;
    }
}
