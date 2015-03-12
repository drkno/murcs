Feature: Show or hide the side panel list

  Scenario: Show side panel list
    Given the side panel is hidden
    When I click the View menu
    And I click the Show/Hide Item list button
    Then the side panel shows

  Scenario: Hide the side panel list
    Given the side panel list is shown
    When I click the View menu
    And I click the Show/Hide Item list button
    Then the side panel hides
