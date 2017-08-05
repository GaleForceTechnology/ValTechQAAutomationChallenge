package technology.galeforce.testframework.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by peter.gale on 16/12/2016.
 */
public class BrowserElements {
    Browser parentBrowser;
    private By getElementsBy;
    List<WebElement> listOfWebElements;

    public BrowserElements(Browser parentBrowser, By getElementsBy) {
        this.parentBrowser = parentBrowser;
        this.getElementsBy = getElementsBy;
        listOfWebElements = parentBrowser.webDriver.findElements(getElementsBy);


    }

    public int countVisibleElements() {
        int count=0;
        for (WebElement ele: listOfWebElements) {
            if (ele.isDisplayed()) {
                count++;
            }
        }
        //return matchingElements.size();
        return count;
    }

    public int countAllElements() {
        return listOfWebElements.size();
    }

}
