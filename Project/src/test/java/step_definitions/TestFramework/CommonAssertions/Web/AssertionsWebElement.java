package step_definitions.TestFramework.CommonAssertions.Web;

import technology.galeforce.testframework.web.BaseWebPage;
import org.openqa.selenium.By;
import step_definitions.TestFramework.CommonAssertions.Assertions;

import static org.junit.Assert.assertTrue;

/**
 * Created by peter.gale on 12/01/2017.
 */
public class AssertionsWebElement {

    public static void exists(BaseWebPage baseWebPage, By by, String elementName) throws Exception {
        exists(baseWebPage, null, by, null, elementName);
    }

    public static void exists(BaseWebPage baseWebPage, By elementBy, By elementGroupBy, String elementName) throws Exception {
        exists(baseWebPage, null, elementBy, elementGroupBy, elementName);
    }

    // Where possible, pass a 'By' that identifies the element/group of elements that indicate where we expected the element to appear, in case it doesn't exist
    public static void exists(BaseWebPage baseWebPage, By preProgressMessageClickBy, By elementBy, By elementGroupBy, String elementName) throws Exception {
        boolean elementExists = baseWebPage.elementExists(elementBy, false);
        String assertionText = "The \"" + elementName + "\" exists";
        By highlightElementsBy;
        if (elementExists) {
            highlightElementsBy = elementBy;
        } else {
            highlightElementsBy = elementGroupBy;
        }
        Assertions.assertTrueWithMessage(baseWebPage, preProgressMessageClickBy, elementExists, assertionText, highlightElementsBy, null);
    }

    public static void doesntExist(BaseWebPage baseWebPage, By by, String elementName) throws Exception {
        boolean elementDoesntExist = baseWebPage.elementDoesntExist(by, false);
        String assertionText = "The \"" + elementName + "\" does not exist";
        Assertions.assertTrueWithMessage(baseWebPage, elementDoesntExist, assertionText, by, null);
    }

    public static void doesntExist(BaseWebPage baseWebPage, By elementThatShouldNotExistBy, By containingElementBy, String elementName) throws Exception {
        boolean elementDoesntExist = baseWebPage.elementDoesntExist(elementThatShouldNotExistBy, false);
        String assertionText = "The \"" + elementName + "\" does not exist";
        By byToHighlight;
        if (elementDoesntExist) {
            byToHighlight = containingElementBy;
        } else {
            byToHighlight = elementThatShouldNotExistBy;
        }
        Assertions.assertTrueWithMessage(baseWebPage, elementDoesntExist, assertionText, byToHighlight, null);
    }

    // TODO: Change to webElements.list is???
    public static void existsInPosition(BaseWebPage baseWebPage, By by, String elementName, int position, String listName) throws Exception {
        // Check it exists
        exists(baseWebPage, by, elementName);
        // Check it exists in position
        boolean linkExists = baseWebPage.elementExists(by, false);
        String assertionText = "The \"" + elementName + "\" exists in position #" + position + " of the list of " + listName;
        Assertions.assertTrueWithMessage(baseWebPage, linkExists, assertionText, by, null);
    }

    public static void showsAttribute(BaseWebPage baseWebPage, By by, String attribute, String elementName, String expectedValue) throws Exception {
        // Replace any "|" step_def line separators with a "\n", which is what the HTML return for newlines
        String expectedValueToCheck = expectedValue.replace("|", "\n");
        String actualValue = baseWebPage.getElementAttribute(by, attribute);
// Remove any '&nbsp;' characters ????
actualValue = actualValue.replace("\u00a0","");
        String assertionText = "The \"" + attribute + "\" attribute of the \"" + elementName + "\" field is \"" + expectedValue + "\"";
        Boolean showsAttribute = actualValue.equals(expectedValueToCheck);
        Assertions.assertTrueWithMessage(baseWebPage, showsAttribute, assertionText, by, actualValue);
    }

    public static void doesntShowAttribute(BaseWebPage baseWebPage, By by, String attribute, String elementName, String expectedValue) throws Exception {
        doesntShowAttributeContainingText(baseWebPage, by, attribute, elementName, expectedValue, expectedValue);
    }

    public static void doesntShowAttribute(BaseWebPage baseWebPage, By by, String attribute, String elementName, String expectedValueToShowInMessage, String expectedValueToCheck) throws Exception {
        String actualValue = baseWebPage.getElementAttribute(by, attribute);
        String assertionText = "The \"" + attribute + "\" attribute of the \"" + elementName + "\" field is not \"" + expectedValueToShowInMessage + "\"";
        Boolean doesntShowAttribute = !actualValue.equals(expectedValueToCheck);
        Assertions.assertTrueWithMessage(baseWebPage, doesntShowAttribute, assertionText, by, actualValue);
    }

    public static void showsAttributeIgnoringCase(BaseWebPage baseWebPage, By by, String attribute, String elementName, String expectedValue) throws Exception {
        String actualValue = baseWebPage.getElementAttribute(by, attribute);
        String assertionText = "The \"" + elementName + "\" field shows the attribute \"" + attribute + "\" of \"" + expectedValue + "\" (ignoring case)";
        Boolean attributeMatches = actualValue.equalsIgnoreCase(expectedValue);
        Assertions.assertTrueWithMessage(baseWebPage, attributeMatches, assertionText, by, actualValue);
    }

    public static void showsAttributeContainingText(BaseWebPage baseWebPage, By by, String attribute, String elementName, String expectedValue) throws Exception {
        showsAttributeContainingText(baseWebPage, by, attribute, elementName, expectedValue, expectedValue);
    }

    public static void showsAttributeContainingText(BaseWebPage baseWebPage, By by, String attribute, String elementName, String expectedValueToShowInMessage, String expectedValueToCheck) throws Exception {
        String actualValue = baseWebPage.getElementAttribute(by, attribute);
        String assertionText = "The \"" + attribute + "\" attribute of the \"" + elementName + "\" field contains the text \"" + expectedValueToShowInMessage + "\"";
        int position = actualValue.indexOf(expectedValueToCheck);
        Boolean attributeContainText = (position>=0);
        Assertions.assertTrueWithMessage(baseWebPage, attributeContainText, assertionText, by, actualValue);
    }

    public static void doesntShowAttributeContainingText(BaseWebPage baseWebPage, By by, String attribute, String elementName, String expectedValue) throws Exception {
        doesntShowAttributeContainingText(baseWebPage, by, attribute, elementName, expectedValue, expectedValue);
    }

    public static void doesntShowAttributeContainingText(BaseWebPage baseWebPage, By by, String attribute, String elementName, String expectedValueToShowInMessage, String expectedValueToCheck) throws Exception {
        String actualValue = baseWebPage.getElementAttribute(by, attribute);
        if (expectedValueToShowInMessage == null) {
            expectedValueToShowInMessage = expectedValueToCheck;
        }
        String assertionText = "The \"" + attribute + "\" attribute of the \"" + elementName + "\" field doesn't contains the text \"" + expectedValueToShowInMessage + "\"";
        int position = actualValue.indexOf(expectedValueToCheck);
        Boolean attributeDoesntContainText = (position<0);
        Assertions.assertTrueWithMessage(baseWebPage, attributeDoesntContainText, assertionText, by, actualValue);
    }

    public static void showsCSSAttribute(BaseWebPage baseWebPage, By by, String cssAttribute, String elementName, String expectedValue) throws Exception {
        // Replace any "|" step_def line separators with a "\n", which is what the HTML return for newlines
        String expectedValueToCheck = expectedValue.replace("|", "\n");
        String actualValue = baseWebPage.getElementCSSAttribute(by, cssAttribute);
// Remove any '&nbsp;' characters ????
        actualValue = actualValue.replace("\u00a0","");
        String assertionText = "The \"" + cssAttribute + "\" CSS attribute of the \"" + elementName + "\" field is \"" + expectedValue + "\"";
        Boolean showsAttribute = actualValue.equals(expectedValueToCheck);
        Assertions.assertTrueWithMessage(baseWebPage, showsAttribute, assertionText, by, actualValue);
    }

    public static void textIsRenderedInUppercase(BaseWebPage baseWebPage, By by, String elementName) throws Exception {
        String outerText = baseWebPage.getElementAttribute(by, "outerText");
        String expectedUppercaseText = elementName.toUpperCase();
        String assertionText = "The \"" + elementName + "\" element is rendered in uppercase - expected \"" + expectedUppercaseText + "\"";
        Boolean attributeMatches = outerText.equals(expectedUppercaseText);
        Assertions.assertTrueWithMessage(baseWebPage, attributeMatches, assertionText, by, outerText);
    }

    public static void existsAndTextIsRenderedInUppercase(BaseWebPage baseWebPage, By by, String elementName) throws Exception {
        exists(baseWebPage, by, elementName);
        textIsRenderedInUppercase(baseWebPage, by, elementName);
    }

    public static void enabled(BaseWebPage baseWebPage, By by, String elementName, boolean expectEnabled) throws Exception {
        if (expectEnabled) {
            boolean elementIsEnabled = baseWebPage.elementIsEnabled(by);
            String assertionText = "The \"" + elementName + "\" element is enabled";
            Assertions.assertTrueWithMessage(baseWebPage, elementIsEnabled, assertionText, by, null);
        } else {
            boolean elementIsNotEnabled = baseWebPage.elementIsNotEnabled(by);
            String assertionText = "The \"" + elementName + "\" element is disabled";
            Assertions.assertTrueWithMessage(baseWebPage, elementIsNotEnabled, assertionText, by, null);
        }
    }

    public static void isChecked(BaseWebPage baseWebPage, By by, String elementName, boolean expectChecked) throws Exception {
        if (expectChecked) {
            boolean elementIsChecked = baseWebPage.elementIsChecked(by);
            String assertionText = "The \"" + elementName + "\" element is checked";
            Assertions.assertTrueWithMessage(baseWebPage, elementIsChecked, assertionText, by, null);
        } else {
            boolean elementIsNotChecked = baseWebPage.elementIsNotChecked(by);
            String assertionText = "The \"" + elementName + "\" element is not checked";
            Assertions.assertTrueWithMessage(baseWebPage, elementIsNotChecked, assertionText, by, null);
        }
    }

}
