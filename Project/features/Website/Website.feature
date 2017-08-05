@FeatureName-Valtech-QAAutomationChallenge
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