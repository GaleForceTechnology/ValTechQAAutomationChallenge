package com.valtech.pageobjects;

import org.openqa.selenium.By;
import technology.galeforce.testframework.web.BaseWebPage;

/**
 * Created by Peter.Gale on 04/08/2017.
 */
public class BaseValtechPageSteps extends BaseWebPage {

    public BaseValtechPageSteps() {
        super();
    }

    public BaseValtechPageSteps(String userName, String pageObjectName, By pageHeaderElementBy) throws Exception {
        super(userName, pageObjectName, pageHeaderElementBy);
    }

    public static final By getDefaultPageHeaderBy(String pageName) {
        String xpath = "//h1[.='" + pageName + "']";
        return By.xpath(xpath);
    }

}
