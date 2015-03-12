Feature: Close the app

  @Manual
  Scenario: Close the app using file menu
    Given I am at the main app window
    When I click the file button in the menu
    And Click the quit option
    Then The app closes

  @Manual
  Scenario: Close the app using 'X' button
    When I click the 'X' button
    Then The app closes