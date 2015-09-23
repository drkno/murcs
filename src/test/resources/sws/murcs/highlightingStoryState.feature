# Manuel: [after Manuel loses Basil's money by "knowing nothing"] See, I know nothing!
# Basil Fawlty: I'm gonna sell you to a vivisectionist!
@Manual
Feature: Highlighting Story States

  Scenario: Toggle story highlight
    When I select "Highlight Stories" from the view-menu
    Then the menu item is ticked
    And stories will now be highlighted depending on their status

  Scenario: Highlight Colors
    When Highlighting is turned on
    And I am viewing a backlog
    Then stories that are ready will be given green highlights
    And stories that have not been estimated but could be will be given green highlights
    And stories that depend on lower priority stories will be given red highlights
