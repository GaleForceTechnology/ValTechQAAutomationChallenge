package com.valtech.pageobjects.services;

import com.valtech.pageobjects.BaseValtechPageSteps;

/**
 * Created by Peter.Gale on 05/08/2017.
 */
public class ServicesPageSteps extends BaseValtechPageSteps {

    public ServicesPageSteps() {
        super();
    }

    public static final String homepageNavigationLinkName = "Services";
    public static final String pageObjectName = homepageNavigationLinkName + " Page";

    public ServicesPageSteps(String userName) throws Exception {
        super(userName, pageObjectName, ServicesPageElementLocators.pageHeaderElementBy);
    }

}
