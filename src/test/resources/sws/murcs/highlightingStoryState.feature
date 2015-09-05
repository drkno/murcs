# Manuel: [after Manuel loses Basil's money by "knowing nothing"] See, I know nothing!
# Basil Fawlty: I'm gonna sell you to a vivisectionist!
@Manual
Feature: Highlighting Story States

  Scenario: Toggle story highlight
    When I select "Highlight Stories" from the view-menu
    Then the menu item is ticked
    And stories will now be highlighted depending on their status