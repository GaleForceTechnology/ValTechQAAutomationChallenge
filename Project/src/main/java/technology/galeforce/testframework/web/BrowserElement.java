package technology.galeforce.testframework.web;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.support.ui.Select;

/**
 * Created by peter.gale on 16/12/2016.
 */
public class BrowserElement {

    public BrowserElement thisBrowserElement;
//    Browser parentBrowser;
    WebDriver webDriver;
    JavascriptExecutor jsExecutor;

    private By getElementBy;
    public WebElement webElement;
    private boolean elementIsPresent;
    public Wait wait;
    public Is is;
    public IsNot isNot;
    public Get get;
    public Click click;
    public Input input;
    public Selection selection;

    Long longTimeOutInSeconds;
    Long longSleepTimeout;

    public BrowserElement(Browser parentBrowser, By getElementBy) {
        thisBrowserElement = this;
//        this.parentBrowser = parentBrowser;
        this.webDriver = parentBrowser.webDriver;
        this.jsExecutor = parentBrowser.jsExecutor;
        this.getElementBy = getElementBy;
        this.tryToGetWebElement();
        wait = new Wait();
        is = new Is();
        isNot = new IsNot();
        get = new Get();
        click = new Click();
        input = new Input();
        selection = new Selection();
    }

    public BrowserElement setTimeOut(int timeOutInSeconds, int sleepTimeout) {
        this.longTimeOutInSeconds = Long.valueOf(timeOutInSeconds);
        this.longSleepTimeout = Long.valueOf(sleepTimeout);
        return this;
    }

    private void tryToGetWebElement() {
        try {
            webElement = webDriver.findElement(getElementBy);
            elementIsPresent = true;
        } catch (NoSuchElementException Ex) {
            elementIsPresent = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Is {

        public boolean present() {
//        // List<WebElement> matchingElements=webDriver.findElements(byWhat);
//        // return !matchingElements.isEmpty();
//        try {
//            WebElement elementToFind = parentBrowser.webDriver.findElement(byWhat);
//            return true;
//        } catch (NoSuchElementException Ex) {
//            return false;
//        }
            return elementIsPresent;
        }

        public boolean enabled() {
            if (webElement.isEnabled()) {
                return true;
            } else {
                return false;
            }
        }

        public boolean displayed() {
////        try {
////            //WebElement elementToFind = webDriver.findElement(byWhat);
////            if elementIsPresent(byWhat) ) {
////
////                return elementToFind.isDisplayed();
////            } else {
////                return false;
////            }
////        } catch (NoSuchElementException Ex) {
////            return false;
////        }
//        return this.getElement(byWhat).isPresent();
            if (webElement == null ) {
                return false;
            } else {
                return webElement.isDisplayed();
            }
        }

        public boolean checked() {
            if (webElement.isSelected()) {
                return true;
            } else {
                return false;
            }
        }

    }

    public class IsNot {


        public boolean present() {
//        List<WebElement> matchingElements=webDriver.findElements(byWhat);
////        return Integer.valueOf(0) == Integer.valueOf(matchingElements.size());
//        return matchingElements.isEmpty();
            return !elementIsPresent;
        }

        public boolean enabled() {
            if (webElement.isEnabled()) {
                return false;
            } else {
                return true;
            }
        }

        public boolean displayed() {
//        try{
//            WebElement elementToFind=webDriver.findElement(byWhat);
//            // This assumes the element is present but may not be visible, so throws an error if the element is not present
//            return !elementToFind.isDisplayed();
//        } catch (NoSuchElementException Ex ){
//            // element not found, so can't be displayed!
//            return true;
//        }
            if (webElement == null ) {
                return true;
            } else {
                try {
                    return !webElement.isDisplayed();
                } catch (StaleElementReferenceException ex) {
                    // The element was there but has now disappeared? So, ...
                    return true;
                }
            }
        }

        public boolean checked() {
            if (webElement.isSelected()) {
                return false;
            } else {
                return true;
            }
        }

    }

    public class Get {

        public String text() {
            if (webElement == null) {
                return null;
            }
            else {
                return webElement.getText();
            }
        }

        public String attribute(String attributeKey) {
            String attribute;
            try {
                attribute = webElement.getAttribute(attributeKey);
            } catch (NullPointerException npe){
                attribute = "";
            }
            return attribute;
        }

        public String cssAttribute(String attributeKey) {
            if (webElement == null) {
                return "";
            }
            else {
                return webElement.getCssValue(attributeKey);
            }
        }

        public int coordinateX() {
            return webElement.getLocation().getX();
        }

        public int coordinateY() {
            return webElement.getLocation().getY();
        }

        public int height() {
            return webElement.getSize().getHeight();
        }
//    public String getElementHeight(By byWhat) {
//        WebElement elementToFind=currentWebBrowser.findElement(byWhat);
//        return elementToFind.getCssValue("height");
//    }

        public int width() {
            return webElement.getSize().getWidth();
        }
//    public String getElementWidth(By byWhat) {
//        WebElement elementToFind=currentWebBrowser.findElement(byWhat);
//        return elementToFind.getCssValue("width");
//    }

    }

    public class Wait {

        public Until until;
        public UntilNot untilNot;


        public Wait() {
            longTimeOutInSeconds = Long.valueOf(15);
            longSleepTimeout = Long.valueOf(500);
            until = new Until();
            untilNot = new UntilNot();
        }


        public class Until {

            public BrowserElement present() {
                WebDriverWait wait = new WebDriverWait(webDriver, longTimeOutInSeconds, longSleepTimeout);
                wait.until(ExpectedConditions.presenceOfElementLocated(getElementBy));
                tryToGetWebElement();
                return thisBrowserElement;
            }

            public BrowserElement displayed() {
                WebDriverWait wait = new WebDriverWait(webDriver, longTimeOutInSeconds, longSleepTimeout);
                wait.until(ExpectedConditions.presenceOfElementLocated(getElementBy));
                // TODO: Should we eliminate the following wait by the time take for the first condition to be met?
                wait.until(ExpectedConditions.visibilityOfElementLocated(getElementBy));
                tryToGetWebElement();
                return thisBrowserElement;
            }

            public BrowserElement clickable() {
                WebDriverWait wait = new WebDriverWait(webDriver, longTimeOutInSeconds, longSleepTimeout);
                wait.until(ExpectedConditions.elementToBeClickable(getElementBy));
                tryToGetWebElement();
                return thisBrowserElement;
            }

            public void disappears() {
                WebDriverWait wait = new WebDriverWait(webDriver, longTimeOutInSeconds, longSleepTimeout);
                wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(getElementBy)));
            }

        }

        public class UntilNot {

//        public void untilNotVisible() {
//// The built in WebDriver wait assumes that the element is present to start off with!
////        WebDriverWait wait = new WebDriverWait(currentWebBrowser, 15, 500);
////        wait.until(ExpectedConditions.invisibilityOfElementLocated(byWhat));
//            if (webElement.isDisplayed()) {
//                try {
//                    Thread.sleep(500);
//                    waitForElementNotVisible(byWhat);
//                } catch (InterruptedException ex) {
//                    // Ignore
//            }
//
//        }
//        }

            public void displayed() {
                WebDriverWait wait = new WebDriverWait(webDriver, longTimeOutInSeconds, longSleepTimeout);
                wait.until(this.elementNotVisible());
            }

            private ExpectedCondition<Boolean> elementNotVisible() {
                return new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        //webElement = webDriver.findElement(getElementBy);
                        tryToGetWebElement();
                        return (Boolean) thisBrowserElement.isNot.displayed();
                    }
                };
            }

        }
    }

    public class Click {

        public Object byJavascript() {
            return jsExecutor.executeScript("arguments[0].click();", webElement);
        }

        public void single() {
            thisBrowserElement.wait.until.clickable();
            webElement.click();
        }

        public void ddouble() {
            thisBrowserElement.wait.until.clickable();
            Actions action = new Actions(webDriver);
            action.doubleClick(webElement).build().perform();
        }

        public void andDragByOffset(int offsetX, int offsetY) {
            thisBrowserElement.wait.until.clickable();
            Actions action = new Actions(webDriver);
            action.clickAndHold(webElement).moveByOffset(offsetX,offsetY).release().perform();
        }

    }

    public class Input {

        public BrowserElement clear() {
            thisBrowserElement.wait.until.displayed();
            webElement.clear();
            return thisBrowserElement;
        }

        public void sendKeys(String inputText) {
            // UPLOADING FILES WITH SENDKEYS FILENAME DOESN'T WORK IF WE HAVE A WAIT FOR THE ELEMENT IN SENDKEYS
            // Disabling this unless it causes problems elsewhere
            //waitForElementPresentAndVisible(byWhat);
            //waitForElementPresent(byWhat);
            webElement.sendKeys(inputText);
        }

        public void text(String inputText) {
            wait.until.displayed().input.sendKeys(inputText);
        }

        public void multilineTextWithTAB(String inputText) {
            // Replace all "|" step_def line separators with a {carriage return}}
            inputText = inputText.replace("|", Keys.RETURN);
            // Tab out of the field when the text is entered
            inputText = inputText  + Keys.TAB;
            wait.until.displayed().input.sendKeys(inputText);
        }

    }
    public class Selection {

        public void optionFromADropDownList(String selectionValue) {
            // wait for the select to appear if it isn't there yet
            if (webElement == null) {
                wait.until.present();
            }
            // Store the select element
            WebElement selectWebElement = webElement;

            // Now wait until the option to be selected is present
            String sourceXpath =  getElementBy.toString().substring(10) + "//option[.='" + selectionValue + "']";
            getElementBy = By.xpath(sourceXpath);
            wait.until.present();

            // Now select the option value from the select web element
            new Select(selectWebElement).selectByVisibleText(selectionValue);
        }

    }

    private String strOriginalStyle = null;
    private String strOriginalDisabled = null;

    public String getStyle() {
        return webElement.getAttribute("style");
    }
    public String getDisabledStatus() {
        return webElement.getAttribute("disabled");
    }

    protected void highlightElement() {
        // Make sure the target element is in focus on the screen
        new Actions(this.webDriver).moveToElement(webElement).perform();
//
//        // Always scroll elements into view
//        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", browserElement);
//        // Make sure the target element is in focus on the screen
//        new Actions(this.webDriver).moveToElement(webElement).perform();


        // Store the original style
        strOriginalStyle = webElement.getAttribute("style");
        // We are disabling the element momentarily also, as some elements do not change their appearance with just a 'style' attribute change, ie the CSS stylesheet overrides any local styles
        strOriginalDisabled  = webElement.getAttribute("disabled");
        // Highlight the element
        // Use same styling as for Sikuli
//    jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, "style", "border: 5px solid chartreuse; color: chartreuse;");
//        jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, "style", "border: 2px solid chartreuse;");
        // Using a solid background color only to avoid distorting the layouts, as a border does
//        highlightElement("chartreuse");
        highlightElement("orange");

        if (strOriginalDisabled == null) {
            setElementDisabledAttribute("disabled");
        } else {
            removeElementDisabledAttribute();
        }
        //jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, "style", "border: 1px solid blue; background-color: chartreuse;");
    }

    protected void restoreElementStyle() {
        restoreHighlightedElement(strOriginalStyle, strOriginalDisabled);
    }

    public void setProperty(String property, String value) {
        jsExecutor.executeScript("arguments[0]." + property + " = \"" + value + "\";", webElement);
    }

    public void setAttribute(String attribute, String value) {
        jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, attribute, value);
    }

    public void highlightElement(String backgroundColour) {
        // Make sure the target element is in focus on the screen
        new Actions(this.webDriver).moveToElement(webElement).perform();
        // Now highlight it
        //jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, "style", "background-color: " + backgroundColour + ";");
        setAttribute("style", "background-color: " + backgroundColour + ";");
    }

    public void restoreHighlightedElement(String strOriginalStyle, String strOriginalDisabled) {
        jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, "style", strOriginalStyle);
        if (strOriginalDisabled == null) {
            removeElementDisabledAttribute();
        } else {
            setElementDisabledAttribute(strOriginalDisabled);
        }
    }

    public void removeElementDisabledAttribute() {
        jsExecutor.executeScript("arguments[0].removeAttribute(arguments[1], arguments[1])", webElement, "disabled");
    }

    public void setElementDisabledAttribute(String value) {
        jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", webElement, "disabled", value);
    }


}

