@Manual
Feature: Highlighting Story States

  Scenario: Disabled view-menu item
    Given I am not viewing a backlog
    Then the "Highlight Stories" view-menu item is disabled

  Scenario: Toggle story highlight
    When I select "Highlight Stories" from the view-menu
    Then the menu item is ticked
    And stories will now be highlighted depending on their status