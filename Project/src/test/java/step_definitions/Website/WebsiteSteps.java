package step_definitions.Website;

import com.valtech.pageobjects.BaseValtechPageSteps;
import com.valtech.pageobjects.about.AboutPageSteps;
import com.valtech.pageobjects.contact.ContactPageElementLocators;
import com.valtech.pageobjects.contact.ContactPageSteps;
import com.valtech.pageobjects.home.HomePageElementLocators;
import com.valtech.pageobjects.home.HomePageSteps;
import com.valtech.pageobjects.services.ServicesPageSteps;
import com.valtech.pageobjects.work.WorkPageSteps;
import org.openqa.selenium.By;
import step_definitions.TestFramework.CommonAssertions.Web.AssertionsWebElement;
import step_definitions.TestFramework.CommonAssertions.Web.AssertionsWebElements;
import technology.galeforce.testframework.Person;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import step_definitions.TestFramework.WebBrowserSteps;
import technology.galeforce.testframework.TestFramework;
import technology.galeforce.testframework.TestRunner;

/**
 * Created by Peter.Gale on 05/08/2017.
 */
public class WebsiteSteps {

    @When("^([^\"]*) visits the (Valtech) website$")
    public void openTheHotelBookingSystem(String userName, String websiteName) throws Throwable {

        Person currentUser = TestFramework.getUser(userName);
        String homepageURL = TestFramework.getAProperty("homepage.URL");

        TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.displayProgressMessage.action("User '" + userName + "' starts a newHotel Booking System session");
        WebBrowserSteps.userStartsANewWebBrowserSession(userName);

        TestRunner usersTestRunner = currentUser.testRunner;
        usersTestRunner
            .displayProgressMessage.action("User '" + userName + "' has started a new Hotel Booking System session")
            .displayProgressMessage.action("Navigating to the url: " + homepageURL)
            .currentUserSession.currentWebBrowser.navigation.goToURL(homepageURL);
        usersTestRunner
            .displayProgressMessage.action("Navigated to the url: " + homepageURL);
        new HomePageSteps(userName);

    }

    @Then("^([^\"]*) (?:can verify|verifies) that the ([^\"]*) is displayed$")
    public void verifyThatTheWebsiteIsShown(String userName, String itemToCheck) throws Throwable {

        BaseValtechPageSteps currentPageSteps = (BaseValtechPageSteps) TestFramework.getUsersCurrentWebPage(userName);

        By itemToCheckBy = null;
        String itemToCheckName= itemToCheck;
        if ("Valtech website".equals(itemToCheck)) {
            itemToCheckBy = HomePageElementLocators.pageHeaderElementBy;
        } else if ("Latest News section".equals(itemToCheck)) {
            itemToCheckBy = HomePageElementLocators.getHomePageSectionByHeader("Latest news");
        } else {
                String pageHeader = itemToCheck.substring(0, itemToCheck.length()-" Page".length());
                itemToCheckBy = BaseValtechPageSteps.getDefaultPageHeaderBy(pageHeader);
                itemToCheckName = "'" + pageHeader +"'" + " H1 tag";
            }

        AssertionsWebElement.exists(currentPageSteps, itemToCheckBy, itemToCheckName);
    }

    @Then("^([^\"]*) navigates to the ([^\"]*) Page$")
    public void navigateToSubPage(String userName, String subPageName) throws Throwable {
        HomePageSteps homepage = (HomePageSteps) TestFramework.getUsersCurrentWebPage(userName);
            TestFramework.getUser(userName).testRunner.displayProgressMessage.action("User '" + userName + "' navigates to the '" + subPageName + "' Page");
        if ("Contact".equals(subPageName)) {
            ((AboutPageSteps) homepage.navigateToSubPage(userName, AboutPageSteps.homepageNavigationLinkName))
                .navigateToContactUs(userName);
        } else {
            homepage.navigateToSubPage(userName, subPageName);
        }
        TestFramework.getUser(userName).testRunner.displayProgressMessage.action("User '" + userName + "' navigated to the '" + subPageName + "' page");
    }

    @Then("^([^\"]*) can count the total number of Valtech offices$")
    public void countTheNumberOfValtechOffices(String userName) throws Throwable {
        ContactPageSteps contactPageSteps = (ContactPageSteps) TestFramework.getUsersCurrentWebPage(userName);
        TestFramework.getUser(userName).testRunner.displayProgressMessage.action("User '" + userName + "' counts the total number of Valtech offices");
        int totalNumberOfOffices =
            contactPageSteps.parentBrowser
               .getElements(ContactPageElementLocators.allOfficesElementsBy).countAllElements();
        TestFramework.addLineToLog("There are " + totalNumberOfOffices + " Valtech offices in total.");
        AssertionsWebElements.countIs(
            contactPageSteps,
            ContactPageElementLocators.allOfficesElementsBy,
            "Valtech Offices",
            totalNumberOfOffices);
    }

}
