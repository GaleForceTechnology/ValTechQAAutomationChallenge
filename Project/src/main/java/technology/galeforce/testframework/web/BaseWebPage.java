package technology.galeforce.testframework.web;

import technology.galeforce.testframework.BasePage;
import technology.galeforce.testframework.Person;
import technology.galeforce.testframework.TestFramework;
import org.openqa.selenium.*;
import technology.galeforce.testframework.TestRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.gale on 18/10/2016.
 */

public abstract class BaseWebPage extends BasePage {

    public BaseWebPage parentPage;
    public Person user;
    public String pageObjectName;
    public List<By> listOfPageIsLoadedByes;
    public Browser parentBrowser;
    public Browser.ProgressMessage progressMessage;
    public Highlighting highlighting1;
    public Highlighting highlighting2;

    public TestRunner parentTestRunner;

    public BaseWebPage() {
        super();
    }

    public BaseWebPage(String userName, String pageObjectName, By... pageIsLoadedByes) throws Exception {
        super("Blah");
        this.user = TestFramework.getUser(userName);
        constructorCommon(userName, pageObjectName, pageIsLoadedByes);
        parentBrowser.setCurrentWebPage(this);
        this.parentTestRunner = this.parentBrowser.parentUserSession.parentTestRunner;
    }

    public BaseWebPage(String userName, String pageObjectName, String directURL, Boolean goToItNow, By... pageIsLoadedByes) throws Exception {
        super("Blah");
        this.user = TestFramework.getUser(userName);
        this.directURL = directURL;
        if (goToItNow) {
            this.goDirectToPageByURL();
        }
        constructorCommon(userName, pageObjectName, pageIsLoadedByes);
        parentBrowser.setCurrentWebPage(this);
    }

    public BaseWebPage(BaseWebPage parentPage, String pageObjectName, By... pageIsLoadedByes) throws Exception {
        super("Blah");
        this.parentPage = parentPage;
        this.user = parentPage.user;
        this.pageLoadTimeOutInSeconds = pageLoadTimeOutInSeconds;
        // set in common???        this.parentBrowser=parentPage.browser;
        constructorCommon(parentPage.user.getName(), pageObjectName, pageIsLoadedByes);
        parentBrowser.setCurrentWebPage(parentPage);
    }

    public BaseWebPage(BaseWebPage parentPage, String pageObjectName, Integer pageLoadTimeOutInSeconds, By... pageIsLoadedByes) throws Exception {
        super("Blah");
        this.parentPage = parentPage;
        this.user = parentPage.user;
        this.pageLoadTimeOutInSeconds = pageLoadTimeOutInSeconds;
        // set in common???        this.parentBrowser=parentPage.browser;
        constructorCommon(parentPage.user.getName(), pageObjectName, pageIsLoadedByes);
        parentBrowser.setCurrentWebPage(parentPage);
    }

    private void constructorCommon(String userName, String pageObjectName, By... pageIsLoadedByes) {
        this.pageObjectName = pageObjectName;
        // User is only passing a single by, so formulate a List
        this.listOfPageIsLoadedByes = new ArrayList<By>();
        for (By by : pageIsLoadedByes) {
            this.listOfPageIsLoadedByes.add(by);
        }
        parentBrowser = user.testRunner.currentUserSession.currentWebBrowser;
        // No need to show messages or inject jQuery etc if we are cloning an already loaded page!
        // Always wait for page to load - in particular before injecting jQuery
        this.waitForPageToLoad(parentPage);
        highlighting1 = new Highlighting();
        highlighting2 = new Highlighting();
    }

//    public boolean isDisplayed() {
//        boolean allExist = true;
//        boolean currentExists;
//        for (By currentPageIsLoadedBy : listOfPageIsLoadedByes) {
//            currentExists = parentBrowser.getElement(currentPageIsLoadedBy, false).is.displayed();
//            if (!currentExists) {
//                allExist = false;
//            }
//        }
//        return allExist;
//    }

//    public boolean isNotDisplayed() {
//        boolean allDontExist = true;
//        boolean currentDoesntExist;
//        for (By currentPageIsLoadedBy : listOfPageIsLoadedByes) {
//            currentDoesntExist = parentBrowser.getElement(currentPageIsLoadedBy).isNot.displayed();
//            if (!currentDoesntExist) {
//                allDontExist = false;
//            }
//        }
//        return allDontExist;
//    }

    private Integer pageLoadTimeOutInSeconds = 60;
    public BaseWebPage waitForPageToLoad(BaseWebPage callingPage) {
        for (By currentPageIsLoadedBy : listOfPageIsLoadedByes) {
            parentBrowser
                .getElement(currentPageIsLoadedBy, false)
                .setTimeOut(pageLoadTimeOutInSeconds, 500)
                .wait
                .until
                .displayed();
        }
        // Now inject grow if necessary for the current page
        if (TestFramework.showProgressNotifications) {
            parentBrowser.progressMessage.enableMessaging();
        }

        return callingPage;
    }

    public boolean alertIsPresent() {
        return parentBrowser.alerts.alertIsPresent();
    }

    public String getAlertText() {
        return parentBrowser.alerts.getAlertText();
    }

    public void acceptAlert() throws IOException {
        parentBrowser.alerts.acceptAlert();
    }

    public By getH1TagHeaderBy() {
        return By.xpath("//h1");
    }

    public String getCurrentURL() {
        return user.testRunner.getCurrentURL();
    }

    public void goToURL(String url) {
        parentBrowser.navigation.goToURL(url);
    }

    public void navigateBack() {
        parentBrowser.navigation.back();
    }


    public By getElementByLabelForIDBy(String label) {
        return parentBrowser.getElementByLabelForIDBy(label);
    }

    public class Highlighting {

        private By elementsHighlightedBy;
        private int countOfElements;
        private String[] originalStyle;
        private String[] originalDisabledStatus;
        private String multipleElementXPath = "";

        BrowserElement currentWebElement;

        public void highlightElements(By myBy, String highlightColour) throws Exception {

            if (TestFramework.highlightElements) {
//                if (browserElement.is.displayed()) {

                elementsHighlightedBy = myBy;

                //  All or just visible ... do different tests require different counts?
                countOfElements = getCountOfAllElements(elementsHighlightedBy);
                if (countOfElements == 0) {
                } else {
                    currentWebElement = parentBrowser.getElement(elementsHighlightedBy, false);
                    originalStyle = new String[countOfElements];
                    originalDisabledStatus = new String[countOfElements];

                    if (elementsHighlightedBy.toString().startsWith("By.xpath: ")) {
                        multipleElementXPath = elementsHighlightedBy.toString().substring("By.xpath: ".length());
                    } else if (elementsHighlightedBy.toString().startsWith("By.id: ")) {
                        multipleElementXPath = "//*[@id='" + elementsHighlightedBy.toString().substring("By.id: ".length()) + "']";
                    } else {
                        throw new Exception("Unhandled element by: '" + elementsHighlightedBy.toString());
                    }
                    for (int elementCounter = 1; elementCounter <= countOfElements; elementCounter++) {
                        String currentElementXpath = "(" + multipleElementXPath + ")[" + elementCounter + "]";
                        currentWebElement = parentBrowser.getElement(By.xpath(currentElementXpath), false);
                        originalStyle[elementCounter - 1] = currentWebElement.get.attribute("style");
                        originalDisabledStatus[elementCounter - 1] = currentWebElement.get.attribute("disabled");
                        currentWebElement.highlightElement(highlightColour);
                        // We are disabling the element momentarily, as some elements do not change their appearance with just a 'style' attribute change, ie the CSS stylesheet overrides any local styles
                        if (originalDisabledStatus[elementCounter - 1] == null) {
                            currentWebElement.setElementDisabledAttribute("disabled");
                        } else {
                            currentWebElement.removeElementDisabledAttribute();
                        }
                    }
                }

                // Pause a little for the user to see what's displayed
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

        public void restoreHighlightedElements() {
            if (TestFramework.highlightElements) {
                if (countOfElements == 0) {
                    // Do nothing?
//            } else if (countOfElements == 1) {
//                currentWebElement.restoreHighlightedElement(originalStyle[0], originalDisabledStatus[0]);
                } else {
                    for (int elementCounter = 1; elementCounter <= countOfElements; elementCounter++) {
                        String currentElementXpath = "(" + multipleElementXPath + ")[" + elementCounter + "]";
                        currentWebElement = parentBrowser.getElement(By.xpath(currentElementXpath), false);
                        currentWebElement.restoreHighlightedElement(originalStyle[elementCounter - 1], originalDisabledStatus[elementCounter - 1]);
                    }
                }
            }
        }

    }

    public boolean elementExists(By myBy, Boolean allowElementHighlighting) {
        //waitALittleForTheElementToBeRenderedIgnoringAnyTimeoutError(myBy);
        return parentBrowser.getElement(myBy, allowElementHighlighting).is.displayed();
    }

    public boolean elementDoesntExist(By myBy, Boolean allowElementHighlighting) {
        // The element will be highlighted if it does exist but shouldn't
        return parentBrowser.getElement(myBy, allowElementHighlighting).isNot.displayed();
    }

    public void clickOnElement(By elementBy, Boolean allowElementHighlighting) {
        parentBrowser.getElement(elementBy, allowElementHighlighting).click.single();
    }

    public String getElementAttribute(By myBy, String attribute) {
        // Don't highlight these elements
        String attributeValue;
        if ("text".equalsIgnoreCase(attribute)) {
            attributeValue = parentBrowser.getElement(myBy, false).get.text();
        } else {
            attributeValue = parentBrowser.getElement(myBy, false).get.attribute(attribute);
        }
        if (attributeValue == null) {
            attributeValue = "";
        }
        return attributeValue;
    }

    public String getElementCSSAttribute(By myBy, String cssAttribute) {
        // Don't highlight these elements
        String attributeText = parentBrowser.getElement(myBy, false).get.cssAttribute(cssAttribute);
        if (attributeText == null) {
            attributeText = "";
        }
        return attributeText;
    }

    public boolean elementIsEnabled(By myBy) {
        boolean isChecked = parentBrowser.getElement(myBy).is.enabled();
        return isChecked;
    }

    public boolean elementIsNotEnabled(By myBy) {
        boolean isNotChecked = parentBrowser.getElement(myBy).isNot.enabled();
        return isNotChecked;
    }

    public boolean elementIsChecked(By myBy) {
        boolean isChecked = parentBrowser.getElement(myBy).is.checked();
        return isChecked;
    }

    public boolean elementIsNotChecked(By myBy) {
        boolean isNotChecked = parentBrowser.getElement(myBy).isNot.checked();
        return isNotChecked;
    }

    public int getCountOfAllElements(By myBy) {
        return parentBrowser.getElements(myBy).countAllElements();
    }

    public int getCountOfVisibleElements(By myBy) {
        return parentBrowser.getElements(myBy).countVisibleElements();
    }

    public String[] getArrayListOfElementText(By myBy) {
        List<WebElement> myElements = parentBrowser.getElements(myBy).listOfWebElements;
        String [] returnArray = new String[myElements.size()];
        int counter = 0;
        for(WebElement e : myElements) {
            String elementText = e.getText();
            returnArray[counter]= e.getText();
            counter ++;
        }
        return returnArray;
    }

    // Any web page may have a URL which takes us directly to it
    protected String directURL;

    public String getDirectURL() {
        return directURL;
    }

    public void goDirectToPageByURL() throws Exception {
        user.testRunner.displayProgressMessage.action("Navigating to the url: " + directURL);
        user.testRunner.currentUserSession.currentWebBrowser.navigation.goToURL(directURL);
        user.testRunner.enableMessaging();
        user.testRunner.displayProgressMessage.action("Navigated to the url: " + directURL);
    }


}
