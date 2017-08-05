package com.valtech.pageobjects.work;

import com.valtech.pageobjects.BaseValtechPageSteps;

/**
 * Created by Peter.Gale on 05/08/2017.
 */
public class WorkPageSteps extends BaseValtechPageSteps {

    public WorkPageSteps() {
        super();
    }

    public static final String homepageNavigationLinkName = "Work";
    public static final String pageObjectName = homepageNavigationLinkName + " Page";

    public WorkPageSteps(String userName) throws Exception {
        super(userName, pageObjectName, WorkPageElementLocators.pageHeaderElementBy);
    }

}