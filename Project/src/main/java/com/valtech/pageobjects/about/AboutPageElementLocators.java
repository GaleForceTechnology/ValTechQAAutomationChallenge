package com.valtech.pageobjects.about;

import org.openqa.selenium.By;

/**
 * Created by Peter.Gale on 05/08/2017.
 */

public class AboutPageElementLocators {

    private static final String pageHeaderXpath = "//h1[.='About']";
    public static final By pageHeaderElementBy = By.xpath(pageHeaderXpath);

    public static final By valtechOfficesLinkElementBy = By.xpath("//a[.='Valtech Offices']");
    public static final By contactUsPageLinkElementBy = valtechOfficesLinkElementBy;

}
