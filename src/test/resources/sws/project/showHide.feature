Feature: Show or hide the side panel list

  Scenario: Show side panel list
    Given The side panel is hidden
    When I click the View menu
    And I click the Show/Hide Item list button
    Then The side panel shows

  Scenario: Hide the side panel list
    Given The side panel list is shown
    When I click the View menu
    And I click the Show/Hide Item list button
    Then The the side panel hides
