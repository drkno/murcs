@Manual
Feature: Undo or redo a change

  Background:
    Given the side panel list is shown
    And an item is selected in the side panel list
    And it is available for editing in the right pane

  Scenario: Undo/Redo menu items
    Given I have made changes to a field
    When I click away from the field
    And the changes are saved
    Then the undo and redo menu items are updated

  Scenario: Undo a change
    Given the undo menu item is available
    When I click undo in the menu
    Then the last change is undone

  Scenario: Redo a change
    Given the redo menu item is available
    When I click redo in the menu
    Then the last change is redone

  Scenario: Delete an item
    Given I click an item in the side panel list
    When I click the delete button
    Then the undo and redo menu items are updated