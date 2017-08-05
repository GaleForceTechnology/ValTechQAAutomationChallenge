package technology.galeforce.testframework.web;

import technology.galeforce.testframework.TestFramework;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.EnumSet;

/**
 * Created by peter.gale on 16/12/2016.
 */
public class BrowserFactory {

    static final String BROWSER_TYPE_PROPERTY_NAME = "Target.Browser.Type";
    static final String BROWSER_LOCATION_PROPERTY_NAME = "Target.Browser.Location";
    static final String BROWSER_HUB_IP_ADDRESS_PROPERTY_NAME = "Target.Browser.HUBIPAddress";

    public enum BrowserTypes {
        CHROME ("Chrome"),
        FIREFOX ("Firefox"),
        PHANTOMJS ("PhantomJS"),
        SELENIUM_GRID_CHROME ("SeleniumGrid-Chrome");
        private final String browserType;
        BrowserTypes(final String browserType) { this.browserType = browserType; }
        public String getName() {
            return browserType;
        }
    }

    private static String defaultBrowserLocation;
    private static String defaultBrowserHUBIPAddress;
    public static Process seleniumGridHUBServer;
    public static Process seleniumGridNODEServer;

    public static BrowserTypes getDefaultBrowserType() throws Exception {
        String defaultBrowserName = TestFramework.getAProperty(BROWSER_TYPE_PROPERTY_NAME);
        // If passed through the command line, use Chrome
        if ("".equals(defaultBrowserName) | defaultBrowserName == null) {
            return BrowserTypes.CHROME;
        } else {
            return getBrowserType(defaultBrowserName);
        }
    }

    public static BrowserTypes getBrowserType(String targetBrowserName) throws Exception {

        BrowserTypes targetBrowserType = null;

        boolean matchedBrowser = false;
        Set<BrowserTypes> browserTypes = EnumSet.allOf(BrowserTypes.class);
        for(BrowserTypes browserType : browserTypes) {
            if (browserType.getName().equals(targetBrowserName)) {
                targetBrowserType = browserType;
                matchedBrowser = true;
            }
        }
        if (matchedBrowser) {
            return targetBrowserType;
        } else {
            throw new Exception("Unhandled browser name: '" + targetBrowserName +"'");
        }

    }

    public static WebDriver startWebBrowser(BrowserTypes browserType, Boolean forceLocalBrowser) throws Exception {

        defaultBrowserLocation = TestFramework.getAProperty(BROWSER_LOCATION_PROPERTY_NAME);
        if ("".equals(defaultBrowserLocation) | (defaultBrowserLocation == null | forceLocalBrowser)) {

            if (BrowserTypes.CHROME.equals(browserType)) {
                return startWebBrowser_Chrome();
            } else if (BrowserTypes.FIREFOX.equals(browserType)) {
                return startWebBrowser_Firefox();
            } else if (BrowserTypes.PHANTOMJS.equals(browserType)) {
                return startWebBrowser_PhantomJS();
            } else {
                throw new Exception("Unhandled browser type: '" + browserType.getName() + "'");
            }

        } else if ("SeleniumGrid".equals(defaultBrowserLocation)) {

            defaultBrowserHUBIPAddress = TestFramework.getAProperty(BROWSER_HUB_IP_ADDRESS_PROPERTY_NAME);
            // We are starting HUB Manually for now
            // ensureLocalGridIsStarted();
            if ("".equals(defaultBrowserHUBIPAddress)) {
                throw new Exception("No HUB IP Address specified");
            } else {
                if (BrowserTypes.CHROME.equals(browserType)) {
                    return startWebBrowser_ChromeInASeleniumGrid();
                } else {
                    throw new Exception("Unhandled browser type: '" + browserType.getName() + "'");
                }
            }

        } else {
            throw new Exception("Unhandled browser location: '" + defaultBrowserLocation + "'");
        }

    }

    private static void ensureLocalGridIsStarted() throws Exception {
        int gridHTTPStatus = Browser.getDownloadableFileHTTPStatus_NonBrowser(defaultBrowserHUBIPAddress);
        // Returns -1 if not grid not started.start
        if (!(gridHTTPStatus == 200)) {
            startSeleniumGridHUBServer();
        }
        startASeleniumGridNODEServer();
    }

    private static void startSeleniumGridHUBServer() throws Exception {
        File seleniumGridHUBServerFile = new File("../SettingUpForAutomation/SeleniumGrid/StartTheHub.bat");
        if (seleniumGridHUBServerFile.exists()) {
            try {
                String executableFileURL = seleniumGridHUBServerFile.getAbsolutePath();
                seleniumGridHUBServer = Runtime.getRuntime().exec(executableFileURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("The file '" + seleniumGridHUBServerFile + "' doesn't exist");
        }
    }

    public static void stopSeleniumGridHUBServer() {
        // The hub server may have been started outside of the current JVM's control, so it will need stoppping manually then!
        if (!(seleniumGridHUBServer == null)) {
            seleniumGridHUBServer.destroy();
        }
    }

    private static void startASeleniumGridNODEServer() throws Exception {
        File seleniumGridNODEServerFile = new File("../SettingUpForAutomation/SeleniumGrid/StartANode.bat");
        if (seleniumGridNODEServerFile.exists()) {
            try {
                String executableFileURL = seleniumGridNODEServerFile.getAbsolutePath();
                seleniumGridNODEServer = Runtime.getRuntime().exec(executableFileURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("The file '" + seleniumGridNODEServerFile + "' doesn't exist");
        }
    }

    public static void stopASeleniumGridNODEServer() {
        // The node server may have been started outside of the current JVM's control, so it will need stopping manually then!
        if (!(seleniumGridNODEServer == null)) {
            seleniumGridNODEServer.destroy();
        }
    }

    private static WebDriver startWebBrowser_ChromeInASeleniumGrid() throws Exception {

        //DesiredCapabilities capability = DesiredCapabilities.chrome();

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("-allow-running-insecure-content");
        options.addArguments("test-type");
        options.addArguments("--start-maximized");
        options.addArguments("--incognito");
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        String remoteWebDriverHubURL;
        //remoteWebDriverHubURL = "http://localhost:4444/wd/hub";
        remoteWebDriverHubURL = defaultBrowserHUBIPAddress;
        WebDriver driverOnLocalGrid;
        try {
            driverOnLocalGrid = new RemoteWebDriver(new URL(remoteWebDriverHubURL), capabilities);
        } catch (Exception e) {
            throw new Exception("Unable to start browser on local grid");
        }
        return driverOnLocalGrid;

    }

    private static WebDriver startWebBrowser_Chrome() {
        // See: https://sites.google.com/a/chromium.org/chromedriver/downloads
        // See: https://sites.google.com/a/chromium.org/chromedriver/capabilities

        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "C:\\WIP\\ValtechQAAutomationChallenge\\Downloads\\ChromeDriver\\v2.29\\chromedriver.exe");
        ChromeDriverService service = ChromeDriverService.createDefaultService();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("-allow-running-insecure-content");

        /*
            Need to add the "test-type" option to Chrome to avoid getting the ...
                "You are using an unsupported command-line flag: --ignore-certifcate-errors. Stability and security will suffer."
            ... message.
            See: https://code.google.com/p/chromedriver/issues/detail?id=799
        */
        options.addArguments("test-type");
// Setting the incognito option stops Chrome asking if we want to save our password
// ... but causes problems when maximising/minimising windows
//        options.addArguments("--incognito");
        options.addArguments("--start-maximized");


        // Chrome driver 2.28 “Chrome is being controlled by automated test software” notification .Can it be removed?
        // http://sqa.stackexchange.com/questions/26051/chrome-driver-2-28-chrome-is-being-controlled-by-automated-test-software-notif
        options.addArguments("disable-infobars");

//???? ok? does this help?
options.addArguments("disable-extensions");

        // Setting this option supposedly removes the password prompt, but I can't get the "addUserProfilePreference" options method
        //   Webdriver error reports v2.52 so is it actually using v3?
        // http ://sqa.stackexchange.com/questions/26275/how-to-disable-chrome-save-your-password-selenium-java
        // http://stackoverflow.com/questions/42793277/chromedriver-user-preferences-ignored
        // options.setExperimentalOption("profile.password_manager_enabled", false);

        // ChromeOptions AddUserProfilePreference only avaiable for dotNet?
        // Java: http://stackoverflow.com/questions/43077737/chromeoptions-adduserprofilepreference
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        Boolean addProxy=false;
        if (addProxy) {
            BrowserMobProxy browserMobProxyServer = new BrowserMobProxyServer();
            browserMobProxyServer.start();
            Proxy seleniumProxyConfiguration = ClientUtil.createSeleniumProxy(browserMobProxyServer);
            capabilities.setCapability(CapabilityType.PROXY, seleniumProxyConfiguration);
        }

        WebDriver newWebDriver = new ChromeDriver(service, capabilities);
        return newWebDriver;

    }

    private static WebDriver startWebBrowser_Firefox() {

        // Firefox seems to have problems in Selenium 3 as of yet

        // http://learn-automation.com/use-firefox-selenium-using-geckodriver-selenium-3/
        //System.setProperty("webdriver.firefox.marionette","K:\\IT\\QA Test Team\\Automation\\Resources\\FirefoxDriver\\geckodriver.exe");
        System.setProperty("webdriver.gecko.driver","K:\\IT\\QA Test Team\\Automation\\Resources\\FirefoxDriver\\geckodriver.exe");
        return new FirefoxDriver();

        //https://github.com/Ardesco/Selenium-Maven-Template/pull/28#issuecomment-244793227
//        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
//        capabilities.setCapability("marionette", true);
//        webBrowser = new FirefoxDriver(capabilities);

    }

    private static WebDriver startWebBrowser_PhantomJS() {

        // https://rumandrye.wordpress.com/2013/03/12/software-test-automation-with-java-phantomjs-and-selenium/

        System.setProperty("phantomjs.binary.path","K:\\IT\\QA Test Team\\Automation\\Resources\\PhantomJS\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();

        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability("takesScreenshot", true);

        return new PhantomJSDriver(capabilities);
    }

}
