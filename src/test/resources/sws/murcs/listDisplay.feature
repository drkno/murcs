@WIP
  Feature: ListDisplay

    Scenario: Change the list display type, update list items
      When I change the list display type
      Then the list items repopulate with that type
      And the first item is selected

    Scenario: Select item from list display
      When I select an item from the list display
      Then details of the select item are shown

    Scenario: Edit item
      Given I have selected an item from the list display
      When I edit the items short name
      Then the short name is updated in the list display