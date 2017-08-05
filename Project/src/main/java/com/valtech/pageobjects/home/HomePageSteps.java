package com.valtech.pageobjects.home;

import com.valtech.pageobjects.BaseValtechPageSteps;
import com.valtech.pageobjects.about.AboutPageSteps;
import com.valtech.pageobjects.services.ServicesPageSteps;
import com.valtech.pageobjects.work.WorkPageSteps;
import technology.galeforce.testframework.TestFramework;

/**
 * Created by peter.gale on 05/08/2017.
 */

public class HomePageSteps extends BaseValtechPageSteps {

    public static final String pageObjectName = "Valtech Website Home Page";

    public HomePageSteps() {
        super();
    }

    public HomePageSteps(String userName) throws Exception {
        super(userName, pageObjectName, HomePageElementLocators.pageHeaderElementBy);
    }

    public BaseValtechPageSteps navigateToSubPage(String userName, String subPageName) throws Exception {
        this.parentBrowser.getElement(HomePageElementLocators.getSubPageNavigationLinkBy(subPageName)).click.single();
        BaseValtechPageSteps returnPageSteps = null;

        if ((subPageName).equals(AboutPageSteps.homepageNavigationLinkName)) {
            returnPageSteps =  new AboutPageSteps(userName);
        } else if ((subPageName).equals(ServicesPageSteps.homepageNavigationLinkName)) {
            returnPageSteps =  new ServicesPageSteps(userName);
        } else if ((subPageName + " Page").equals(WorkPageSteps.pageObjectName)) {
            returnPageSteps =  new WorkPageSteps(userName);
        } else {
            throw new Exception("Unhandled sub page name '" + subPageName + "'");
        }

        return returnPageSteps;
    }

}
