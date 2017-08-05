package technology.galeforce.testframework.web;

/**
 * Created by peter.gale on 16/12/2016.
 */
public class BrowserNavigation {

    Browser parentBrowser;

    public BrowserNavigation(Browser parentBrowser) throws Exception {
        this.parentBrowser = parentBrowser;
    }

    public void goToURL(String url) {
        parentBrowser.webDriver.get(url);
    }

    public String getCurrentURL() {
        String url="";
        url = parentBrowser.webDriver.getCurrentUrl();
        return url;
    }

    public void back() {
        parentBrowser.webDriver.navigate().back();
    }

    public void refresh() {
        parentBrowser.webDriver.navigate().refresh();
    }

}
