package step_definitions.TestFramework.CommonAssertions.Web;

import technology.galeforce.testframework.web.BaseWebPage;
import step_definitions.TestFramework.CommonAssertions.Assertions;

import static org.junit.Assert.assertTrue;

/**
 * Created by peter.gale on 12/01/2017.
 */
public class AssertionsWeb {

    public static AssertionsWebLink Link = new AssertionsWebLink();
    public static AssertionsWebPage Page = new AssertionsWebPage();
    public static AssertionsWebElement Element = new AssertionsWebElement();
    public static AssertionsWebElements Elements = new AssertionsWebElements();

    public static void currentURLIs(BaseWebPage baseWebPage, String expectedCurrentURL) throws Exception {
        String actualCurrentURL = baseWebPage.getCurrentURL();
        boolean assertionPasses = expectedCurrentURL.equals(actualCurrentURL);
        String assertionText = null;
        assertionText = "The current URL is '" + expectedCurrentURL;
        Assertions.assertTrueWithMessage(baseWebPage, assertionPasses, assertionText, null, actualCurrentURL);
    }

}
