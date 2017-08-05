package step_definitions.TestFramework.CommonAssertions.Web;

import technology.galeforce.testframework.TestFramework;
import technology.galeforce.testframework.web.BaseWebPage;
import org.openqa.selenium.By;
import step_definitions.TestFramework.CommonAssertions.Assertions;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by peter.gale on 12/01/2017.
 */
public class AssertionsWebElements {

    public static void exist(BaseWebPage baseWebPage, List<By> byes, String elementName) throws Exception {
        // A list of elements denoted by By's each exist
        Iterator<By> byesIterator = byes.iterator();
        int numberExpected =  byes.size();
        int elementCounter = 0;
        while (byesIterator.hasNext()) {
            By currentBy = byes.get(elementCounter);
            boolean elementExists = baseWebPage.elementExists(currentBy, false);
            elementCounter ++;
            String assertionText;
            if (numberExpected ==1) {
                assertionText = "The \"" + elementName + "\" element is displayed";
            } else {
                assertionText = "The \"" + elementName + "\" element #" + elementCounter + " is displayed";
            }
            Assertions.assertTrueWithMessage(baseWebPage, null, elementExists, assertionText, currentBy, null);
            byesIterator.next();
        }
    }

    public static void noneExist(BaseWebPage baseWebPage, List<By> byes, String elementsListName) throws Exception {
        // A list of elements denoted by By's for which none of them exists
        // i.e. false if any one of them exists
        // We may need a separate elements set existence test to return true if some of them exist
        Iterator<By> byesIterator = byes.iterator();
        int numberExpected =  byes.size();
        int elementCounter = 0;
        while (byesIterator.hasNext()) {
            By currentBy = byes.get(elementCounter);
            boolean elementDoesntExist = baseWebPage.elementDoesntExist(currentBy, false);
            elementCounter ++;
            String assertionText;
            assertionText = "All \"" + elementsListName + "\" elements are not displayed";
            Assertions.assertTrueWithMessage(baseWebPage, null, elementDoesntExist, assertionText, currentBy, null);
            byesIterator.next();
        }
    }

    public static void countIs(BaseWebPage baseWebPage, By by, String elementsListName, int numberExpected) throws Exception {
        countIs(baseWebPage, by, null, elementsListName, numberExpected, false);
    }

    public static void countIs(BaseWebPage baseWebPage, By by, By elementGroupBy, String elementsListName, int numberExpected) throws Exception {
        countIs(baseWebPage, by, elementGroupBy, elementsListName, numberExpected, false);
    }

    public static void countIs(BaseWebPage baseWebPage, By elementBy, String elementsListName, int numberExpected, Boolean onlyVisibleElements) throws Exception {
        countIs(baseWebPage, elementBy, null, elementsListName, numberExpected, onlyVisibleElements);
    }

    public static void countIs(BaseWebPage baseWebPage, By elementBy, By elementGroupBy, String elementsListName, int numberExpected, Boolean onlyVisibleElements) throws Exception {

        int actualNumber;
        if (onlyVisibleElements) {
            actualNumber = baseWebPage.getCountOfVisibleElements(elementBy);
        } else {
            actualNumber = baseWebPage.getCountOfAllElements(elementBy);
        }
        String assertionText = "There are " + numberExpected + " '"  + elementsListName + "' on the '" + baseWebPage.pageObjectName + "' page";
        Boolean correctNumberOfElements = (actualNumber == numberExpected);

        // If we have no matching elements, whether expected or not, highlight the group that it shouuld appear in
        By highlightElementsBy;
        if (actualNumber == 0) {
            highlightElementsBy = elementGroupBy;
        } else {
            highlightElementsBy = elementBy;
        }
        Assertions.assertTrueWithMessage(baseWebPage, correctNumberOfElements, assertionText, highlightElementsBy, "#" + Integer.toString(actualNumber));

    }

    public static void elementExistsInPosition(BaseWebPage baseWebPage, By preProgressMessageClickBy, By allElementsBy, By targetElementBy, String elementName, int position, String elementsListName) throws Exception {

        baseWebPage.highlighting2.highlightElements(allElementsBy,"orange");
        String xpath = null;
        if (allElementsBy.toString().startsWith("By.xpath: ")) {
            xpath =  allElementsBy.toString().substring("By.xpath: ".length());
            xpath = "(" + xpath + ")[" + position + "]";
        }

        // Do nto check elements with no text (Webdriver will see them as not existing!?
        if (!"".equals(elementName)) {

            boolean elementExists = baseWebPage.elementExists(targetElementBy, false);
            String assertionText = "The \"" + elementName + "\" element is displayed in position #" + position + " of the list of " + elementsListName;

            if (elementExists) {
                Assertions.assertTrueWithMessage(baseWebPage, preProgressMessageClickBy, elementExists, assertionText, targetElementBy, null);
            } else {
                Assertions.assertTrueWithMessage(baseWebPage, preProgressMessageClickBy, elementExists, assertionText, By.xpath(xpath), null);
            }
        }

        baseWebPage.highlighting2.restoreHighlightedElements();
    }

    public static void listIs(BaseWebPage baseWebPage, By listBy, String elementsListName, String expectedOptions) throws Exception {
        listIs(baseWebPage, null, listBy, elementsListName, expectedOptions);
    }

    public static void listIs(BaseWebPage baseWebPage, By preProgressMessageClickBy, By listBy, String elementsListName, String expectedOptions) throws Exception {

        String[] expectedOptionsArray = expectedOptions.split("\\|");
        String[] actualOptionsArray = baseWebPage.getArrayListOfElementText(listBy);

        int expectedSelectionFieldOptionCount;
        int actualSelectionFieldOptionCount;
        actualSelectionFieldOptionCount = actualOptionsArray.length;

        String assertionText;
        String currentExpectedOption;

        if ("".equals(expectedOptions)) {

            expectedSelectionFieldOptionCount = 0;

        } else {

            String allOptionsXpath = null;
            String currentOptionXpath;
            if (listBy.toString().startsWith("By.xpath: ")) {
                allOptionsXpath = listBy.toString().substring("By.xpath: ".length());
            } else {
                throw new Exception("Unhandled 'By' type: " + listBy.toString());
            }

            // Check each option exists regardless of order
            String currentActualOption;

            for (int i=1; i<= expectedOptionsArray.length; i++) {

                currentExpectedOption = expectedOptionsArray[i - 1];

                if (TestFramework.highlightElements) {

                    // Assert on each web element individually if we need to highlight each
                    // Ignore elements where no text has been supplied - may no appear on the page
                    if (!"".equals(currentExpectedOption)) {
                        // NOTE: the all options xpath may need wrapping with "(..)"
                        currentOptionXpath = "(" + allOptionsXpath + ")[normalize-space(.)='" + currentExpectedOption + "']";
                        Assertions.Web.Element.exists(
                            baseWebPage,
                            preProgressMessageClickBy,
                            By.xpath(currentOptionXpath),
                            listBy,
                            "'" + elementsListName + "' '" + currentExpectedOption + "' element");
                    }

                }   else {

                    // Assert on the list in total to save on processing time
                    boolean matchFound = false;
                    for (int j=1; j<= actualOptionsArray.length; j++) {
                        currentActualOption = actualOptionsArray[j-1];
                        if (currentActualOption.equals(currentExpectedOption)) {
                            matchFound = true;
                            break;
                        }
                    }
                    assertionText = "Then '" + elementsListName + "' selection field contains the option '" + currentExpectedOption + "'";
                    Assertions.assertTrueWithMessage(baseWebPage, matchFound, assertionText, listBy, null);
                }

            }

            // Check each option exists in the right order
            int size = expectedOptionsArray.length;
            for (int position = 1; position <= size; position++) {

                currentExpectedOption = expectedOptionsArray[position-1];

                if (TestFramework.highlightElements) {

                    currentOptionXpath = "(" + allOptionsXpath + ")[" + position + "][normalize-space(.)='" + currentExpectedOption + "']";
                    Assertions.Web.Elements.elementExistsInPosition(
                        baseWebPage,
                        preProgressMessageClickBy,
                        listBy,
                        By.xpath(currentOptionXpath),
                        currentExpectedOption,
                        position,
                        elementsListName);

                }   else {

                    // Only check for a match if we haven't reached the end of the 'actualOptionsArray' list already
                    if (position <= actualOptionsArray.length) {
                        currentActualOption = actualOptionsArray[position-1];
                        boolean matchFound = (currentActualOption.equals(currentExpectedOption));
                        // String assertionText = "An '" + elementsListName + "' selection field option '" + currentExpectedOption + "' exists in position #" + i + " [got '" + currentActualOption + "' in position #" + i + "]";
                        assertionText = "An '" + elementsListName + "' selection field option '" + currentExpectedOption + "' exists in position #" + position;
                        Assertions.assertTrueWithMessage(baseWebPage, matchFound, assertionText, listBy, "'" + currentActualOption + "' in position #" + position);
                    }

                }

            }

        }

        expectedSelectionFieldOptionCount = expectedOptionsArray.length;
        Boolean correctNumberOfElements = (actualSelectionFieldOptionCount == expectedSelectionFieldOptionCount);

        if (TestFramework.highlightElements) {

            assertionText = "There are " + expectedSelectionFieldOptionCount + " '" + elementsListName;
            Assertions.assertTrueWithMessage(baseWebPage, preProgressMessageClickBy, correctNumberOfElements, assertionText, listBy, Integer.toString(actualSelectionFieldOptionCount));

        }   else {

            correctNumberOfElements = (actualSelectionFieldOptionCount == expectedSelectionFieldOptionCount);
            assertionText = "There are " + expectedSelectionFieldOptionCount + "' " + elementsListName + "' selection field options [got " + actualSelectionFieldOptionCount + "]";
            Assertions.assertTrueWithMessage(baseWebPage, correctNumberOfElements, assertionText, listBy, Integer.toString(actualSelectionFieldOptionCount));

        }

    }

}
