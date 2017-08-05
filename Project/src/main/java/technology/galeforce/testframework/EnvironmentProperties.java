package technology.galeforce.testframework;

import java.util.Properties;

/**
 * Created by peter.gale on 19/10/2016.
 * This class holds the names we expect in an external environment properties file
 * Create a java class for constants- See: http://www.javapractices.com/topic/TopicAction.do?Id=2
 */

public class EnvironmentProperties {

    public static final String FRAMEWORK_CLIENT_APPLICATION_NAME ="FrameworkClientApplicationName";
    private static final String replaceableUserName="_USERNAME_";
    private static final String REGISTERED_USER_LOGINUSERID ="Registered.User._USERNAME_.LoginUserID";
    private static final String REGISTERED_USER_LOGINPASSWORD ="Registered.User._USERNAME_.LoginPassword";

    public static String REGISTERED_USER_LOGINUSERID(String userName) {
        String textToReturn = REGISTERED_USER_LOGINUSERID.replaceAll(replaceableUserName,userName);
        return textToReturn;
    }

    public static String REGISTERED_USER_LOGINPASSWORD(String userName) {
        String textToReturn = REGISTERED_USER_LOGINPASSWORD.replaceAll(replaceableUserName,userName);
        return textToReturn;
    }

    public static void addAPropertyToAPersonsTestRunner(String userName, String propertyKey, String propertyValue) throws Exception {
        TestFramework.getUser(userName).testRunner.currentUserSession.properties.put(propertyKey, propertyValue);
    }

    public static String getAPropertyFromAPersonsTestRunner(String userName, String propertyKey) throws Exception {
        return TestFramework.getUser(userName).testRunner.currentUserSession.properties.getProperty(propertyKey);
    }

    public static void listAllProperties(Properties props) {
        outputAListOfProperties(props);
    }

    public static void listAllSystemProperties() {
        outputAListOfProperties(System.getProperties());
    }

    public static void outputAListOfProperties(Properties props) {
        props.list(System.out);
        System.out.println("--- end ---");
    }

}
