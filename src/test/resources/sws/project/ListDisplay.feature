@Manual
  Feature: ListDisplay

    Scenario: Change the list display type, update list items
      When I change the list display type
      Then the list items repopulate with that type

    Scenario: Select item from list display
      When I select an item from the list display
      Then details of the select item are shown