package com.valtech.pageobjects.home;

import org.openqa.selenium.By;

/**
 * Created by Peter.Gale on 05/08/2017.
 */

public class HomePageElementLocators {

    private static final String pageHeaderXpath = "//header//a[.='Valtech']/i";
    public static final By pageHeaderElementBy = By.xpath(pageHeaderXpath);

    public static By getSubPageNavigationLinkBy(String subPageName) {
        String xpath = "//li[@class='navigation__menu__item']//a[.='" + subPageName + "']";
        return By.xpath(xpath);
    }

    public static By getHomePageSectionByHeader(String sectionHeader) {
        String xpath = "//h2[.='" + sectionHeader +"']//ancestor::div[not(@class)][1]";
        return By.xpath(xpath);
    }

}
