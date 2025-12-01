package com.opencart.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtil {
    private WebDriver driver;
    private WebDriverWait wait;

    public WaitUtil(WebDriver driver, long seg){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(seg));
    }


    //Espera a que un elmento se visible
    public WebElement esperavisible(By locator){
        return wait.until(ExpectedConditions.visibilityOfElementLocated((locator)));
    }

    public WebElement esperaClickeable(By locator){
        return  wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}
