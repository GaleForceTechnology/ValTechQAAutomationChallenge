package step_definitions.TestFramework;

import technology.galeforce.testframework.TestFramework;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.*;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import java.util.Iterator;

import static org.junit.Assert.*;

public class TestFrameworkSteps {

    // Store these here or in RunCukesTest? Or Both? - Need to replicate these to TestFramework!!!???
    private static String previousFeatureName = null;
    private static String currentFeatureName = null;
    private static int currentFeatureFileNumber = 0;
    private static Scenario currentScenario;
    private static int currentScenarioInFeatureInstanceNumber;
    private static int currentConcreteSubScenarioInFeatureInstanceNumber;
    private static boolean isRunning = false;

    // Setting a low order so this runs first
    @Before(order=1)
    // The @Before hook runs before each scenario, even before any background steps
    public static void beforeCallingScenario(Scenario scenario) throws Throwable {

        //Class<ScenarioImpl> scenarioClass = ScenarioImpl.class;
        //System.out.println("ScenarioName: " + scenario.getName());
        //System.out.println("ScenarioID: " + scenario.getId());
        //System.out.println("ScenarioClass: " + scenario.getClass().getName());

        // NOTE: Scenario names don't have to be unique so the ScenarioId allocated to them will be duplicated across scenarios
        // Scenario's and Scenario Outlines seem to be instances of the same object,
        // ... however in the case of the latter, the ScenarioID is appended by ";;#" where # is the corresponding exampel table row (starting at 2)
        // So we can use this information to determine whether we have started a new scenario and/or moved to a new example in the outline.

        // Determine the feature name from a temporary tag passed from test run control spreadsheet, or set manually in the central feature set.
        Boolean weAreStartingANewFeature;
        currentFeatureName = "'[Unknown Feature Name]";
        String featureNamePrefix = "@FeatureName-";
        for (Iterator<String> iterator = scenario.getSourceTagNames().iterator(); iterator.hasNext();) {
            String tagName = iterator.next().toString();
            // Do not display the feature name tag
            if (tagName.indexOf(featureNamePrefix) == 0 ) {
                currentFeatureName = tagName.substring(featureNamePrefix.length());
            }
        }
        if ((previousFeatureName == null) || (!currentFeatureName.equals(previousFeatureName))) {
            weAreStartingANewFeature = true;
        } else {
            weAreStartingANewFeature = false;
        }
        previousFeatureName = currentFeatureName;

        if (weAreStartingANewFeature) {
            currentFeatureFileNumber++;
            currentScenarioInFeatureInstanceNumber = 0;
        }

        // Store the scenario details before doing anything else
        currentScenario = scenario;
        // For output purposes, we will identify scenarios concretely by a unique number for the individual Scenario or ScenarioOutline group,
        // ... plus a concrete sub scenario instance number - 1 for scenario, or the data row number for scenario outlines

        // The structure of Cucumbers internal scenario Id is:
        // ... for Scenarios: feature name; scenario name
        // ... for Scenario Outlines: feature name; scenario name;examples table name (if any); row in examples table
        // where the names are modified to remove spaces etc.
        // So Scenario Outlines will have 3 semicolons, scenarios only 1

        // Check for the number of name parts (';' separators)
        String currentScenarioID = scenario.getId();
        int countOfSemiColons = StringUtils.countMatches(currentScenarioID,";");

        Boolean scenarioIsAnOutlineScenario = (countOfSemiColons == 3); // -1 => false, else true!
        Boolean weAreStartingANewScenario;
        if (scenarioIsAnOutlineScenario) {
            int rowNumberStartPosition = currentScenarioID.lastIndexOf(";");
            String scenarioSuffixNumber = currentScenarioID.substring(rowNumberStartPosition + 1);
            currentConcreteSubScenarioInFeatureInstanceNumber = Integer.parseInt(scenarioSuffixNumber) -1; // these start at 2!
            weAreStartingANewScenario = (currentConcreteSubScenarioInFeatureInstanceNumber == 1);
        } else {
            // None outline scenarios are always the start of a new concrete scenario instance
            weAreStartingANewScenario = true;
            // Set this a s0 which indicates not to create the next level of folder details
            currentConcreteSubScenarioInFeatureInstanceNumber = 0;
        }

        if (weAreStartingANewScenario) {
            currentScenarioInFeatureInstanceNumber++;
        }

        // Now we can configure the new instance of the Test Framework - for which we may need to use the scenario details
        TestFramework.configureTheTestFramework(
            currentFeatureName,
            currentFeatureFileNumber,
            currentScenario.getName(),
            currentScenarioInFeatureInstanceNumber,
            currentConcreteSubScenarioInFeatureInstanceNumber);

        TestFramework
            .getUser(
                TestFramework.getSystemUserName())
                    .testRunner.currentUserSession.currentWebBrowser
                        .getElement(By.xpath("//h3"), false)
                        .setProperty("textContent", "Scenario: " + scenario.getName());

        String innerHTML = null;
        for (Iterator<String> iterator = scenario.getSourceTagNames().iterator(); iterator.hasNext();) {
            String tagName = iterator.next().toString();
            if (tagName.indexOf(featureNamePrefix) == 0 ) {
                // Prefixing, as the tags come out in reverse order
                if (innerHTML == null) {
                    innerHTML = "<p>" + tagName + "</p>";
                } else {
                    innerHTML = "<p>" + tagName + "</p>" + innerHTML;
                }
                //System.out.println(tagName);
            }
        }
        if (innerHTML == null) {
            innerHTML = "{none}";
        }
        innerHTML = "<h4>Scenario Tag Names:</h4>" + innerHTML;
        TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.currentUserSession.currentWebBrowser.getElement(By.xpath("//div[@id='tagNames']"), false).setProperty("innerHTML", innerHTML);

        TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.enableMessaging();
        // Always takes screenshot when starting the new scenario fails (to show the scenario name)
        TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.takeAScreenshotHandler(true);

        String outputLine = "Starting Scenario: " + TestFramework.getCurrentScenarioFullName();
        TestFramework.addLineToLog(outputLine);

        //testFrameworkUser.testRunner.displayProgressMessage.notice("Test!");
// Note we could dynamically add a teardown hook here as per this thread:  https://groups.google.com/forum/#!msg/cukes/Z2mWgk0rSgs/4F2H85PcmRsj
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                for (Person user : TestFramework.users) {
//                    user.testRunner.closeAllWebBrowsers();
//                    TestFramework.users.remove(user);
//                }
//            }
//        });
// ... but for now we will just use a Cucumber @After hook

        // Any 'Background' (which is not a scenario in itself will get run after the code in this @Before class

        isRunning = true;

    }

    // Setting a high order so this runs last
    @After(order=1)
    public void afterRunningScenario(Scenario scenario) throws Exception {

        String outputLine;
        if (scenario.isFailed()){
            outputLine = "failed";
            // Always takes screenshot when a scenario fails
            TestFramework.takeScreenshots = true;
            TestFramework.getCurrentUser().testRunner.takeAScreenshotHandler(false);
        } else {
            outputLine = "passed";
            // Delete the screenshots failed if the scenario passed and he user hasn't asked for them
            if (!TestFramework.takeScreenshots) {
                TestFramework.deleteScreenshotsFolder(false);
            }
        }
        outputLine = "Test outcome: " + outputLine;
        TestFramework.addLineToLog(outputLine);
        TestFramework.outputLogs();
        TestFramework.teardownTheTestFramework(isRunning);
        isRunning = false;
    }

    // We could take screenshots after every step!? - or at least after every scenario!
    // See: https://github.com/cucumber/cucumber-jvm/pull/838 Add @BeforeStep and @AfterStep annotations

    @Given("^The test framework gets preconfigured before every scenario$")
    public static void iHaveConfiguredTheTestFramework() throws Exception {
        assertTrue("The test framework is preconfigured  [" + isRunning + "]", isRunning);
    }

    @Then("^the test framework is not running$")
    public void theTestFrameworkIsNotRunning(){
        assertFalse("The test framework is not running [" + isRunning + "]", isRunning);
    }

    @Then("^the test framework is running$")
    public void theTestFrameworkIsRunning(){
        assertTrue("The test framework is running [" + isRunning + "]", isRunning);
    }

    @Then("^I can run a basic test$")
	public void iCanRunABasicTestFrameworkTest() {
		assertTrue("True is true", true);
	}

}