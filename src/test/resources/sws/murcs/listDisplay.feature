@Manual
@ListDisplay
Feature: List Display

    Scenario: Change the list display type, update list items
      When I change the list display type
      Then the list items repopulate with that type
      And the first item is selected

  Scenario: Opening list item in a new tab
    When I CTRL + Click on an item in the display list
    Then the item is opened in a new tab

    Scenario: Select item from list display
      When I select an item from the list display
      Then details of the select item are shown

    Scenario: Edit item
      Given I have selected an item from the list display
      When I edit the items short name
      Then the short name is updated in the list display

    Scenario: Sorted list
      Given there are multiple items in the list display
      Then the list is sorted alphabetically

    Scenario: Edit item in sorted list
      Given there are multiple items in the list display
      And I have selected an item from the list display
      When I prefix the items short name
      Then the list is sorted alphabetically
