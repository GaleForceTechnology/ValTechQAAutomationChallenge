package technology.galeforce.testframework;

/**
 * Created by peter.gale on 18/10/2016.
 */

public abstract class BasePage {


    public BasePage() {
        // We don't want users to instantiate a page object without passing some parameters, so throw an error if we get here
        throw new AssertionError("Must use a defined constructor");
    }

    public BasePage(String someText) {
        // Got a constructor!
    }

}
