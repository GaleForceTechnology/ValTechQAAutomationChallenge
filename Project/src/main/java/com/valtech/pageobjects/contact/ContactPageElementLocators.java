package com.valtech.pageobjects.contact;

import org.openqa.selenium.By;

/**
 * Created by Peter.Gale on 05/08/2017.
 */
public class ContactPageElementLocators {
    private static final String pageHeaderXpath = "//h1[.='Contact']";
    public static final By pageHeaderElementBy = By.xpath(pageHeaderXpath);
    public static final By allOfficesElementsBy = By.xpath("//h2[@class='office__heading']");



}
