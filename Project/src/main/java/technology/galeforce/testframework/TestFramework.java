package technology.galeforce.testframework;

import technology.galeforce.testframework.screenrecorder.AVIScreenRecorder;
import technology.galeforce.testframework.web.BaseWebPage;
import technology.galeforce.testframework.web.Browser;
import technology.galeforce.testframework.web.BrowserFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Peter Gale on 13/10/2016.
 *
 * We are using TestFramework as a static object, where all the members are declared static.
 * See: https://www.quora.com/What-is-the-purpose-of-a-static-object-in-Java-When-is-it-actually-used-or-in-which-context
 * This makes the TestFramework object available globally without needing any class instantiation
 *
 */

public class TestFramework {

//    public static String currentScenarioName;
//    public static int scenariosRunInThisSessionCounter = 0;
//    public static int currentConcreteSubScenarioInstanceNumber;

    private static String systemUserName = "TestFramework";

    public static List<Person> allUsers;
    private static Person currentUser;
    public static EnvironmentProperties environmentProperties = new EnvironmentProperties();

    // See: https://www.mkyong.com/java/java-properties-file-examples/
    public static Properties properties = new Properties(); // Populated from the command line options set

    public static boolean runningUnderJenkins;
    private static String testRunRootDirectory;
    private static String currentScenarioFullName;
    private static String currentScenarioRootDirectory;
    private static String currentScenarioLogFileContents;
    private static String allScenariosLogFileContents;

    private static String screenshotDirectory;
    public static boolean highlightElements;
    public static boolean showProgressNotifications;
    public static boolean recordAVideo;

    public static String getSystemUserName() {
        return systemUserName;
    }

    public static String getTestRunRootDirectory() {
        return testRunRootDirectory;
    }

    public static String getCurrentScenarioFullName() {
        return currentScenarioFullName;
    }

    public static String getCurrentScenarioRootDirectory() {
        return currentScenarioRootDirectory;
    }

    public static Person getCurrentUser() {
        return currentUser;
    }

    // Screenshots will be taken whenever a message is displayed or when a web element is highlighted
    public static boolean takeScreenshots;
    // We want to share the same sequence of screenshot numbers across all users and browser instances in the same test run?
    public static int screenshotNumber;

    public static String getScreenshotDirectory() {
        return screenshotDirectory;
    }

    public static void configureTheTestFramework(
        String currentFeatureName,
        int currentFeatureInstanceNumber,
        String currentScenarioName,
        int currentScenarioInFeatureInstanceNumber,
        int currentConcreteSubScenarioInFeatureInstanceNumber) throws Exception {

        // Don't initialise the framework if we've done it once already!
//        if (!isRunning) {

            allUsers = new ArrayList<Person>();
            allUsers.add(new Person(systemUserName));

            loadEnvironmentConfigurationPropertiesFilePassedByNameInCommandLine();
            loadExternalConfigurationPropertiesFilePassedByNameInCommandLine();

//            String testA reRunningUnderJenkins = getSystemOrEnvironmentProperty("runningUnderJenkins");
//            if (testAreRunningUnderJenkins == null ) {
//                runningUnderJenkins = false;
//            } else if (testAreRunningUnderJenkins.equals("true")) {
//                runningUnderJenkins = true;
//            } else {
//                runningUnderJenkins = false;
//            }

        testRunRootDirectory = getASystemOrEnvironmentProperty("testRunRootDirectory");
            if (testRunRootDirectory == null || "".equals(testRunRootDirectory)) {
                testRunRootDirectory = "C:\\WIP";
//                if (runningUnderJenkins) {
//                    testRunRootDirectory = testRunRootDirectory + "\\Jenkins";
//                }
                testRunRootDirectory = testRunRootDirectory + "\\Output";
            }

// The root output folder should always be a unique new number, so we shouldn't have to worry about recreating it
// .... doing so seems to conflict with Cucumber recreating a new output report.js file
//            if ((currentFeatureInstanceNumber == 1)  && (currentScenarioInFeatureInstanceNumber == 1)) {
////                forceDeleteAFolderAndPossiblyRecreate(testRunRootDirectory, false);
//                File folder = new File(testRunRootDirectory);
//                if (folder.exists()) {
//                    try {FileUtils.forceDelete(folder);} catch (FileNotFoundException ex) {}
//// Wait until the folder exists before proceeding?
//while (folder.exists()) {
//    Thread.sleep(100);
//}
//                }
//            }

// Don't need this level here now???'
//            testRunRootDirectory = testRunRootDirectory + "\\Output";
//            // Make sure the this part of the target output directory exists, and over-rites any previous one, if this is the start of a new session (ie. first scenario in first feature)

            currentScenarioFullName = String.format("%03d", currentFeatureInstanceNumber) + "-Feature-" + currentFeatureName;
//            // Make sure the this part of the target output directory exists
//            (new File(testRunRootDirectory)).mkdir();

            String scenarioTypeName = null;
            if (currentConcreteSubScenarioInFeatureInstanceNumber == 0) {
                scenarioTypeName = "Scenario";
            } else {
                scenarioTypeName = "ScenarioOutline";
            }
            currentScenarioFullName = currentScenarioFullName + "\\" + String.format("%03d", currentScenarioInFeatureInstanceNumber) + "-" + scenarioTypeName + "-" + currentScenarioName;
//            // Make sure the this part of the target output directory exists
//            (new File(testRunRootDirectory)).mkdir();

            // Only crete the next level of folder details for scenarios outlines
            if (!(currentConcreteSubScenarioInFeatureInstanceNumber == 0)) {
                currentScenarioFullName = currentScenarioFullName + "\\Example-" + String.format("%03d", currentConcreteSubScenarioInFeatureInstanceNumber);
            }
//            // Make sure the this part of the target output directory exists
//            (new File(testRunRootDirectory)).mkdir();

            // Replace any characters that the windows filesystem treats as illegal
            currentScenarioRootDirectory = testRunRootDirectory + "\\" + currentScenarioFullName.replace("?","_");

            // Make sure all folders exist down to this level
            // See: http://stackoverflow.com/questions/2833853/create-whole-path-automatically-when-writing-to-a-new-file
            Path currentScenarioRootDirectoryPath= Paths.get(currentScenarioRootDirectory);
            Files.createDirectories(currentScenarioRootDirectoryPath);
//// Wait until the folder exists before proceeding?
//File folder = new File(testRunRootDirectory);
//while (!(folder.exists() || !folder.isDirectory())) {
//    Thread.sleep(100);
//}

            currentScenarioLogFileContents = "";

            String highlightElementsAsWeUseThem = getASystemOrEnvironmentProperty("highlightElementsAsWeUseThem");
            if (highlightElementsAsWeUseThem == null ) {
                highlightElements = false;
            } else if (highlightElementsAsWeUseThem.equals("true")) {
                highlightElements = true;
            } else {
                highlightElements = false;
            }

            String checkShowProgressNotifications = getASystemOrEnvironmentProperty("showProgressNotifications");
            if (checkShowProgressNotifications == null ) {
                showProgressNotifications = false;
            } else if (checkShowProgressNotifications.equals("true")) {
                showProgressNotifications = true;
            } else {
                showProgressNotifications = false;
            }

            String userWantsToTakeScreenshots = getASystemOrEnvironmentProperty("takeScreenshots");
            if (userWantsToTakeScreenshots == null ) {
                takeScreenshots = false;
            } else if (userWantsToTakeScreenshots.equals("true")) {
                takeScreenshots = true;
            } else {
                takeScreenshots = false;
            }

            // Give each scenario it's own timestamped screenshots folder so we can isolate failures
            screenshotDirectory = currentScenarioRootDirectory + "\\screenshots";
            (new File(screenshotDirectory)).mkdir();

// Taking the GUID on the screenshots folder out - this is created locally at a higher level for tests run via the TestRunControl spreadsheet
// Test run from code will always overwrite the previous screenshots - removign an need for housekeeping!
//            // Colons not allowed in windows folder names - but need to leave the one for any drive letter, e.g. C:...
//            screenshotDirectory = screenshotDirectory + "\\" + TestFramework.getAGuid().replace(":", "-");

            // Always delete any old screenshots folder (and it's contents), if it exists
            // Always recreate the folder even if we are NOT taking fresh screenshots, as we will be gett a screenshot for each scenario started, and for any failures
            // The folder will be deleted if the scenario passes?
            forceDeleteAFolderAndPossiblyRecreate(screenshotDirectory, true);
            // Initialise the screenshots counter
            screenshotNumber = 0;

            String userWantsToRecordAVideo = getASystemOrEnvironmentProperty("recordAVideo");
            if (userWantsToRecordAVideo == null ) {
                recordAVideo = false;
            } else if (userWantsToRecordAVideo.equals("true")) {
                recordAVideo = true;
            } else {
                recordAVideo = false;
            }
            if (recordAVideo) {
                AVIScreenRecorder.startScreenCast(currentScenarioRootDirectory);
            }

        // Define a system user
//        iDefineAUserCalled(TestFramework.getSystemUserName());
            Person testFrameworkUser = TestFramework.getUser(TestFramework.getSystemUserName());
            testFrameworkUser.testRunner.openNewWebBrowserUserSession("Test Framework Administration", false);
//        String root = TestFramework.getTestRunRootDirectory();

            String frameworkClientApplicationName = TestFramework.getAProperty(EnvironmentProperties.FRAMEWORK_CLIENT_APPLICATION_NAME);
            String html;
            html = "<html>";
            html = html + "    <head>\\n";
            html = html + "        <meta http-equiv='Content-Type' content='text/html; charset=windows-1252'>\\n";
            html = html + "    </head>";
//            html = html + "    <body background='https://www.planwallpaper.com/static/images/old-paper-floral-parchment-background-texture_wunZAKZ.jpg'>\\n";
//            html = html + "    <body background='https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX3423586.jpg'>\\n";
//            html = html + "    <body background='https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX3423586.jpg'>\\n";
            //html = html + "    <body background='https://previews.123rf.com/images/lehui/lehui1108/lehui110800050/10383853-antiques-monotint-Clapper-board-Stock-Photo-movie-clapperboard.jpg'>\\n";
            html = html + "    <body background='https://previews.123rf.com/images/kobets/kobets1002/kobets100200020/6507888-clap-board-and-three-film-strips-on-abstract-dark-background-Stock-Photo.jpg'>\\n";
            html = html + "        <center>\\n";
            html = html + "        <br\\>";
            html = html + "        <br\\>";
            html = html + "        <br\\>";
            html = html + "        <br\\>";
            html = html + "        <br\\>";
            html = html + "        <br\\>";
            html = html + "        <br\\>";
            html = html + "        <br\\>";
            html = html + "        <br\\>";
            html = html + "        <h1>" + frameworkClientApplicationName + "</h1>\\n";
            html = html + "        <h2>Test Automation By GaleForce Technology</h2>\\n";
            html = html + "        <h3>Starting up ...</h3>\\n";
            html = html + "        <div id='tagNames'></div>\\n";
            html = html + "        </center>\\n";
            html = html + "    </body>\\n";
            html = html + "</html>";
            TestFramework.getUser(TestFramework.getSystemUserName()).testRunner.currentUserSession.currentWebBrowser.getElement(By.xpath("html"), false).setProperty("innerHTML", html);

//            isRunning = true;

//        }

    }

    public static void deleteScreenshotsFolder(boolean recreate) throws Exception {
        forceDeleteAFolderAndPossiblyRecreate(screenshotDirectory, recreate);
    }

    private static void forceDeleteAFolderAndPossiblyRecreate (String path, boolean recreate) throws Exception {
        File directory = new File(path);
        try {FileUtils.forceDelete(directory);} catch (FileNotFoundException ex) {}
        if (recreate) {
            directory.mkdir();
        }
    }
    public static void teardownTheTestFramework(boolean isRunning) {
        if (recordAVideo) {
            AVIScreenRecorder.stopScreenCast();
        }
        clearDownAllUsers(isRunning);
        clearOutTempDirectory();
    }

    private static void clearDownAllUsers(boolean isRunning) {
        // Clear down all users
        for (Person aUser : allUsers) {
            aUser.testRunner.tearDown();
            aUser=null; // No need to remove the user from the list as we will be destroying the list itself
        }
        allUsers = null;
        isRunning = false;
        BrowserFactory.stopSeleniumGridHUBServer();
    }

    private static void clearOutTempDirectory() {
        String tempDirectory = System.getenv("TEMP");
        //listAllEnvironmentVariables();

        File path = new File(tempDirectory);
        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++){
            if (files[i].isDirectory()) {
                if (files[i].getName().indexOf("scoped_") == 0) {
                    //System.out.println("Directory: " + files[i].getName());
                    try {
                        FileUtils.deleteDirectory(files[i]);
                    } catch (Exception ex) {
                        // Ignore any errors!
                    }
                }
            }
            if (files[i].isFile()) {
                if (files[i].getName().indexOf("screenshot") == 0) {
                    try {
                        files[i].delete();
                    } catch (Exception ex) {
                        // Ignore any errors!
                    }
                }

            }


//            if (files[i].isFile()){ //this line weeds out other directories/folders
//                System.out.println(files[i]);
//            }
        }
    }


    public static Person getUser(String targetUserName) throws Exception {

        Person matchingUser = null;
        boolean userFound = false;

        // We use a '#' at the end of the user name to indicate a session number
        int startOfSessionNumber = targetUserName.indexOf("#");
        if (startOfSessionNumber > 0) {
            int sessionNumber = Integer.parseInt(targetUserName.substring(startOfSessionNumber + 1));
            targetUserName = targetUserName.substring(0, startOfSessionNumber);
            Person currentUser =  getUser(targetUserName);
            currentUser.testRunner.setSessionByNumber(sessionNumber);
        }

        // We use a '!' at the end of the user name to indicate a browser window number
        int startOfWindowNumber = targetUserName.indexOf("!");
        if (startOfWindowNumber > 0) {
            int windowNumber = Integer.parseInt(targetUserName.substring(startOfWindowNumber + 1));
            targetUserName = targetUserName.substring(0, startOfWindowNumber);
            Person currentUser =  getUser(targetUserName);
            currentUser.testRunner.setCurrentWindowByNumber(windowNumber);
        }

        for (Person aUser: allUsers) {
            if (targetUserName.equals(aUser.getName())) {
                matchingUser = aUser;
                userFound = true;
            }
        }
        if (userFound) {
            currentUser = matchingUser;
            return matchingUser;
        } else {
            throw new Exception("User '" + targetUserName + "' is not found in the list of all defined users.");
        }

    }

    public static BaseWebPage getUsersCurrentWebPage(String targetUserName) throws Exception {
        // We use a '#' at the end of the user name to indicate a session number
        Browser webBrowser = getUser(targetUserName).testRunner.currentUserSession.currentWebBrowser;
        webBrowser.activate();
        return webBrowser.getCurrentWebPage();
    }

    private static void loadEnvironmentConfigurationPropertiesFilePassedByNameInCommandLine() throws Exception {
        loadAPropertiesFilePassedByNameInCommandLine("environment.configuration");
    }

    private static void loadExternalConfigurationPropertiesFilePassedByNameInCommandLine() throws Exception {
        loadAPropertiesFilePassedByNameInCommandLine("external.configuration");
    }

    private static void loadAPropertiesFilePassedByNameInCommandLine(String cliFileName) throws Exception {
        // Get all environment specific properties from a file whose name is either:
        // 1. set in the VM options in IntelliJ runtime config:
        //             -Dexternal.configuration=C:\WIP\current.test.properties
        // 2. Passed on the maven command line:
        //              C:\WIP\CFHDocmailAutomation\Project>mvn -Denvironment.configuration=environment.test.properties clean test
        // For now we will not handle any "file not found" type of errors
        String sourcePropertiesFilePath = System.getProperty(cliFileName);
        properties = loadAPropertiesFile(cliFileName, sourcePropertiesFilePath, properties);
    }

    public static Properties loadAPropertiesFile(String sourcePropertiesFileName, String sourcePropertiesFilePath, Properties propertiesToAddTo) throws Exception {
        if (!(sourcePropertiesFilePath == null)) {
            File file = new File(sourcePropertiesFilePath);
            if(!(file.exists() && !file.isDirectory())) {
                throw new Exception("Cannot find the '" + sourcePropertiesFileName + "' file: '" + sourcePropertiesFilePath + "'");
            }
            InputStream inputStream = new FileInputStream(sourcePropertiesFilePath);
            // We may want to ADD to any properties set elsewhere
            if (propertiesToAddTo == null) {
                propertiesToAddTo = new Properties();
            }
            propertiesToAddTo.load(inputStream);
            inputStream.close();
        }
        return propertiesToAddTo;
    }

//    private static String getAPropertyFromAnExternalPropertiesFile(String sourcePropertiesFileName, String sourcePropertiesFilePath, String propertyName) throws Exception {
//        Properties tempPropertyFile = loadAPropertiesFile(sourcePropertiesFileName, sourcePropertiesFilePath, null);
//        String propertyValue = tempPropertyFile.getProperty(propertyName);
//        return propertyValue;
//    }

    public static String getAProperty(String propertyName) {
        String propertyValue = properties.getProperty(propertyName);
        return propertyValue;
    }

    public static String getAnEnvironmentProperty(String propertyName) {
        String environmentProperty = System.getenv().get(propertyName);
        return environmentProperty;
    }

    public static String getASystemOrEnvironmentProperty(String propertyName) {
        // Some parameters we may want to pass as either an environment variable (Property) or system property
        // Get system Property
        String systemProperty = System.getProperty(propertyName);
        String environmentProperty= System.getenv().get(propertyName);
        if (!(systemProperty == null)) {
            return systemProperty;
        } else {
            return environmentProperty;
        }
    }

    public static void listAllproperties() {
        properties.list(System.out);
    }

    public static void listAllEnvironmentVariables() {
        System.out.println("LISTING ENVIRONMENT VARIABLES");
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n",
                    envName,
                    env.get(envName));
        }
    }

    public static String getAGuid() {
        // https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-kk:mm:ss:SSS");
        return format.format(curDate);
    }

    public static void addLineToLog(String logLineText) throws IOException {
        // See: http://stackoverflow.com/questions/2885173/how-do-i-create-a-file-and-write-to-it-in-java
        // System.getProperty("line.separator") ??
//        FileUtils.writeStringToFile(new File(currentScenarioRootDirectory + "\\TestLog.txt"), logText + "\r\n", "utf-8");
        currentScenarioLogFileContents = currentScenarioLogFileContents + logLineText + "\r\n";
    }
//    private static String logCurrentScenarioLogFileContents = "";
//    private static String logAllScenariosLogFileContents = "";

    public static void outputLogs() throws IOException {
        // See: http://stackoverflow.com/questions/2885173/how-do-i-create-a-file-and-write-to-it-in-java
        // System.getProperty("line.separator") ??
        // NOTE: All logs overwrite the previous copy when using FileUtils!
        FileUtils.writeStringToFile(new File(currentScenarioRootDirectory + "\\TestLog.txt"), currentScenarioLogFileContents, "utf-8");
        if (!"".equals(allScenariosLogFileContents) && allScenariosLogFileContents!= null) {
            allScenariosLogFileContents = allScenariosLogFileContents + "\r\n" + "\r\n"  + currentScenarioLogFileContents;
        } else {
            allScenariosLogFileContents = currentScenarioLogFileContents;
        }
        FileUtils.writeStringToFile(new File(testRunRootDirectory + "\\TestLog.txt"), allScenariosLogFileContents, "utf-8");
    }

}