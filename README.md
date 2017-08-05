https://gist.github.com/jxson/1784669
https://help.github.com/articles/basic-writing-and-formatting-syntax/

## Synopsis

This project is an implementation of an automation challenge.

The framework used has been designed to work primarily with Cucumber/BDD approaches where features/scenarios are in actor based, implementation agnostic declarative steps, though it has also been used successfully within mock-BDD java only based approaches and non-BDD jUnit type tests. 

It aims to provide clear differentiation between requirement specification, the steps ro actions to execute the tests, and the low-level automation code to control any particular type of application, such as a web site.

This way, the highest level of collaboration and participation can be fostered between BA's, manual tester, automation testers and developers.

Great emphasis is given to clarity of what is being tested, traceability, ease of debugging, reuse and reductionin manual test effort, while still preparing the ground for running suites of tests on a CI server, possibly as part of a DevOps environment. Tests can be run locally against any target environment with minimal reconfiguration.

## Installation

On a windows PC, download the project as a zip file and extract it to the folder: C:\WIP\ValtechQAAutomationChallenge, or clone the project into that directory.

Severals paths are hard coded as an initial implementation so it is not possible to run the code from just any directory, or a non-windows based computer, without additional configuration.

Two batch files are provided to help run the tests with little of no extra configuration.

Ansi166 is free utility which allows batch file to display coloured output. If you don't already have it installed, this can be installed by running the batch file:

>C:\WIP\ValtechQAAutomationChallenge\InstallAnsicon.bat

The test project uses maven to run Cucumber based tests. To run all the tests with full logging/debugging options enabled, run the batch file:

>C:\WIP\ValtechQAAutomationChallenge\RunFullTestSuite.bat

The first time this batch file is run, the bundled Maven package will download all necessary jar files, which may take a few minutes, to the folder:

>C:\WIP\MavenRepository

The the 'Project' subfolder is set up as a full Intellij/POM proejct, so users may will to expore running tests from within any compatible IDE, but no extra instruciton for this is given here.

## Tests

The tests implemented are described in a single Cucumber/Gherkin feature file which can be seen under:

>C:\WIP\ValtechQAAutomationChallenge\Project\features\Website\Website.feature

This can also be seen [here] (https://github.com/GaleForceTechnology/ValtechQAAutomationChallenge/blob/master/Project/features/Website/Website.feature)

```
Feature: Website

  The Valtech website at the URL: http://www.valtech.com/

  Background:
    Given I define a user called "The Website User"
      And The Website User visits the Valtech website

  Scenario: Verify that the Valtech website is displaying
    Then The Website User can verify that the Valtech website is displayed

  Scenario: Verify that the 'Latest News' section is displaying
    Then The Website User can verify that the Latest News section is displayed

  Scenario Outline: Verify that the ABOUT, SERVICES and WORK pages' each display the relevant page name
    When The Website User navigates to the <PageName> Page
    Then The Website User can verify that the <PageName> Page is displayed

    Examples:
      | PageName |
      | About    |
      | Services |
      | Work     |

  Scenario: Count the number of Valtech offices
    When The Website User navigates to the Contact Page
     And The Website User verifies that the Contact Page is displayed
    Then The Website User can count the total number of Valtech offices
```

The output fromm running the full test suite locally can be found at:

>C:\WIP\ValtechQAAutomationChallenge\Output\FullTestSuite

The standard Cucumber output will be available here:
>C:\WIP\ValtechQAAutomationChallenge\Output\FullTestSuite\index.html

A heierarchical set of sub folders within this top level output follows the structure of the feature file, starting at:

>C:\WIP\ValtechQAAutomationChallenge\Output\FullTestSuite\001-Feature-Valtech-QAAutomationChallenge

All logging is enable in the provided full test suite batch file, and each folder output represents a single scenarios and includes:

>A log file, TestLog.txt
>A video recording
>A screenshots subfolder containing all screenshots

The options to highlight on-screen elements as they are actioned is enabled, as is the option to display on-screen annotations of the actions/checks being executed.

Screenshots are taken at each action, so some screens may show little change from the previous if there is only some background action being taken.

## Outstanding Framework Issues

Further enhancements to the framework used in these sample tests are planned/in progress to better facilitate multithreaded test runs, and to reduce the number of class instances generated.

## License

Available under a GNU AGPL v3 licence
