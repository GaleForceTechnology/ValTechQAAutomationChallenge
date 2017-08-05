package technology.galeforce.testframework.web;

import technology.galeforce.testframework.TestFramework;
import technology.galeforce.testframework.UserSession;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by peter.gale on 18/10/2016.
 */

public class Browser {

    protected WebDriver webDriver;
    protected JavascriptExecutor jsExecutor;

    protected FileDownloader fileDownloader;

    public BrowserNavigation navigation;
    public BrowserWaits waits;
    public BrowserAlerts alerts;
    private BrowserElement element;
    private BrowserElements elements;
    public ProgressMessage progressMessage;

    // We use this global object to store the web page that we expect the current user to last working on, where this is needed to pass the page objects between step definitions
    // TODO: Assuming the users only ever has one browser open for a real CFH Docmail test - we will need to refactor allWebBrowsers to be a list of objects containing a currentWebBrowser and currentUserWebPage
//    public BaseWebPage currentWebPage;
    private int currentWindowNumber; // Zero based
    private List<String> windowHandles = new ArrayList<String>();
    private List<BaseWebPage> currentWebPages = new ArrayList<BaseWebPage>();

    public UserSession parentUserSession;

    public Browser(UserSession parentUserSession, BrowserFactory.BrowserTypes browserType, Boolean forceLocalBrowser) throws Exception {
        this.parentUserSession = parentUserSession;
        webDriver = BrowserFactory.startWebBrowser(browserType, forceLocalBrowser);
        this.jsExecutor = (JavascriptExecutor) webDriver;
        this.navigation = new BrowserNavigation(this);
        getCurrentWindowHandle();
        currentWindowNumber = 0;
        this.waits = new BrowserWaits(this);
        this.alerts = new BrowserAlerts(this);
        progressMessage = new Browser.ProgressMessage();
    }

    public String getBrowserName() {
        return ((RemoteWebDriver) webDriver).getCapabilities().getBrowserName();
    }

    public void setCurrentWebPage(BaseWebPage baseWebPage) {
        currentWebPages.set(currentWindowNumber, baseWebPage);
    }

    public BaseWebPage getCurrentWebPage() {
        return currentWebPages.get(currentWindowNumber);
    }

    private void getCurrentWindowHandle() {
        windowHandles.add(webDriver.getWindowHandle());
        currentWebPages.add(null);
    }

    public void setCurrentWindowByNumber(int windowNumber) {
        // Get the target window handle - remembering that our list is zero based!
        currentWindowNumber = (windowNumber-1);
        String targetWindowHandle = windowHandles.get(currentWindowNumber);
        webDriver.switchTo().window(targetWindowHandle);
    }

    public void openNewWindowInSameBrowser() {
        // http://darrellgrainger.blogspot.co.uk/2013/11/opening-two-windows-for-one-webdriver.html
        // Open a new tab/window in the same browser session
        jsExecutor.executeScript("window.open();");
        // Get a new list of all the open tabs/windows
        Set<String> allOpenWindows = webDriver.getWindowHandles();
        int numberOfOpenWindows = allOpenWindows.size();
        // Remove any tabs/windows that we already know about
        for (String windowHandle : windowHandles) {
            System.out.println(windowHandle);
            allOpenWindows.remove(windowHandle);
        }
        // The one left must ne the new window
        String newWindowHandle = (String) allOpenWindows.toArray()[0];
        // Tell webdriver to make it current in it's memory
        webDriver.switchTo().window(newWindowHandle);
        // Make the new window the current one
        currentWindowNumber = (numberOfOpenWindows -1); // Zero based!;
        // Add the new window/tab to our list
        windowHandles.add(newWindowHandle);
        // Initialise a new currentWebPage object for the new window
        currentWebPages.add(null);
        //String test = customerWindow.toArray()[0].toString();
    }

    public void minimize() {
        Dimension n = new Dimension(0,0);
        webDriver.manage().window().setSize(n);
    }

    public void activate() {
        // Minimize then maximise the window to force it to the top of the computer's screen
        webDriver.switchTo().window(webDriver.getWindowHandle());
        this.minimize();
        webDriver.manage().window().maximize();
    }

    public void activate_Docmail() {

        // Minimize then maximise the window to force it to the top of the computer's screen
        webDriver.switchTo().window(webDriver.getWindowHandle());
        this.minimize();

        // We need to ensure that the window is fully maximized so that the user's script doesn't interact with a screen of varying size, and so cause mis-clicks/failures

        // Get the minimized screen height
        Long screenHeight = (Long) jsExecutor.executeScript("return window.screen.availHeight;");
        Long screenWidth = (Long) jsExecutor.executeScript("return window.screen.availWidth;");

        // Get the mimimized window height
        Long windowHeight = Long.valueOf(webDriver.manage().window().getSize().height);
        Long windowWidth = Long.valueOf(webDriver.manage().window().getSize().width);

        webDriver.manage().window().maximize();
        this.waits.webForms.waitForWebFormsToBeInactive();

        Boolean waitABit = true;
        while (waitABit) {
            // Stop if the fullscreen window size used is bigger than then screen area currently used by the page
            if (windowHeight>screenHeight && windowWidth>screenWidth) {
                waitABit = false;
            }
            try { Thread.sleep(1); } catch (InterruptedException e) {}
            windowHeight = Long.valueOf(webDriver.manage().window().getSize().height);
            windowWidth = Long.valueOf(webDriver.manage().window().getSize().width);
        }

    }


    public void close() {
        webDriver.quit();
    }

    public BrowserElement getElement(By getElementBy) {
        BrowserElement browserElement = new BrowserElement(this, getElementBy);
        highlightWebElementAsWeUseThem(browserElement, true);
        return browserElement;
    }

    public BrowserElement getElement(By getElementBy, Boolean allowElementHighlighting) {
        BrowserElement browserElement = new BrowserElement(this, getElementBy);
        highlightWebElementAsWeUseThem(browserElement, allowElementHighlighting);
        return browserElement;
    }

    private void highlightWebElementAsWeUseThem(BrowserElement browserElement, Boolean allowElementHighlighting) {
        if (allowElementHighlighting && TestFramework.highlightElements) {
            if (browserElement.is.displayed()) {
                try {
                    browserElement.highlightElement();
                    parentUserSession.parentTestRunner.takeAScreenshotHandler(true);
                    Thread.sleep(30);
                } catch (Exception e) {
                    // Ignore any errors
                }
                try {
                    browserElement.restoreElementStyle();
                } catch (Exception e) {
                    // Ignore any errors
                }
            }
        }
    }

    private String getLabelXpath(String labelText) {
        String xpath = "//label[.='" + labelText + "']";
        return xpath;
    };

    public By getLabelBy(String labelText) {
        return By.xpath(getLabelXpath(labelText));
    }

    public By getElementByLabelForIDBy(String labelText) {
        String forID = this.getElement(getLabelBy(labelText), false).wait.until.displayed().get.attribute("for");
        return By.id(forID);
    }

    public BrowserElement getElementByLabel(String labelText) {
        BrowserElement browserElement = new BrowserElement(this, getElementByLabelForIDBy(labelText));
        highlightWebElementAsWeUseThem(browserElement, true);
        return browserElement;
    }

    // Takes an array or label text alternatives for the current field
    public BrowserElement getElementByLabelWithAlternatives(String labelTextAlternatives) {
        String[] labelTextAlternativesArray = labelTextAlternatives.split("\\|");
        // Default to the first label unless others are found!
        String labelTextToUse = labelTextAlternativesArray[0];
        for (int i = 1; i <= labelTextAlternativesArray.length; i++) {
            if (this.getElement(By.xpath("//label[.='" + labelTextAlternativesArray[i - 1] + "']")).is.displayed()) {
                labelTextToUse =  labelTextAlternativesArray[i - 1];
            }
        }
        if ("".equals(labelTextToUse)) {
            labelTextToUse = labelTextAlternativesArray[0];
        }
        BrowserElement browserElement = new BrowserElement(this, getElementByLabelForIDBy(labelTextToUse));
        highlightWebElementAsWeUseThem(browserElement, true);
        return browserElement;
    }

    public BrowserElements getElements(By getElementsBy) {
        return new BrowserElements(this, getElementsBy);
    }

    public String getUserAgentStringFromBrowser() {
        return jsExecutor.executeScript("return navigator.userAgent").toString();
    }

    public static int getDownloadableFileHTTPStatus_NonBrowser(String fileURL) throws Exception {
        URI fileAsURI = new URI(fileURL);
        FileDownloader fileDownloader;
        fileDownloader = new FileDownloader();
        fileDownloader.setURI(fileAsURI);
        fileDownloader.setHTTPRequestMethod(HTTP.RequestType.GET);
        return fileDownloader.getLinkHTTPStatus();
    }

    public int getDownloadableFileHTTPStatus_Browser(String fileURL) throws Exception {
        URI fileAsURI = new URI(fileURL);
        fileDownloader = new FileDownloader(this.getCookiesFromBrowser(), this.getUserAgentStringFromBrowser());
        fileDownloader.setURI(fileAsURI);
        fileDownloader.setHTTPRequestMethod(HTTP.RequestType.GET);
        return fileDownloader.getLinkHTTPStatus();
    }

    public void downloadFile(String fileURL) throws Exception {
        // NOTE: We need to have checked the fileURL's HTTP status with getDownloadableFileHTTPStatus() first to have instantiated the  fileDownloader object
        fileDownloader.downloadFile(fileURL);
    }

    BasicCookieStore getCookiesFromBrowser() {
        Set<Cookie> seleniumCookieSet=this.webDriver.manage().getCookies();
        BasicCookieStore copyOfWebDriverCookieStore = new BasicCookieStore();
        for (Cookie seleniumCookie : seleniumCookieSet) {
            BasicClientCookie duplicateCookie = new  BasicClientCookie (seleniumCookie.getName(), seleniumCookie.getValue());
            duplicateCookie.setDomain(seleniumCookie.getDomain());
            duplicateCookie.setSecure(seleniumCookie.isSecure());
            duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
            duplicateCookie.setPath(seleniumCookie.getPath());
            copyOfWebDriverCookieStore.addCookie(duplicateCookie);
        }
        return copyOfWebDriverCookieStore;
    }

    private boolean jQueryIsPreLoaded;
    public class ProgressMessage {

//        public ProgressMessage() {
//        }

        public void enableMessaging() {

            // Only enable the display of messages if the user has set this!
            if (TestFramework.showProgressNotifications) {

                // Some pages already have jQuery loaded
//                jQueryIsPreLoaded = (Boolean) jsExecutor.executeScript("return (window.jQuery != null) && (jQuery.active === 0);");
                jQueryIsPreLoaded = (Boolean) jsExecutor.executeScript("return (window.jQuery != null);");
                // The EE Hotel booking system has 1 jQuery running permanently
                //jQueryIsPreLoaded = (Boolean) jsExecutor.executeScript("return (window.jQuery != null) && (jQuery.active <= 1);");

                if (!jQueryIsPreLoaded) {

                    String jQuerySourceURL ="https://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js";
                    //String jQuerySourceURL ="file:///C:/WIP/EqualExperts/Project/src/test/resources/jQuery/jquery.min.js";

                    // We cannot load local js files by default - security
                    // Could host a local node js webserver ...
                    // http://jasonwatmore.com/post/2016/06/22/nodejs-setup-simple-http-server-local-web-server
                    // ... or host the jquery/growl apps on the application itself

                    // Check for jQuery on the page, add it if need be
                    jsExecutor.executeScript(
                        "if (!window.jQuery) " +
                            "{" +
                                "var jquery = document.createElement('script'); " +
                                "jquery.type = 'text/javascript'; " +
                                "jquery.src = '" + jQuerySourceURL + "'; " +
                                "document.getElementsByTagName('head')[0].appendChild(jquery);" +
                            "}"
                    );

                    // Make sure we don't do a jQuery '$' command until jQuery has loaded
                    Boolean jQueryIsLoaded = false;
                    while (!jQueryIsLoaded) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
//                        jQueryIsLoaded = (Boolean) jsExecutor.executeScript("return (window.jQuery != null) && (jQuery.active === 0);");
                        jQueryIsLoaded = (Boolean) jsExecutor.executeScript("return (window.jQuery != null);");
                        ///jQueryIsLoaded = (Boolean) jsExecutor.executeScript("return (window.jQuery != null) && (jQuery.active <= 1);");
                    }

                }

                String jQueryGrowlSourceJSURL ="https://the-internet.herokuapp.com/js/vendor/jquery.growl.js";
                String jQueryGrowlSourceCSSURL ="https://the-internet.herokuapp.com/css/jquery.growl.css";
                //String jQueryGrowlSourceJSURL ="file:///C:/WIP/EqualExperts/Project/src/test/resources/jQuery/jquery.growl.js";
                //String jQueryGrowlSourceCSSURL ="file:///C:/WIP/EqualExperts/Project/src/test/resources/jQuery/jquery.growl.ss";

                // Use jQuery to add jquery-growl to the page
                jsExecutor.executeScript("$.getScript('" + jQueryGrowlSourceJSURL + "')");
                waits.jquery.waitForjQueryToBeInactive();

                // Make sure we don't do a growl command command until growl has loaded
                Boolean growlsLoaded = false;
                while (!growlsLoaded) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    growlsLoaded = (Boolean) jsExecutor.executeScript("return ($.growl != null);");
                    ///jQueryIsLoaded = (Boolean) jsExecutor.executeScript("return (window.jQuery != null) && (jQuery.active <= 1);");
                }

                // Use jQuery to add jquery-growl styles to the page and wait until it has completed
                jsExecutor.executeScript("$('head').append('<link rel=\"stylesheet\" href=\"" + jQueryGrowlSourceCSSURL + "\" type=\"text/css\" />');");
               waits.jquery.waitForjQueryToBeInactive();

            }

        }

        public void displayMessage(String messageType, String title, String message) {
//            // jquery-growl with no frills
//            browser.jsExecutor.executeScript("$.growl({ title: 'GET', message: '/' });");
            // jquery-growl with colourised output
            //browser.jsExecutor.executeScript("$.growl.notice({ title: 'NOTE:', message: 'loaded the \\'" + pageObjectName + "\\' page'});");

//            Browser.this.activate();

            // Only display messages if the user has set this!
            if (TestFramework.showProgressNotifications) {

                enableMessaging();

                // Escape all quotes
                message = message.replace("'", "\\'");
                // The newline character breaks a grow notification, so replace it with "|" which is what the HTML returns for newlines in/across innerText values
                if (message.indexOf("\n")>0) {
                    message = message.replace("\n", "|");
                }

                // Some assertions have embedded "<" characters, which conflicts with html, so replace these with something more compatible
                // See: http://www.freeformatter.com/html-escape.html
                //message = message.replace("<", "&lt;");
                //message = message.replace(">", "&gt;");
                // NOTE: Emedded "<" & ">" characters need to be removed at the message source as we may want to embed HTML in the message elsewhere!

                if (jQueryIsPreLoaded) {
                    // If jQuery is preloaded we may get errors if it is not the latest version, so skip over these!
                    try {
                        jsExecutor.executeScript("$.growl." + messageType + "({ title: '" + title + "', message: '" + message + "'});");
                    } catch (Exception ex) {
                        // Just display the error message to remind us of there being a problem
                        ex.printStackTrace();
                    }
                } else {
                    jsExecutor.executeScript("$.growl." + messageType + "({ title: '" + title + "', message: '" + message + "'});");
                }
                // Make sure the query has been initiated before moving on as we may be taking a screenshot without it on otherwiser!
                waits.jquery.waitForjQueryToBeInactive();
            }
        }

    }

    public void takeAScreenshot(String screenshotFileName) {
        File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(screenshotFileName));
        } catch (IOException e) {
            // Ignore any errors?
            e.printStackTrace();
        }
    }

}
