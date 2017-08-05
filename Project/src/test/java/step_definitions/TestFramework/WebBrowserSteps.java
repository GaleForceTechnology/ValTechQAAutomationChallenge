package step_definitions.TestFramework;

import technology.galeforce.testframework.web.BrowserFactory;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

import technology.galeforce.testframework.Person;
import technology.galeforce.testframework.TestFramework;

/**
 * Created by peter.gale on 14/10/2016.
 */

public class WebBrowserSteps  {

    @When("^([^\"]*) starts (?:a|another) new (?:Web Browser) session$")
    public static void userStartsANewWebBrowserSession(String userName) throws Throwable {
        TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.displayProgressMessage.action("User '" + userName + "' starts a new web browser session");
        // Just open a web browser but do not navigate to any particular page
        TestFramework.getUser(userName).testRunner.openNewWebBrowserUserSession(false);
    }

    @When("^([^\"]*) opens (?:a|another) new (?:Web Browser) window$")
    public static void opensANewWebBrowserWindow(String userName) throws Throwable {
        TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.displayProgressMessage.action("User '" + userName + "' opens a new web browser window");
        TestFramework.getUser(userName).testRunner.openNewWindowInTheCurrentWebBrowserSession();
    }

    @When("^([^\"]*) opens a web browser$")
    // We assume user will want a local browser unless they specify otherwise
    public void userOpensAWebBrowser(String userName) throws Throwable {
        Person user = TestFramework.getUser(userName);
        user.testRunner.openNewWebBrowserUserSession(false);
    }

    @When("^([^\"]*) opens a local ([^\"]*) web browser$")
    // We assume user will want a local browser unless they specify otherwise
    public void userOpensAWebBrowser(String userName,String browserTypeName) throws Throwable {
        Person user = TestFramework.getUser(userName);
        BrowserFactory.BrowserTypes browserType = BrowserFactory.getBrowserType(browserTypeName);
        user.testRunner.openNewWebBrowserUserSession(browserType, false);
    }

    @Then("^([^\"]*)'s current browser type is ([^\"]*)$")
    public void usersCurrentBrowserTypeIs(String userName, String expectedBrowserType) throws Throwable {
        Person user = TestFramework.getUser(userName);
        String actualBrowserType=user.testRunner.getCurrentWebBrowserType();
        assertTrue(
            "The current browser type is '" + expectedBrowserType + "' [got '" + actualBrowserType +"']",
            actualBrowserType.equals(expectedBrowserType.toLowerCase()));
    }

    @Then("^([^\"]*) closes the current web browser$")
    public void userClosesTheWebBrowser(String userName) throws Throwable {
        Person user = TestFramework.getUser(userName);
        user.testRunner.closeTheCurrentUserSession();
    }

    @Then("^([^\"]*) has (\\d+) web browser\\(s\\) open$")
    public void thereAreXWebBrowsersOpen(String userName, int expectedNumberOfOpenWebBrowsers) throws Throwable {
        Person user = TestFramework.getUser(userName);
        int actualNumberOpen=user.testRunner.getNumberOfWebBrowsers();
        String assertionTest;
        if (expectedNumberOfOpenWebBrowsers==0) {
            assertionTest = "No web browser is open";
        } else if (expectedNumberOfOpenWebBrowsers==1) {
            assertionTest="One web browser is open";
        } else {
            assertionTest=expectedNumberOfOpenWebBrowsers + " web browsers are open";
        }
        assertionTest=assertionTest+ " [got " + actualNumberOpen + "]";
        assertTrue(assertionTest, actualNumberOpen==(expectedNumberOfOpenWebBrowsers));
    }

    @When("^([^\"]*) navigates to web address \"([^\"]*)\"$")
    public void userNavigatesToWebAddress(String userName, String url) throws Throwable {
        Person user = TestFramework.getUser(userName);
        user.testRunner.currentUserSession.currentWebBrowser.navigation.goToURL(url);
    }

    @Then("^([^\"]*)'s current web address is \"([^\"]*)\"$")
    public void theCurrentWebAddressIs(String userName, String url) throws Throwable {
        Person user = TestFramework.getUser(userName);
        String actualURL=user.testRunner.getCurrentURL();
        assertTrue("The current web address [" + actualURL + "] is " + url, url.equals(actualURL));
    }

}