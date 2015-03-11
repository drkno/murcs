Feature: Close the app


  Scenario: Close the app using file menu
    When I click the file button in the menu
    And Click the quit option
    Then The app closes

  Scenario: Close the app using 'X' button
    When I click the 'X' button
    Then The app closes