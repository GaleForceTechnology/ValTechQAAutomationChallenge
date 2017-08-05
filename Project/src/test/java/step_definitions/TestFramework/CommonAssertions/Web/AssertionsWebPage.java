package step_definitions.TestFramework.CommonAssertions.Web;

import technology.galeforce.testframework.web.BaseWebPage;
import step_definitions.TestFramework.CommonAssertions.Assertions;

import static org.junit.Assert.assertTrue;

/**
 * Created by peter.gale on 13/01/2017.
 */
public class AssertionsWebPage {

    //public static void isDisplayed(BaseWebPage baseWebPage, String expectedPage) {
    public static void isDisplayed(BaseWebPage baseWebPage) throws Exception {
        // Checking the internal page name doesn't seem meaningful now! Assertions.Text.matches(baseWebPage, null, "expected web page", expectedPage, baseWebPage.pageObjectName);
        Assertions.Web.Elements.exist(baseWebPage, baseWebPage.listOfPageIsLoadedByes, baseWebPage.pageObjectName + "' page is loaded elements");
    }

    public static void isNotDisplayed(BaseWebPage baseWebPage) throws Exception {
        // Checking the internal page name doesn't seem meaningful now! Assertions.Text.matches(baseWebPage, null, "expected web page", expectedPage, baseWebPage.pageObjectName);
        Assertions.Web.Elements.noneExist(baseWebPage, baseWebPage.listOfPageIsLoadedByes, "" + baseWebPage.pageObjectName + "' page is loaded elements");
    }

}
