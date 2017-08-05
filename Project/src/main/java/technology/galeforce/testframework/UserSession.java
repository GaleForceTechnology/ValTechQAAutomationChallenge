package technology.galeforce.testframework;

import technology.galeforce.testframework.web.Browser;
import technology.galeforce.testframework.web.BrowserFactory;

import java.util.Properties;

/**
 * Created by peter.gale on 10/11/2016.
 */

public class UserSession {

//    public class UserSessionTypes {
//        public final static String WINDOWS = "Windows";
//        public final static String WEB = "Web Browser";
//    }

    // Each user session will host either a web app or a windows app ... though for the windows app messages are displayed in the systems browser page
    public enum UserSessionTypes {
        WINDOWS ("Windows"),
        WEB ("Web Browser");
        private final String browserType;
        private UserSessionTypes(final String browserType) { this.browserType = browserType; }
        public String getName() {
            return browserType;
        }
    }

    public UserSessionTypes currentUserSessionType;

    public Browser currentWebBrowser;
    public Boolean browserIsOpen = false;
    public boolean currentWindowsControllerIsActiveSession;
    // Allow tests to store ad-hoc values for use against test runs
    public Properties properties = new Properties();

    // Constructor
    public TestRunner parentTestRunner;

    public UserSession(TestRunner parentTestRunner, UserSessionTypes userSsessionType, BrowserFactory.BrowserTypes browserType, Boolean forceLocalBrowser) throws Exception {

        this.parentTestRunner = parentTestRunner;
        this.currentUserSessionType = userSsessionType;

        if (UserSessionTypes.WEB.equals(currentUserSessionType)) {
            // TODO: For now, we open a browser automatically for each user session - ie UserSession and WebBrowser are synonymous for now?
            this.currentWebBrowser = new Browser(this, browserType, forceLocalBrowser);
            browserIsOpen = true;
        } else if (UserSessionTypes.WINDOWS.equals(currentUserSessionType)) {
            // This is a windows user sessions, so point all messages to the system web page,e.g.
            this.currentWebBrowser = TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.currentUserSession.currentWebBrowser;
        } else {
            throw new Exception("Unhandled session type '" + currentUserSessionType + "'");
        }

    }

    public void close() {
        currentWebBrowser.close();
        browserIsOpen = false;
    }

}
