package step_definitions.TestFramework.CommonAssertions;

import technology.galeforce.testframework.BasePage;
import technology.galeforce.testframework.TestFramework;
import technology.galeforce.testframework.TestRunner;
import technology.galeforce.testframework.web.BaseWebPage;
import org.openqa.selenium.By;
import step_definitions.TestFramework.CommonAssertions.Text.AssertionsText;
import step_definitions.TestFramework.CommonAssertions.Web.AssertionsWeb;

import static org.junit.Assert.assertTrue;

/**
 * Created by peter.gale on 12/01/2017.
 */
public class Assertions {

    public static AssertionsWeb Web = new AssertionsWeb();
    public static AssertionsText Text = new AssertionsText();

    public static void assertTrueWithMessage(BasePage basePage, By preProgressMessageClickBy, boolean assertionPasses, String assertionText, By elementsBy, String gotText) throws Exception {

        boolean isWebPage = basePage instanceof BaseWebPage;
        boolean isWindowsPage = false;

//        if (elementsBy != null) {
//            String highlightColour;
//            if (assertionPasses) {
//                highlightColour = "chartreuse";
//            } else {
//                highlightColour = "red";
//            }
//            ((BaseWebPage)basePage).highlighting1.highlightElements(elementsBy, highlightColour);
//        }

        // Output messages to system users screen if we haven't supplied a user page or this is a windows app
        TestRunner testRunnerForOutput;
        if (basePage == null ) {
                testRunnerForOutput = TestFramework.getUser(TestFramework.getSystemUserName()).testRunner;
        } else {
            testRunnerForOutput = ((BaseWebPage)basePage).parentBrowser.parentUserSession.parentTestRunner; //.user.testRunner;
        }

        if (isWebPage) {
            // Expand a selector if necessary if this is a web page
            if (preProgressMessageClickBy != null) {
                ((BaseWebPage) basePage).clickOnElement(preProgressMessageClickBy, false);
            } else {
                // Webdriver screenshots close any selector,  so using desktop screenshots here instead
                ((BaseWebPage) basePage).parentBrowser.parentUserSession.parentTestRunner.setDesktopScreenshots();
            }
        }

// Expand the selector if necessary
if (isWebPage) {
    if (elementsBy != null) {
        String highlightColour;
        if (assertionPasses) {
            highlightColour = "chartreuse";
        } else {
            highlightColour = "red";
        }
        ((BaseWebPage) basePage).highlighting1.highlightElements(elementsBy, highlightColour);
    }
}
        if (assertionPasses) {
            testRunnerForOutput.displayProgressMessage.assertionPassed(assertionText);
        } else {
            assertionText = assertionText + " - failed!";
            if (gotText != null) {
                assertionText = assertionText + " [got '" + gotText + "']";
            }
            testRunnerForOutput.displayProgressMessage.assertionFailed(assertionText);
        }

        if (isWebPage) {
            // Contract the selector if necessary
            if (preProgressMessageClickBy != null) {
                ((BaseWebPage) basePage).clickOnElement(preProgressMessageClickBy, false);
                // Revert to desktop screenshots off
                ((BaseWebPage) basePage).parentBrowser.parentUserSession.parentTestRunner.setBrowserScreenshots();
            }
        }

        if (TestFramework.recordAVideo) {
            // Pause a little to the user see the screen before the system closes down
            try {Thread.sleep(1000);} catch (InterruptedException e) {
            }
        }

        assertTrue(assertionText, assertionPasses);

        if (isWebPage) {
            if (elementsBy != null) {
                ((BaseWebPage) basePage).highlighting1.restoreHighlightedElements();
            }
        }

        if (isWindowsPage) {
            // Restore the system user's settings
            TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.currentUserSession.currentWindowsControllerIsActiveSession = false;
        }


    }

    public static void assertTrueWithMessage(BasePage basePage, boolean assertionPasses, String assertionText, By elementsBy, String gotText) throws Exception {
        assertTrueWithMessage(basePage, null, assertionPasses, assertionText, elementsBy, gotText);
    }

}