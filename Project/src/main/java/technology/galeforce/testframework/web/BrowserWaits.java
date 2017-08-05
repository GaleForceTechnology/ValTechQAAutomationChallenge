package technology.galeforce.testframework.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by peter.gale on 16/12/2016.
 */
public class BrowserWaits {

    private Browser parentBrowser;
    public Jquery jquery;
    public WebForms webForms;

    public BrowserWaits(Browser parentBrowser) throws Exception {
        this.parentBrowser = parentBrowser ;
        jquery = new Jquery();
        webForms = new WebForms();
    }

    public class Jquery {

        public void waitForjQueryToBeInactive() {
            // Giving this plenty of time as there may just be delays on the network etc
            WebDriverWait wait = new WebDriverWait(parentBrowser.webDriver, 60,100);
            wait.until(this.jQueryAJAXCallsHaveCompleted());
        }

        private ExpectedCondition<Boolean> jQueryAJAXCallsHaveCompleted() {
            return new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return (Boolean) parentBrowser.jsExecutor.executeScript("return (window.jQuery != null) && (jQuery.active <= 1);");
                }
            };
        }

    }

    public class WebForms {

        // See: https://www.neustar.biz/blog/selenium-tips-wait-with-waitforcondition
        public void waitForWebFormsToBeInactive() {
            try {Thread.sleep(200); } catch (InterruptedException e) {}
            try {
                WebDriverWait wait = new WebDriverWait(parentBrowser.webDriver, 15, 100);
                wait.until(this.webFormsPostBackIsCompleted());
            } catch (WebDriverException ex) {
                // ignore webdriver except in case this app doesn't use web forms
            }
        }

        private ExpectedCondition<Boolean> webFormsPostBackIsCompleted() {
            return new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    // https://msdn.microsoft.com/en-us/library/bb311028.aspx
                    return (Boolean) parentBrowser.jsExecutor.executeScript("return !Sys.WebForms.PageRequestManager.getInstance().get_isInAsyncPostBack();");
                }
            };
        }

    }

}
