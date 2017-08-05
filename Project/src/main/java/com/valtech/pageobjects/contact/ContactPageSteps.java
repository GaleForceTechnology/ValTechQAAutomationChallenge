package com.valtech.pageobjects.contact;

import com.valtech.pageobjects.BaseValtechPageSteps;

/**
 * Created by Peter.Gale on 05/08/2017.
 */
public class ContactPageSteps extends BaseValtechPageSteps {

    public ContactPageSteps() {
        super();
    }

    public static final String homepageNavigationLinkName = "Contact";
    public static final String pageObjectName = homepageNavigationLinkName + " Page";

    public ContactPageSteps(String userName) throws Exception {
        super(userName, pageObjectName, ContactPageElementLocators.pageHeaderElementBy);
    }

}