package step_definitions.TestFramework;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static org.junit.Assert.assertTrue;

import technology.galeforce.testframework.Person;
import technology.galeforce.testframework.TestFramework;

/**
 * Created by peter.gale on 14/10/2016.
 */

public class UserSteps {

    @Then("^I define a user$")
    public static void iDefineAUser() throws Throwable {
        TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.displayProgressMessage.scenarioStep("Creating a new user");
        // We cannot define a new nameless user is any users exist already - these may be named or nameless! But we can discount the system user
        boolean allowNewNamelessUser = (TestFramework.allUsers.size() == 1);
        // Allow new nameless user to be created?
        if (allowNewNamelessUser) {
            Person newNamelessUser = new Person();
            TestFramework.allUsers.add(newNamelessUser);
        }
    }

    @Given("^I define a user called \"([^\"]*)\"$")
    public static void iDefineAUserCalled(String userName) throws Throwable {

        if (!TestFramework.getSystemUserName().equals(userName)) {
            TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.displayProgressMessage.notice("Creating new user '" + userName + "'");
        }

        // Create a new user only if one doesn't exist with that name already, and if there is no nameless defined either
        Person newNamedUser = new Person(userName);
        boolean allowNewNamedUser = true;
         for (Person user: TestFramework.allUsers) {

            // Don't allow any named users if we have a nameless user already created
            if ("the user".equals(user.getName())) {
                allowNewNamedUser = false;
            }

            // Don't allow new named users if we have a user defined with that name already
            if (userName.equals(user.getName())) {
                allowNewNamedUser = false;
            }
        }
        if (allowNewNamedUser) {
            TestFramework.allUsers.add(newNamedUser);
        }

    }

    @Then("^the user \"([^\"]*)\" is called \"([^\"]*)\"$")
    public void userIsCalledByHisOwnName(String expectedUserName, String actualUserName) throws Exception {
        Person user = new Person(expectedUserName);
        assertTrue("The user '" + expectedUserName + "' is called '" +  actualUserName + "': ", user.getName().equals(actualUserName));
    }

    @Then("^there are (\\d+) user\\(s\\) defined$")
    public void thereAreXUsersDefined(int expectedNumberOfUsers) throws Throwable {
        // NOTE: We always have a system user that shoudl be discounter
        assertTrue("Number of users defined [" +  (TestFramework.allUsers.size()-1) + "] is: " + expectedNumberOfUsers , (TestFramework.allUsers.size()-1)==expectedNumberOfUsers);
    }

    public static void addANonDefaultPropertyToAPersonsTestRunner_MessageWrapper(String userName, String propertyKey, String propertyValue) throws Exception {
        // Do not over-write the property value if the user has requested  to leave the "Default" setting
        if (!"Default".equals(propertyValue)) {
            TestFramework.getUsersCurrentWebPage(userName).parentBrowser.parentUserSession.parentTestRunner.displayProgressMessage.notice("Setting '" + propertyKey + "' to '" + propertyValue + "'");
            TestFramework.environmentProperties.addAPropertyToAPersonsTestRunner(userName, propertyKey, propertyValue);
        }
    }

}
