@Manual
Feature: Highlighting Story States

  Scenario: Toggle story highlight
    When I select "Highlight Stories" from the view-menu
    Then the menu item is ticked
    And stories will now be highlighted depending on their status