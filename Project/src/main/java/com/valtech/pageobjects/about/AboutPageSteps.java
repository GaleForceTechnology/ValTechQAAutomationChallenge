package com.valtech.pageobjects.about;

import com.valtech.pageobjects.BaseValtechPageSteps;
import com.valtech.pageobjects.contact.ContactPageSteps;

/**
 * Created by Peter.Gale on 05/08/2017.
 */
public class AboutPageSteps extends BaseValtechPageSteps {

    public AboutPageSteps() {
        super();
    }

    public static final String homepageNavigationLinkName = "About";
    public static final String pageObjectName = homepageNavigationLinkName + " Page";

    public AboutPageSteps(String userName) throws Exception {
        super(userName, pageObjectName, AboutPageElementLocators.pageHeaderElementBy);
    }

    public ContactPageSteps navigateToContactUs(String userName) throws Exception {
        this.parentBrowser.getElement(AboutPageElementLocators.contactUsPageLinkElementBy).click.single();
        return new ContactPageSteps(userName);
    }

}
