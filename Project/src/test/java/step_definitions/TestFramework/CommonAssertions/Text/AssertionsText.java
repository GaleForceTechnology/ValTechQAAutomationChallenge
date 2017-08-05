package step_definitions.TestFramework.CommonAssertions.Text;

import technology.galeforce.testframework.BasePage;
import technology.galeforce.testframework.web.BaseWebPage;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import step_definitions.TestFramework.CommonAssertions.Assertions;

import static org.junit.Assert.assertTrue;

/**
 * Created by peter.gale on 16/01/2017.
 */

public class AssertionsText {

//    // We may need to make assertions outside of a browser later on, but not yet!
//    public static void matches(String itemName, String expectedValue, String actualValue) {
//        // Checks outside a web browser
//        matches(null, null, itemName, expectedValue, actualValue);
//    }

    public static void matches(BasePage basePage, By affectedElementsBy, String itemName, String expectedValue, String actualValue) throws Exception {
        boolean matches = expectedValue.equals(actualValue);
        String assertionText = "The '" + itemName + "' is '" + expectedValue + "'";
        Assertions.assertTrueWithMessage(basePage, matches, assertionText, affectedElementsBy, actualValue);
    }

    public static void doesntMatch(BaseWebPage baseWebPage, By affectedElementsBy, String itemName, String expectedValue, String actualValue) throws Exception {
        boolean doesntMatch = !expectedValue.equals(actualValue);
        String assertionText = "The '" + itemName + "' is not '" + expectedValue + "'";
        Assertions.assertTrueWithMessage(baseWebPage, doesntMatch, assertionText, affectedElementsBy, actualValue);
    }

    public static void isNotNull(BaseWebPage baseWebPage, By affectedElementsBy, String itemName, String actualValue) throws Exception {
        isNotNull(baseWebPage, affectedElementsBy, itemName, actualValue, false);
    }

    public static void isNotNull(BaseWebPage baseWebPage, By affectedElementsBy, String itemName, String actualValue, Boolean maskValue) throws Exception {
        boolean isNotNull = (actualValue != null);
        if (maskValue) {
            actualValue = StringUtils.repeat("*", actualValue.length());
        }
        String assertionText = "The '" + itemName + "' ['" + actualValue + "'] is not null";
        Assertions.assertTrueWithMessage(baseWebPage, isNotNull, assertionText, affectedElementsBy, actualValue);
    }

}