package technology.galeforce.testframework;

import technology.galeforce.testframework.web.BrowserFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static java.awt.Toolkit.getDefaultToolkit;

/**
 * Created by peter.gale on 14/10/2016.
 */

public class TestRunner {

    private List<UserSession> allSessionsForCurrentUser;
    public UserSession currentUserSession;
    private Integer currentSessionNumber = 0;
    public DisplayProgressMessage displayProgressMessage = new DisplayProgressMessage();
    public Person parentPerson;

    // Constructor
    public TestRunner(Person parentPerson) throws Exception {
        // Initialise the list of user session
        //allWebBrowsers = new ArrayList<WebDriver>();
        allSessionsForCurrentUser = new ArrayList<UserSession>();
        this.parentPerson = parentPerson;
    }

    public void tearDown() {
        if (!(allSessionsForCurrentUser==null)) {
            if (allSessionsForCurrentUser.size()>0) {
                for (UserSession userSession : allSessionsForCurrentUser) {
                    currentUserSession = userSession;
                    closeTheCurrentUserSession();
                }
                currentUserSession = null;
                allSessionsForCurrentUser = null;
            }
        }
    }

    public void openNewWindowsUserSession() throws Exception {
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArchitecture = System.getProperty("os.arch");
        currentUserSession = new UserSession(this, UserSession.UserSessionTypes.WINDOWS, null, false);
        openUserSession_Common("Windows", " on " + osName + " version " + osVersion + " (" + osArchitecture + ")");
    }

    public void openNewWebBrowserUserSession(BrowserFactory.BrowserTypes browserType, Boolean forceLocalBrowser) throws Exception {
        openNewWebBrowserUserSession_Common(null, browserType, forceLocalBrowser);
    }

    public void openNewWebBrowserUserSession(Boolean forceLocalBrowser) throws Exception {
        openNewWebBrowserUserSession_Common(null, null, forceLocalBrowser);
    }

    public void openNewWebBrowserUserSession(String sessionName, Boolean forceLocalBrowser) throws Exception {
        openNewWebBrowserUserSession_Common(sessionName, null, forceLocalBrowser);
    }

    private void openNewWebBrowserUserSession_Common(String sessionName, BrowserFactory.BrowserTypes browserType, Boolean forceLocalBrowser) throws Exception {
        if (sessionName == null ) {
            sessionName = UserSession.UserSessionTypes.WEB.toString();
        }
        if (browserType ==  null) {
            browserType = BrowserFactory.getDefaultBrowserType();
        }
        currentUserSession = new UserSession(this, UserSession.UserSessionTypes.WEB, browserType, forceLocalBrowser);

        // We can take a browser screenshot now we have a browser open
        setBrowserScreenshots();
        openUserSession_Common(sessionName, " in '" + currentUserSession.currentWebBrowser.getBrowserName() + "'");
    }

    public void openNewWindowInTheCurrentWebBrowserSession() throws Exception {
        currentUserSession.currentWebBrowser.openNewWindowInSameBrowser();
    }

    public void setCurrentWindowByNumber(int windowNumber) throws Exception {
        currentUserSession.currentWebBrowser.setCurrentWindowByNumber(windowNumber); // Zero based!
    }

    private void openUserSession_Common(String sessionName, String messageSuffix) throws Exception {
        allSessionsForCurrentUser.add(currentUserSession);
        int sessionNumber = allSessionsForCurrentUser.size();
        enableMessaging();
        if (!TestFramework.getSystemUserName().equals(parentPerson.getName())) {
            displayProgressMessage.action("The user '" + parentPerson .getName() + "' started new '" + sessionName + "' session (#" + sessionNumber + ") " + messageSuffix);
        }

    }

    public void setSessionByNumber(int sessionNumber) throws Exception {

        if (currentSessionNumber != sessionNumber) {
            // Minimize all windows to get them out of the way
            for (UserSession userSession : allSessionsForCurrentUser) {
                userSession.currentWebBrowser.minimize();
            }
            currentUserSession = allSessionsForCurrentUser.get((sessionNumber - 1));
            currentSessionNumber = sessionNumber;
            currentUserSession.currentWebBrowser.activate();
            displayProgressMessage.action("Switched to user session #" + sessionNumber);
        }
    }

    public String getCurrentURL() {
        if (currentUserSession==null) {
            return "";
        } else {
            return currentUserSession.currentWebBrowser.navigation.getCurrentURL();
        }
    }

    public void closeTheCurrentUserSession() {
        // Set the previously opened web browser as the current browser
        // TODO: May need to get the first open user session here?
        currentUserSession.close();

        // Using .remove on the list seems to cause "ConcurrentModificationExceptions"
        //allSessionsForCurrentUser.remove(currentUserSession);
        // So we'll just make the object null
        currentUserSession = null;

        if (getNumberOfWebBrowsers() != 0) {
            currentUserSession = allSessionsForCurrentUser.get(0);
        }
    }

    public String getCurrentWebBrowserType() {
        return  currentUserSession.currentWebBrowser.getBrowserName();
    }

    public int getNumberOfWebBrowsers() {
        if (allSessionsForCurrentUser.size()==0) {
            return 0;
        } else {
            int numberOfOpenWebBrowsers=0;
            if (allSessionsForCurrentUser.size()>0) {
                for (UserSession userSession : allSessionsForCurrentUser) {
                    if (userSession.browserIsOpen) {
                        numberOfOpenWebBrowsers++;
                    }
                }
            }
            return numberOfOpenWebBrowsers;
        }

    }

    public TestRunner enableMessaging() throws Exception {
        // If the user doesn't currently have a browser open, we will use the system users browser session
        if (currentUserSession.browserIsOpen) {
            currentUserSession.currentWebBrowser.progressMessage.enableMessaging();
        } else {
            // The system users browser should already be enabled for messaging
            // TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.enableMessaging();
        }
        return TestRunner.this;
    }

    public class DisplayProgressMessage {

        // For now, we assuming that the user will only have a web browser session, so we only need to dispaly messages there
        // If/when other UIs are automated, then we may need to choose where / how to disaply the progrss message

        private void takeAMessageBasedScreenshot () throws Exception {
            if (TestFramework.takeScreenshots) {
                // Pause a little to let the message appear before we take a screenshot
                try { Thread.sleep(250); } catch (InterruptedException e) {}
                takeAScreenshotHandler();
            }
        }

        public void error(String message) throws Exception {
            currentUserSession.currentWebBrowser.progressMessage.displayMessage("error", "PASSED!", message);
            takeAMessageBasedScreenshot();
        }

        public void assertionPassed(String message) throws Exception {
            currentUserSession.currentWebBrowser.progressMessage.displayMessage("notice", "PASSED!", message);
            takeAMessageBasedScreenshot();
        }

        public void assertionFailed(String message) throws Exception {
            currentUserSession.currentWebBrowser.progressMessage.displayMessage("error", "FAILED!", message);
            takeAMessageBasedScreenshot();
        }

        public void notice(String message) throws Exception {
            currentUserSession.currentWebBrowser.progressMessage.displayMessage("notice", "NOTE:", message);
            takeAMessageBasedScreenshot();
        }

        public void warning(String message) throws Exception {
            takeAMessageBasedScreenshot();
            currentUserSession.currentWebBrowser.progressMessage.displayMessage("warning", "WARNING!", message);
        }

        public void scenarioStep(String message) throws Exception {
            currentUserSession.currentWebBrowser.progressMessage.displayMessage("warning", "SCENARIO STEP:", message);
            takeAMessageBasedScreenshot();
        }

        public TestRunner action(String message) throws Exception {
            currentUserSession.currentWebBrowser.progressMessage.displayMessage("warning", "ACTION:", message);
            takeAMessageBasedScreenshot();
            return TestRunner.this;
        }

    }

    // Always take desktop screenshot until we have opened a browser
    private boolean desktopScreenshots = true;

    public void setDesktopScreenshots () {
        desktopScreenshots = true;
    }

    public void setBrowserScreenshots () {
        desktopScreenshots = false;
    }

    // Overrider to force taking a screenshot, e.g. on test failures
    public void takeAScreenshotHandler(boolean overrideUserSetting) throws Exception {
        Boolean currentScreenShotTakingSession = TestFramework.takeScreenshots;
        TestFramework.takeScreenshots =  true;
        takeAScreenshotHandler();
        TestFramework.takeScreenshots =  currentScreenShotTakingSession;
    }

    // NOTE: We should try to encourage only taking a screenshot when a message is displayed, but sometimes we need to take one without a new message
    public void takeAScreenshotHandler() throws Exception {

        if (TestFramework.takeScreenshots) {

            String screenShotFileName = getNextScreenShotFileName();

            UserSession.UserSessionTypes activeUserSessionType;
            if (UserSession.UserSessionTypes.WINDOWS.equals(currentUserSession.currentUserSessionType)) {
                if (currentUserSession.currentWindowsControllerIsActiveSession) {
                    // The current web page will be the main page for the framework system user
                    activeUserSessionType = UserSession.UserSessionTypes.WINDOWS;
                } else {
                    activeUserSessionType = UserSession.UserSessionTypes.WEB;
                }
            } else {
                activeUserSessionType = currentUserSession.currentUserSessionType;
            }

            if (UserSession.UserSessionTypes.WEB.equals(activeUserSessionType)) {
                screenShotFileName = screenShotFileName + ".png";
                if (desktopScreenshots) {
                    // Take a java screenshot see - http://stackoverflow.com/questions/4490454/how-to-take-a-screenshot-in-java
                    BufferedImage image = null;
                    try {
                        // NOTE: I had problems with this command but it seems to be working now - not entirely sure why!?
                        image = new Robot().createScreenCapture(new Rectangle(getDefaultToolkit().getScreenSize()));
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                    try {
                        ImageIO.write(image, "png", new File(screenShotFileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Take a web browser based screenshot
                    currentUserSession.currentWebBrowser.takeAScreenshot(screenShotFileName);
                }
            } else if (UserSession.UserSessionTypes.WINDOWS.equals(activeUserSessionType)) {
                // Take a browser screenshot to capture the message displayed
                TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.currentUserSession.currentWebBrowser.takeAScreenshot(screenShotFileName + "a.png");
            } else {
                throw new Exception("Unhandled user session type '" + currentUserSession.currentUserSessionType + "'");
            }

        }

    }

    public String getNextScreenShotFileName() {
        TestFramework.screenshotNumber ++;
            String screenshotNumberPaddedWithLeadingZeros = String.format("%04d", TestFramework.screenshotNumber);
        String nextScreenshotFileName = TestFramework.getScreenshotDirectory() + "\\" + screenshotNumberPaddedWithLeadingZeros;
        return nextScreenshotFileName;
    }

    // NOTE: We should try to encourage only taking a screenshot when a message is displayed, but sometimes we need to take one without a new message
    public void takeADesktopScreenshot() throws Exception {
        setDesktopScreenshots();
        takeAScreenshotHandler();
        setBrowserScreenshots();
    }

}