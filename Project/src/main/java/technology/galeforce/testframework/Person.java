package technology.galeforce.testframework;

/**
 * Created by Peter Gale on 14/10/2016.
 */

public class Person {

    private String name;

    // Constructor - person with no defined name
    public Person () throws Exception {
        constructorCommon("the user");
    }

    // Constructor - person with name passed from step definitions

    public TestRunner testRunner;

    public Person (String userName) throws Exception {
        constructorCommon(userName);
    }

    private void constructorCommon(String userName) throws Exception {
        testRunner = new TestRunner(this);
        this.setName(userName);
    }

    public void tearDown() {
        testRunner=null;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    public String getName() {
        return this.name;
    }


}

