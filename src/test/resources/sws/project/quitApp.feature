@Manual
Feature: Close the app

  Scenario: Close the app using file menu
    Given I am at the main app window
    When I click the file button in the menu
    And I Click the quit option
    Then the app closes

  Scenario: Close the app using 'X' button
    When I click the 'X' button
    Then the app closes