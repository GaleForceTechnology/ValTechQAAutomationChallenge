package technology.galeforce.testframework.web;

import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

/**
 * Created by peter.gale on 16/12/2016.
 */
public class BrowserAlerts {

    Browser parentBrowser;

    public BrowserAlerts(Browser parentBrowser) throws Exception {
        this.parentBrowser = parentBrowser;
    }

    public boolean alertIsPresent() {
        // Javascript alerts should appear almost immediately
        WebDriverWait wait = new WebDriverWait(parentBrowser.webDriver, 1, 100);
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public String getAlertText() {
        // Javascript alerts should appear almost immediately
        WebDriverWait wait = new WebDriverWait(parentBrowser.webDriver, 1, 100);
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = parentBrowser.webDriver.switchTo().alert();
            return alert.getText();
        } catch (TimeoutException ex) {
            return null;
        }
    }

    public void acceptAlert() throws IOException {

        if (parentBrowser.webDriver instanceof PhantomJSDriver) {

            // http://stackoverflow.com/questions/8244723/alert-handling-in-selenium-webdriver-selenium-2-with-java
            // http://stackoverflow.com/questions/29820448/how-to-handle-accept-js-alerts-in-phantomjs-using-webdriver

            // headless browsers need to inject javascript before the button is clicked to handle alerts correctly
            // See: http://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.Alert

            //File scrFile = ((TakesScreenshot)currentWebBrowser).getScreenshotAs(OutputType.FILE);
            //FileUtils.copyFile(scrFile, new File("C:\\WIP\\pjs1.png"));

            // This bit doesn't seem to be necessary!
            /*
            ((PhantomJSDriver) currentWebBrowser).executePhantomJS(
                "var page = this; " +
                "page.onConfirm = function(msg) {" +
                    "console.log('CONFIRM: ' + msg);return true;" +
                "};");
            */

        } else {
            WebDriverWait wait = new WebDriverWait(parentBrowser.webDriver, 30, 100);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = parentBrowser.webDriver.switchTo().alert();
            alert.getText();
            alert.accept();
        }
    }

    public void checkAndAcceptAlert_InjectJSForPhantomDriver() {
        // headless browsers need to inject this javascript before the button is clicked to handle alerts correctly
        if (parentBrowser.webDriver instanceof PhantomJSDriver) {
            String result = (String) parentBrowser.jsExecutor.executeScript("window.confirm = function(){return true;}");
        }
    }

}
